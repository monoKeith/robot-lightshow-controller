import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

public class Sender {

    public static void main(String[] args) throws Exception {
        // Setup
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost("tcp://localhost:55555");
        connectionFactory.setUsername("default");
        connectionFactory.setPassword("default");
        connectionFactory.setVPN("default");
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        // Send
        final String TOPIC_NAME = "try-me";
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageProducer messageProducer = session.createProducer(topic);

        TextMessage message = session.createTextMessage("Hello world! From Java JMS.");
        messageProducer.send(topic, message, DeliveryMode.NON_PERSISTENT,
                Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

    }

}
