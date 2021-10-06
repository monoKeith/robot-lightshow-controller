import com.cyberbotics.webots.controller.Compass;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.LED;
import com.cyberbotics.webots.controller.Robot;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.UUID;

public class LightBotFirmware {

    public static void main(String[] args) throws InterruptedException, MqttException {
        // Generate a random UUID
        UUID uuid= UUID.randomUUID();
        // Setup transmitter identity
        Transmitter transmitter = new Transmitter(uuid);
        // Init Bot
        BotControl thisBot = new BotControl(transmitter);
        // Run loop
        while (thisBot.waitTimeStep()) {
            thisBot.controlThread();
        }

    }
}
