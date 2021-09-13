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
    private static final int timeStep       = 1000;
    // Robot
    private Robot robot;
    // Onboard devices
    private Motor leftWheel, rightWheel;
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

        leftWheel.setPosition(10);
        rightWheel.setPosition(10);

        // Produce log
        location.log();
    }

}
