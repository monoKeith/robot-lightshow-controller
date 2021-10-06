import com.cyberbotics.webots.controller.Compass;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.LED;
import com.cyberbotics.webots.controller.Robot;

import java.util.ArrayList;

public class LightBot {

    public static void main(String[] args) throws InterruptedException {

        BotControl thisBot = new BotControl();

        // Run loop
        while (thisBot.waitTimeStep()) {
            thisBot.run();
        }

    }
}
