import com.cyberbotics.webots.controller.*;
import org.eclipse.paho.client.mqttv3.MqttException;

public class BotControl {
    private enum MovementStates{
        STOP,
        CURVE,
        ROTATE,
        FORWARD
    }

    // Controller class for LightBot
    private static final String[] ledNames  = {"led1", "led2", "led3", "led4", "led5", "led6", "led7"};
    private static final String leftMotor   = "left wheel motor";
    private static final String rightMotor  = "right wheel motor";
    private static final String rgbName     = "ledrgb";
    private static final int timeStep       = 16;
    // sampleRateMs - Wheels position sensor sample rate (in ms)
    public static final int sampleRateMs    = timeStep;
    // spinConst - Number of position unit both wheels rotate (in opposite direction) to turn robot by 1 degree
    private static final double spinConst   = 0.1;
    // moveConst - Number of position unit both wheels rotate to move 1 meter
    private static final double moveConst   = 200;
    // defaultV - default velocity for wheels
    private static final double defaultV    = 60;
    // Robot
    private Robot robot;
    // Onboard devices
    private Motor leftWheel, rightWheel;
    private PositionSensor leftPosition, rightPosition;
    private LED[] leds;
    private LED rgb;
    // Location management
    private Location location;
    // State
    private MovementStates movementState;
    // Transmitter
    private Transmitter transmitter;

    // Report location only once upon arrival, not multiple times
    private boolean locationReported;

    // Enable Logging
    private final boolean LOG_ENABLE = false;

    public BotControl(Transmitter transmitter) {
        robot = new Robot();
        this.transmitter = transmitter;
        // Init motors
        leftWheel = robot.getMotor(leftMotor);
        rightWheel = robot.getMotor(rightMotor);
        leftPosition = leftWheel.getPositionSensor();
        rightPosition = rightWheel.getPositionSensor();
        leftPosition.enable(sampleRateMs);
        rightPosition.enable(sampleRateMs);
        // Init LEDs (RGB set to Yellow)
        leds = new LED[ledNames.length];
        int curLed = 0;
        for (String ledName: ledNames){
            leds[curLed++] = robot.getLED(ledName);
        }
        rgb = robot.getLED(rgbName);
        rgb.set(0xffff00);
        // Location
        location = new Location(robot);
        // Target
        location.setTarget(-0.25,-0.25);
        // State
        movementState = MovementStates.STOP;
        // Flag
        locationReported = false;
        // Default velocity
        resetSpeed();

        // Start connection handler thread
        new Thread(this::connectionThread).start();
    }

    // Stuck until time step
    public boolean waitTimeStep(){
        return robot.step(timeStep) != -1;
    }

    public void debug(){
        location.update();
        location.log();
    }

    // Update robot status according to remote control instructions
    // Require transmitter to be already set up
    private void connectionThread(){
        while(true){
            String originalMsg;

            // Receive message
            try {
                originalMsg = transmitter.waitForMsg();
                if (originalMsg == null) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            String[] msg = originalMsg.split(" ");

            switch (msg[0]){
                case "TARGET" -> {
                    // TARGET X Y
                    double x = Double.parseDouble(msg[1]);
                    double y = Double.parseDouble(msg[2]);
                    location.setTarget(x, y);
                    locationReported = false;
                }
                default -> System.out.println("Unknown instruction: " + originalMsg);
            }
        }
        System.out.println("Connection thread terminated");
    }

    // Update robot status according to sensors readings
    // Mostly corresponds to movements of the robot
    public void controlThread(){
        location.update();
        if (LOG_ENABLE){
            location.log();
            System.out.println("State: " + movementState.toString());
        }
        // Update state according to current state
        switch (movementState) {
            case STOP -> {
                // Check position
                if (!location.checkPosition()){
                    // Update state according to alignment
                    movementState = location.checkCurveAlignment() ? MovementStates.FORWARD : MovementStates.CURVE;
                    movementState = location.checkSpinAlignment() ? movementState : MovementStates.ROTATE;
                }
            }
            case ROTATE -> {
                if (location.checkSpinAlignment()){
                    movementState = MovementStates.FORWARD;
                }
            }
            case CURVE -> {
                if (location.checkPosition()){
                    movementState = MovementStates.STOP;
                } else if (location.checkSpinAlignment()){
                    movementState = location.checkCurveAlignment() ? MovementStates.FORWARD : MovementStates.CURVE;
                } else {
                    movementState = MovementStates.ROTATE;
                }
            }
            case FORWARD -> {
                if (location.checkPosition()){
                    // Arrived
                    movementState = MovementStates.STOP;
                } else {
                    // Not arrived yet, update state according to alignment
                    movementState = location.checkCurveAlignment() ? MovementStates.FORWARD : MovementStates.CURVE;
                }
            }
            default -> {
                System.out.println(
                        String.format("Unknown state: %s, abort.", movementState));
                return;
            }
        }

        // Action
        switch(movementState) {
            case STOP       -> arrived();
            case ROTATE     -> spin();
            case CURVE      -> curve();
            case FORWARD    -> forward();
        }
    }

    // Set wheel velocity to default value
    private void resetSpeed(){
        leftWheel.setVelocity(defaultV);
        rightWheel.setVelocity(defaultV);
    }

    // Adjust speed of motor according target direction
    // Sensitivity - One side reach stop when angle offset is 45 degrees
    private void curve(){
        double speedOffset = (location.directionDiff() / 45) * defaultV;
        leftWheel.setVelocity(defaultV - speedOffset);
        rightWheel.setVelocity(defaultV + speedOffset);
    }

    // Spin robot to point to a target direction
    private void spin(){
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double degreesToTurn = location.directionDiff() * spinConst;
        // Spin wheels in opposite direction
        resetSpeed();
        leftWheel.setPosition(curLPosition - degreesToTurn);
        rightWheel.setPosition(curRPosition + degreesToTurn);
    }

    // Move forward, should be called only when aligned to target
    private void forward(){
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double distanceToGo = location.getDistance() * moveConst;
        // Spin wheels in same direction
        resetSpeed();
        leftWheel.setPosition(curLPosition + distanceToGo);
        rightWheel.setPosition(curRPosition + distanceToGo);
    }

    private void arrived(){
        // Only report once
        if (locationReported) return;
        System.out.println("Arrived.");
        try {
            transmitter.reportLocation(location);
            locationReported = true;
        } catch (MqttException e) {
            System.out.println("Failed to publish arrival message!");
            e.printStackTrace();
        }
    }

}
