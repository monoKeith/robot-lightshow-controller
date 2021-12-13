import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.UUID;

public class Transmitter {
    // Topics const
    public static final String HOST = "tcp://localhost";
    public static final String USERNAME = "default";
    public static final String PASSWORD = "default";
    // Robot send its UUID on startup once, to this topic:
    public static final String UUID_TOPIC = "T/LightBot/UUID";
    // Robot listen to broadcast from this topic:
    public static final String BROADCAST_TOPIC = "T/LightBot/Broadcast";
    // Robot publish msg once arrived destination
    public static final String ARRIVAL_TOPIC = "T/LightBot/Arrival";

    // MQTT related vars
    private final UUID uuid;
    private final MqttClient mqttClient;
    private final MqttConnectOptions connOpts;

    // Queue to save received messages
    private LinkedList<String> receiveQueue;

    private boolean terminationFlag;

    public Transmitter(UUID uuid) throws MqttException {
        // Initialize vars
        this.uuid = uuid;
        receiveQueue = new LinkedList<String>();
        terminationFlag = false;

        // Initialize MQTT
        mqttClient = new MqttClient(HOST, "LightBot/" + uuid);
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        mqttClient.connect(connOpts);

        // Robot listen to its UUID, and broadcast
        String topic = "T/LightBot/" + uuid;
        mqttClient.subscribe(topic, 0);
        mqttClient.subscribe(BROADCAST_TOPIC, 0);

        // Setup msg callback
        mqttClient.setCallback(new MqttCallback() {

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                if (BotControl.LOG_ENABLE){
                    System.out.println(
                        "\nReceived message:" +
                        "\n\tTopic:   " + topic +
                        "\n\tMessage: " +  msg + "\n");
                }

                // Save message to queue
                queueMsg(msg);
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Connection to Solace broker lost!" + cause.getMessage());
                System.exit(1);
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }

        });
    }

    private synchronized void queueMsg(String newMsg){
        receiveQueue.add(newMsg);
        notifyAll();
    }

    // Get the newest message, stuck here if no new message
    public synchronized String waitForMsg() throws InterruptedException {
        while(receiveQueue.isEmpty()){
            wait();
            if (terminationFlag) return null;
        }
        String newMsg = receiveQueue.pop();
        notifyAll();
        return newMsg;
    }

    public void powerOnMessage(Location location) throws MqttException {
        String message = String.format("%s %s %s",
                uuid.toString(),
                location.getX(),
                location.getY()
        );
        MqttMessage newMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
        newMessage.setQos(0);
        mqttClient.publish(UUID_TOPIC, newMessage);
    }

    // Report location
    public void arrivalMessage(Location location) throws MqttException {
        String message = String.format("%s %s %s",
                uuid.toString(),
                location.getX(),
                location.getY()
                );
        MqttMessage newMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
        newMessage.setQos(0);
        mqttClient.publish(ARRIVAL_TOPIC, newMessage);
    }

    // Disconnect mqtt client
    public synchronized void disconnect(){
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        terminationFlag = true;
        notifyAll();
    }

}
