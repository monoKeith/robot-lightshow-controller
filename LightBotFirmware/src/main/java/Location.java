import com.cyberbotics.webots.controller.Compass;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.Robot;

public class Location {
    private static final String gpsName     = "gps";
    private static final String compassName = "compass";
    // sampleRateMs - GPS and Compass sample rate (in ms)
    private static final int sampleRateMs   = BotControl.sampleRateMs;
    // posAccuracy - accuracy for detecting if target position is reached (in meter)
    private static final double posAccuracy = 0.01;
    // spinAccuracy - accuracy for spinning towards target position (in degrees)
    private static final double spinAccuracy = 5;
    // curveAccuracy - accuracy for curving towards target position (in degrees)
    private static final double curveAccuracy = 0.1;
    // Sensors
    private GPS gps;
    private Compass compass;
    // Target
    private double targetX, targetY;
    // Delta
    private double deltaX, deltaY;
    // Directions (in Degrees)
    // targetAngle - when vertex is the robot, angle between x-axis and target point
    // curAngle - when vertex is the robot, angle between x-axis and robot's forward direction
    // When these 2 angles match, robot is pointing straight towards target point
    private double targetAngle, curAngle;
    // Sensors data
    private double[] location, direction;

    public Location(Robot robot){
        // Init GPS
        gps = robot.getGPS(gpsName);
        gps.enable(sampleRateMs);
        // Init Compass
        compass = robot.getCompass(compassName);
        compass.enable(sampleRateMs);
        // Default values
        targetX = 0;
        targetY = 0;
        deltaX = 0;
        deltaY = 0;
    }

    public synchronized void setTarget(double X, double Y){
        targetX = X;
        targetY = Y;
    }

    // True  - head pointing to correct direction
    // False - Need alignment
    public synchronized boolean checkSpinAlignment(){
        return Math.abs(curAngle - targetAngle) <= spinAccuracy;
    }

    public synchronized boolean checkCurveAlignment(){
        return Math.abs(curAngle - targetAngle) <= curveAccuracy;
    }

    // Check if robot in target location
    public synchronized boolean checkPosition(){
        return Math.abs(deltaX) <= posAccuracy && Math.abs(deltaY) <= posAccuracy;
    }

    public synchronized void update(){
        location = gps.getValues();
        direction = compass.getValues();
        // Calculate targetAngle
        deltaX = targetX - location[2];
        deltaY = targetY - location[0];
        targetAngle = Math.atan(deltaY / deltaX) * 180 / (Math.PI);
        targetAngle = targetAngle >= 0 ? targetAngle : targetAngle + 180;
        targetAngle = deltaY < 0 ? targetAngle + 180 : targetAngle;
        // Calculate curAngle
        double dirX = - direction[2];
        double dirY = direction[0];
        curAngle = Math.atan(dirY / dirX) * 180 / (Math.PI);
        curAngle = curAngle >= 0 ? curAngle : curAngle + 180;
        curAngle = dirY < 0 ? curAngle + 180 : curAngle;
    }

    // absolute Distance from target position
    public synchronized double getDistance(){
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    // Returns difference between target angle and current angle
    // https://stackoverflow.com/questions/1878907/how-can-i-find-the-difference-between-two-angles
    public synchronized double directionDiff(){
        double diff = targetAngle - curAngle;
        return (diff + 180) % 360 - 180;
    }

    /* Log */

    public void log(){
        logGPS();
        logCompass();
        logDelta();
        logDirection();
        System.out.println("Direction diff: " + directionDiff());
    }

    public void logGPS(){
        System.out.println(
                String.format("GPS: X-Floor:%.02f Y-Floor:%.02f Height:%.02f",
                        location[2], location[0], location[1]));
    }

    public void logCompass(){
        System.out.println(
                String.format("COMPASS: X-Floor:%.02f Y-Floor:%.02f UP:%.02f",
                        direction[2], direction[0], direction[1]));
    }

    public void logDelta(){
        System.out.println(
                String.format("DeltaX: %.02f, DeltaY: %.02f", deltaX, deltaY));
    }

    public void logDirection(){
        System.out.println(
                String.format("PointingAt: %.02f TargetAt: %.02f",
                        curAngle, targetAngle));
    }

}
