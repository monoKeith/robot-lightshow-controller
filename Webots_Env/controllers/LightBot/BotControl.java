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
        location.setTarget(0,0);
        // State
        movementState = MovementStates.STOP;
    }

    // Stuck until time step
    public boolean waitTimeStep(){
        return robot.step(timeStep) != -1;
    }

    // Update robot status according to sensors readings
    public void run(){
        location.update();
        location.log();
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
                    // Update state to forward
                    movementState = MovementStates.FORWARD;
                }
            }
            case FORWARD -> {
                if (location.checkPosition()){
                    // Arrived
                    movementState = MovementStates.STOP;
                } else {
                    // Not arrived yet, update state according to alignment
                    movementState = location.checkAlignment() ? MovementStates.FORWARD : MovementStates.ROTATE;
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
                // Spin towards target direction
                spin(location.getTargetAngle());
            }
            case FORWARD -> {
                forward(location.getDistance());
            }
            default -> {
                System.out.println(
                        String.format("Unknown state: %s, abort.", movementState));
                return;
            }
        }
    }

    // Spin robot to point to a specified direction
    private void spin(double targetAngle){
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double degreesToTurn = (targetAngle - location.getCurAngle()) * spinConst;
        // Spin wheels in opposite direction
        leftWheel.setPosition(curLPosition - degreesToTurn);
        rightWheel.setPosition(curRPosition + degreesToTurn);
    }

    private void forward(double distance){
        double curLPosition = leftPosition.getValue();
        double curRPosition = rightPosition.getValue();
        double distanceToGo = distance * moveConst;
        // Spin wheels in same direction
        leftWheel.setPosition(curLPosition + distanceToGo);
        rightWheel.setPosition(curRPosition + distanceToGo);
    }

}
