package main.MessageQueue;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageQueueTest {

    Message expectedMessage = new Message();
    class MessageSenderWithBool implements MessageSender {
            boolean ran = false;
            @Override
            public void sendMessage(String peerID, Message message) {
                Assert.assertEquals(peerID,"1001");
                Assert.assertEquals(expectedMessage,message);
                Assert.assertFalse(ran);
                ran = true;
            }
    }

    @Test
    public void addMessage() throws Exception {
        MessageSenderWithBool messageSender = new MessageSenderWithBool();
        MessageQueue queue = new MessageQueue(messageSender);
        queue.addMessage("1001",expectedMessage);
        // Really ugly, but no real way to synchronize.
        Thread.sleep(100);
        Assert.assertTrue(messageSender.ran);

    }

}