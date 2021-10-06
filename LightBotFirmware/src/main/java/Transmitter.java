import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Transmitter {

    public static final String HOST = "tcp://localhost";
    public static final String USERNAME = "default";
    public static final String PASSWORD = "default";

    private final UUID uuid;

    public Transmitter(UUID uuid) throws MqttException, InterruptedException {
        //
        this.uuid = uuid;

        // Initialize
        MqttClient mqttClient = new MqttClient(HOST, "LightBot_" + uuid);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        mqttClient.connect(connOpts);

        CountDownLatch latch = new CountDownLatch(1);

        // Topic should be identifier of a robot
        String topic = "try-me";
        mqttClient.subscribe(topic, 0);

        // Receive msg callback
        mqttClient.setCallback(new MqttCallback() {

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("\nReceived a Message!" +
                        "\n\tTopic:   " + topic +
                        "\n\tMessage: " + new String(message.getPayload()) +
                        "\n\tQoS:     " + message.getQos() + "\n");

                latch.countDown();
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Connection to Solace broker lost!" + cause.getMessage());
                latch.countDown();
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }

        });

    }


}
