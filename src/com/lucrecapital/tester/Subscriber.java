package com.lucrecapital.tester;

import com.superstream.messages.PushMessageExtended;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.UDPBroadcastGroupConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;

/**
 *
 * @author koleschenko
 */
public class Subscriber {

    private TopicSubscriber subscriber;
    private TopicSession session;
    private TopicConnection connection;
    private TopicPublisher topicPublisher;

    public PushMessageExtended pushMessage = new PushMessageExtended();

    public void createSubscriber() {
        try {
            String name = "test-cluster";//pp.getProperty("HornetClusterName");
            String adress = "239.1.1.254";// pp.getProperty("HornetClusterGroupAdress");
            int port = 9875;//Integer.valueOf(pp.getProperty("HornetClusterGroupPort"));
            UDPBroadcastGroupConfiguration udpBroadcastGroupConfiguration
                    = new UDPBroadcastGroupConfiguration(adress, port, null, 0);
            ServerLocator serverLocator = HornetQClient.createServerLocatorWithHA(
                    new DiscoveryGroupConfiguration(name, 10000, 10000, udpBroadcastGroupConfiguration));
            ConnectionFactory connectionFactory = new HornetQJMSConnectionFactory(serverLocator);
            connection = ((TopicConnectionFactory) connectionFactory).createTopicConnection();
            Random r = new Random();
            connection.setClientID("myMegaHatorID_" + r.nextInt());
            connection.start();
            System.out.println("Connection created");
            session = connection.createTopicSession(false,
                    TopicSession.AUTO_ACKNOWLEDGE);
            System.out.println("Session created");
            Topic topic = session.createTopic("ExposureEvents");
            System.out.println(topic);
            topicPublisher = session.createPublisher(topic);
            subscriber = session.createSubscriber(topic);

            System.out.println(subscriber);
            System.out.println(topicPublisher);
        } catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendMessage() {
        try {
            if (session != null) {
                ObjectMessage createObjectMessage = session.createObjectMessage(pushMessage);
                topicPublisher.publish(createObjectMessage);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setMessage(String body, String title) {
        pushMessage.setBody(body);
        pushMessage.setTitle(title);
    }
    
    public void setListener(TextListener tml) {
        try {
            subscriber.setMessageListener(tml);
        } catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
};
