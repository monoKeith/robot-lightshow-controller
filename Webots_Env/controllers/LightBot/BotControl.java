import com.cyberbotics.webots.controller.*;

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
    private static final int timeStep       = 100;
    // sampleRateMs - Wheels position sensor sample rate (in ms)
    private static final int sampleRateMs   = 100;
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

    public BotControl() {
        robot = new Robot();
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
        // Default velocity
        resetSpeed();
    }

    // Stuck until time step
    public boolean waitTimeStep(){
        return robot.step(timeStep) != -1;
    }

    public void debug(){
        location.update();
        location.log();
    }

    // Update robot status according to sensors readings
    public void run(){
        location.update();
        location.log();
        System.out.println("State: " + movementState.toString());
        // Update state according to current state
        switch (movementState) {
            case STOP -> {
                // Check position
                if (!location.checkPosition()){
                    // Update state according to alignment
                    movementState = location.checkAlignment() ? MovementStates.FORWARD : MovementStates.ROTATE;
                }
            }
            case ROTATE -> {
                if (location.checkAlignment()){
                    movementState = MovementStates.FORWARD;
                }
            }
            case CURVE -> {
                if (location.checkAlignment()){
                    double dirDiff = Math.abs(location.directionDiff());
                    movementState = dirDiff < 1 ? MovementStates.FORWARD : MovementStates.CURVE;
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
                    movementState = location.checkAlignment() ? MovementStates.FORWARD : MovementStates.CURVE;
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
            case STOP -> {
                // Do nothing
            }
            case ROTATE -> {
                spin();
            }
            case CURVE -> {
                curve();
            }
            case FORWARD -> {
                forward();
            }
            default -> {
                System.out.println(
                        String.format("Unknown state: %s, abort.", movementState));
                return;
            }
        }
    }

    // Set wheel velocity to default value
    private void resetSpeed(){
        leftWheel.setVelocity(defaultV);
        rightWheel.setVelocity(defaultV);
    }

    // Adjust speed of motor according target direction
    // One side reach stop when angle offset is 180 degrees
    private void curve(){
        double speedOffset = (location.directionDiff() / 180) * defaultV;
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

}
