package com.lucrecapital.tester;

import ca.juliusdavies.nanotime.Clock;
import com.superstream.messages.PushMessage1;
import com.superstream.messages.PushMessageExtended;
import javax.jms.*;
import static org.hornetq.jms.client.HornetQJMSClientLogger.LOGGER;

public class TextListener implements MessageListener {

    public PushMessage1 pm = new PushMessage1();
    public PushMessageExtended pme = new PushMessageExtended();

    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage) message;
            try {
                Object object = om.getObject();

                if (object instanceof PushMessage1) {
                    pm = (PushMessage1) object;

                } else if (object instanceof PushMessageExtended) {
                    pme = (PushMessageExtended) object;

                } else {
                    LOGGER.info("Not a push Message");
                }

            } catch (JMSException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
    
    public PushMessageExtended getLastMessage(){
       return pme;
    }
    
    public void setLastMessage(String text){
       pme.setTitle(text);
    }
    
    public long getCurrentTimestamp(){
        long[] tmp1 = Clock.nativeTime();
        //System.out.print(tmp1[1]);
        return tmp1[1];
    }

}
