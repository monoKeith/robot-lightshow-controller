import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

public class Subscribe_JMS {

    public static void main(String[] args) throws Exception {
        // Setup
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost("tcp://localhost:55555");
        connectionFactory.setUsername("default");
        connectionFactory.setPassword("default");
        connectionFactory.setVPN("LightBot");
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        // Subscribe
        final String TOPIC_NAME = "try-me";
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageConsumer messageConsumer = session.createConsumer(topic);
        final CountDownLatch latch = new CountDownLatch(3);

        messageConsumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    System.out.printf("TextMessage received: '%s'%n", ((TextMessage) message).getText());
                } else {
                    System.out.println("Message received.");
                    System.out.printf("Message Content:%n%s%n", SolJmsUtility.dumpMessage(message));
                }

                //latch.countDown(); // unblock the main thread
            } catch (JMSException ex) {
                System.out.println("Error processing incoming message.");
                ex.printStackTrace();
            }
        });


        // Start
        connection.start();
        latch.await();
    }
}
