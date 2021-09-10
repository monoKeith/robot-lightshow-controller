// File:          test_drive.java
// Date:
// Description:
// Author:
// Modifications:

// You may need to add other webots classes such as
//  import com.cyberbotics.webots.controller.DistanceSensor;
//  import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.LED;
import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;

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

    boolean round = true;
    int colorIndex = 0;
    
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

    }
  }
}
