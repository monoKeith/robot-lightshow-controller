import com.cyberbotics.webots.controller.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BotControl {
    private enum MovementStates{
        IDLE,
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
    private final Location location;
    // State
    private MovementStates movementState;
    // Transmitter
    private Transmitter transmitter;

    // Report location only once upon arrival, not multiple times
    private boolean locationReported;

    // Enable Logging
    public static final boolean LOG_ENABLE = false;

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
        rgb.set(0xffffff);
        // Location
        location = new Location(robot);
        // Target
        location.setTarget(-0.25,-0.25);
        // State
        movementState = MovementStates.IDLE;
        // Flag
        locationReported = false;
        // Default velocity
        resetVelocity();

        // Power on pulse
        location.update();
        try {
            transmitter.powerOnMessage(location);
        } catch (MqttException e) {
            e.printStackTrace();
        }

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

            String[] commands = originalMsg.split("\\|");

            for (String command: commands){
                if (command.isEmpty()) continue;
                String[] msg = command.split(" ");
                switch (msg[0]){
                    case "TARGET" -> {
                        // TARGET X Y
                        double x = Double.parseDouble(msg[1]);
                        double y = Double.parseDouble(msg[2]);
                        location.setTarget(x, y);
                        movementState = MovementStates.STOP;
                        locationReported = false;
                    }
                    case "COLOR" -> {
                        // COLOR <integer-color-code-256^3>
                        int val = Integer.parseInt(msg[1]);
                        rgb.set(val);
                    }
                    case "UUID" -> {
                        // Report UUID
                        try {
                            transmitter.powerOnMessage(location);
                        } catch (MqttException e) {
                            System.out.println("Failed to report UUID!");
                            e.printStackTrace();
                        }
                    }
                    default -> System.out.println("Unknown command: " + originalMsg);
                }
            }
        }
        System.out.println("Connection thread terminated");
    }

    // Update robot status according to sensors readings
    // Mostly corresponds to movements of the robot
    public void controlThread(){
        location.update();
        boolean arrived = location.arrived();

        if (LOG_ENABLE) {
            location.log();
            System.out.println("arrived?" + arrived);
            System.out.println("State: " + movementState.toString());
        }

        // Update state according to current state
        switch (movementState) {
            case IDLE -> {
                // Do nothing
            }
            case STOP -> {
                // Check position
                if (!arrived) {
                    // Update state according to alignment
                    movementState = location.noNeedToCurve() ? MovementStates.FORWARD : MovementStates.CURVE;
                    movementState = location.noNeedToSpin() ? movementState : MovementStates.ROTATE;
                }
            }
            case ROTATE -> {
                if (location.noNeedToSpin()) {
                    movementState = MovementStates.FORWARD;
                }
            }
            case CURVE, FORWARD -> {
                if (arrived) {
                    movementState = MovementStates.STOP;
                } else if (location.noNeedToSpin()) {
                    movementState = location.noNeedToCurve() ? MovementStates.FORWARD : MovementStates.CURVE;
                } else {
                    movementState = MovementStates.ROTATE;
                }
            }
            // Arrived
            // Not arrived yet, update state according to alignment
            default -> {
                System.out.println(
                        String.format("Unknown state: %s, abort.", movementState));
                return;
            }
        }

        // Action
        switch (movementState) {
            case STOP -> arrived();
            case ROTATE -> spin();
            case CURVE -> curve();
            case FORWARD -> forward();
        }

    }

    // Set wheel velocity to default value
    private void resetVelocity(){
        leftWheel.setVelocity(defaultV);
        rightWheel.setVelocity(defaultV);
    }

    // Adjust speed of motor according target direction
    // Sensitivity - One side reach stop when angle offset is 45 degrees
    private void curve(){
        resetVelocity();

//        double distanceToGo = location.getDistance() * moveConst;
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();

        double speedOffset = (location.directionDiff() / 45) * defaultV;
        speedOffset = max(-defaultV, speedOffset);
        speedOffset = min(speedOffset, defaultV);

        leftWheel.setVelocity(defaultV - speedOffset);
        rightWheel.setVelocity(defaultV + speedOffset);
        // Workaround to not set position to infinite
        leftWheel.setPosition(curLPosition - speedOffset);
        rightWheel.setPosition(curRPosition + speedOffset);
    }

    // Spin robot to point to a target direction
    private void spin(){
        resetVelocity();
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double degreesToTurn = location.directionDiff() * spinConst;
        // Spin wheels in opposite direction
        leftWheel.setPosition(curLPosition - degreesToTurn);
        rightWheel.setPosition(curRPosition + degreesToTurn);
    }

    // Move forward, should be called only when aligned to target
    private void forward(){
        resetVelocity();
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double distanceToGo = location.getDistance() * moveConst;
        // Spin wheels in same direction
        leftWheel.setPosition(curLPosition + distanceToGo);
        rightWheel.setPosition(curRPosition + distanceToGo);
    }

    private void arrived(){
        // Stop robot
        resetVelocity();
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        leftWheel.setPosition(curLPosition);
        rightWheel.setPosition(curRPosition);
        // Only report once
        if (locationReported) return;
        if (LOG_ENABLE) System.out.println("Arrived.");
        try {
            transmitter.arrivalMessage(location);
            locationReported = true;
        } catch (MqttException e) {
            System.out.println("Failed to publish arrival message!");
            e.printStackTrace();
        }
    }

}
