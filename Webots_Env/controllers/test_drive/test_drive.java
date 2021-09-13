// File:          test_drive.java
// Date:
// Description:
// Author:
// Modifications:

// You may need to add other webots classes such as
//  import com.cyberbotics.webots.controller.DistanceSensor;
//  import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.*;

import java.util.ArrayList;

public class test_drive {

  public static void main(String[] args) throws InterruptedException {

    Robot robot = new Robot();
    final String[] ledNames = {"led1", "led2", "led3", "led4", "led5", "led6", "led7"};
    final int[] colors = { 0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0x00ffff, 0xff00ff, 0x000000 };

    ArrayList<LED> leds = new ArrayList<LED>();
    for (String ledName: ledNames){
      leds.add(robot.getLED(ledName));
    }
    final LED ledRGB = robot.getLED("ledrgb");
    final GPS gps = robot.getGPS("gps");
    final Compass compass = robot.getCompass("compass");

    boolean round = true;
    int colorIndex = 0;

    // Initialize GPS and Compass, sampling period 100 ms
    gps.enable(100);
    compass.enable(100);
    
    while (robot.step(1000) != -1) {

      for (LED led: leds) {
        led.set(round ? 1 : 0);
      }
      ledRGB.set(colors[colorIndex]);

      // Proceed led state
      round = ! round;
      colorIndex ++;
      if (colorIndex >= colors.length){
        colorIndex = 0;
      }

      // Read GPS
      double[] location = gps.getValues();
      System.out.println(String.format("GPS: X-Floor:%.02f Y-Floor:%.02f Height:%.02f", location[2], location[0], location[1]));

      // Read Compass
      double[] direction = compass.getValues();
      System.out.println(String.format("COMPASS: X-Floor:%.02f Y-Floor:%.02f UP:%.02f", direction[2], direction[0], direction[1]));

    }

  }
}
