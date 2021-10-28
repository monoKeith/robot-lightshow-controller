import com.cyberbotics.webots.controller.Compass;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.LED;
import com.cyberbotics.webots.controller.Robot;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.UUID;

public class LightBotFirmware {

    private UUID uuid;
    private Transmitter transmitter;
    private BotControl thisBot;

    public LightBotFirmware() throws MqttException {
        // Initialize Vars
        uuid= UUID.randomUUID();
        transmitter = new Transmitter(uuid);
        thisBot = new BotControl(transmitter);
    }

    private void run(){
        while (thisBot.waitTimeStep()) {
            thisBot.controlThread();
        }
        // Exit
        terminate();
    }

    private void terminate(){
        transmitter.disconnect();
        System.out.println("Light Bot Firmware terminated");
    }

    public static void main(String[] args) throws MqttException {
        LightBotFirmware firmware = new LightBotFirmware();
        firmware.run();
    }
}
