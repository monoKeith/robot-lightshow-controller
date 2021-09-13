import com.cyberbotics.webots.controller.Compass;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.Robot;

public class Location {
    private static final String gpsName     = "gps";
    private static final String compassName = "compass";
    private static final int sampleRateMs   = 100;
    // Sensors
    private GPS gps;
    private Compass compass;
    // Target
    private double targetX, targetY;
    // Delta
    private double deltaX, deltaY;
    // Directions (in Degrees)
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

    public void setTarget(int X, int Y){
        targetX = X;
        targetY = Y;
    }

    public void update(){
        location = gps.getValues();
        direction = compass.getValues();

        deltaX = targetX - location[2];
        deltaY = targetY - location[0];
        targetAngle = Math.atan(deltaY / deltaX) * 180 / (Math.PI);
        targetAngle = targetAngle >= 0 ? targetAngle : targetAngle + 180;
        targetAngle = deltaY < 0 ? targetAngle + 180 : targetAngle;

        double dirX = - direction[2];
        double dirY = direction[0];
        curAngle = Math.atan(dirY / dirX) * 180 / (Math.PI);
        curAngle = curAngle >= 0 ? curAngle : curAngle + 180;
        curAngle = dirY < 0 ? curAngle + 180 : curAngle;
    }

    /* Log */

    public void log(){
        logGPS();
        logCompass();
        logDelta();
        logDirection();
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
