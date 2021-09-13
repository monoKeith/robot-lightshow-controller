import com.cyberbotics.webots.controller.*;

public class BotControl {
    // Controller class for LightBot
    private static final String[] ledNames  = {"led1", "led2", "led3", "led4", "led5", "led6", "led7"};
    private static final String leftMotor   = "left wheel motor";
    private static final String rightMotor  = "right wheel motor";
    private static final String rgbName     = "ledrgb";
    private static final String gpsName     = "gps";
    private static final String compassName = "compass";
    private static final int timeStep       = 1000;
    private static final int sampleRateMs   = 100;
    // Robot
    private Robot robot;
    // Onboard devices
    private Motor leftWheel, rightWheel;
    private LED[] leds;
    private LED rgb;
    private GPS gps;
    private Compass compass;
    // Target
    private int targetX, targetY;

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
        // Init GPS
        gps = robot.getGPS(gpsName);
        gps.enable(sampleRateMs);
        // Init Compass
        compass = robot.getCompass(compassName);
        compass.enable(sampleRateMs);
        // Target
        setTarget(0,0);
    }

    // Target GPS location of robot
    public void setTarget(int X, int Y){
        targetX = X;
        targetY = Y;
    }

    // Stuck until time step
    public boolean waitTimeStep(){
        return robot.step(timeStep) != -1;
    }

    // Update robot status according to sensors readings
    public void run(){


        // Produce log
        logGPS();
        logCompass();
    }

    public void logGPS(){
        double[] location = gps.getValues();
        System.out.println(
                String.format("GPS: X-Floor:%.02f Y-Floor:%.02f Height:%.02f",
                        location[2], location[0], location[1]));
    }

    public void logCompass(){
        double[] direction = compass.getValues();
        System.out.println(
                String.format("COMPASS: X-Floor:%.02f Y-Floor:%.02f UP:%.02f",
                        direction[2], direction[0], direction[1]));
    }


}
