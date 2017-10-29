package main.messsage.messagequeue;

import java.util.concurrent.LinkedBlockingQueue;

// Placeholder until the real message is created. Delete when merged in.
class Message {}

interface MessageSender {
    void sendMessage(String peerID, Message message);
}

// Sends out all messages given to it. Is thread safe, addMessage can be called at any time without issue.
public class MessageQueue {

    private final Thread messageQueueThread;

    private MessageSender messageSender;
    public class MessageAndDestination {
        public final Message message;
        public final String peerIDDestination;
        public MessageAndDestination( String peerIDDestination, Message message) {
            this.message = message;
            this.peerIDDestination = peerIDDestination;
        }
    }

    private final LinkedBlockingQueue<MessageAndDestination> messageQueue = new LinkedBlockingQueue<>();

    public void addMessage(String peerID, Message message) {
        messageQueue.offer(new MessageAndDestination(peerID,message));
    };


    public MessageQueue(MessageSender messageSender) {
        this.messageSender = messageSender;
        messageQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        MessageAndDestination messageAndDestination = messageQueue.take();
                        sendMessage(messageAndDestination.peerIDDestination, messageAndDestination.message);
                    } catch(InterruptedException exception) {
                        // This should never happen.
                        System.err.println("Error, message queue thread interrupted, why? " + exception);
                        // Failure error codes are between 1 and 127.
                        System.exit(1);
                    }
                }
            }
        });
        messageQueueThread.start();

    }

    private void sendMessage(String peerIDDestination, Message message) {
        this.messageSender.sendMessage(peerIDDestination,message);
    }


}
