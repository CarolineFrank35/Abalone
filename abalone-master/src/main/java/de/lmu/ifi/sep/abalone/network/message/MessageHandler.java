package de.lmu.ifi.sep.abalone.network.message;

import de.lmu.ifi.sep.abalone.network.NetworkObserver;

import java.io.Closeable;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

/**
 * Class implements the message handler concept which provides a non blocking
 * strategy to handle message received from the network
 */
public class MessageHandler extends Thread implements Closeable {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private BlockingDeque<Message> messageQueue;
    private NetworkObserver observer;

    /**
     * Creates a message handler, which forwards the first element within the
     * given messageQueue if available to the given observer until {@link #close()} is called.
     * Throws an assertion error if messageQueue or observer is null.
     * After construction {@link #start()} must be called to execute the thread.
     *
     * @param messageQueue - Watched queue
     * @param observer     - Notified if element available
     */
    public MessageHandler(BlockingDeque<Message> messageQueue, NetworkObserver observer) {
        assert messageQueue != null;
        assert observer != null;

        logger.info("Initializing message handler...");

        this.messageQueue = messageQueue;
        this.observer = observer;

        logger.info("Message handler ready.");
    }

    @Override
    public void run() {
        logger.info("Handling messages...");
        while (true) {
            try {
                Message message = this.messageQueue.takeFirst();

                if (message instanceof CloseMessage) {
                    break;
                } else {
                    logger.info("Handling Message: " + message.toString());
                    observer.receive(message);
                }
            } catch (InterruptedException e) {
                logger.severe(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Closes the message handler by inserting an poison element into the
     * given message queue which terminates the message handler.
     */
    @Override
    public void close() {
        logger.info("Closing message handler...");
        this.messageQueue.addFirst(new CloseMessage());
        logger.info("Message handler was closed");

    }

    public void reset(NetworkObserver observer) {
        this.observer = observer;
    }
}
