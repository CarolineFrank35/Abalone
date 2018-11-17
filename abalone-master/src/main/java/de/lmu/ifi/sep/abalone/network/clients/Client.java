package de.lmu.ifi.sep.abalone.network.clients;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.events.EventMessage;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.network.ConnectorObserver;
import de.lmu.ifi.sep.abalone.network.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

/**
 * Super class of {@link HostClient} and {@link GuestClient} which provides basic
 * functionality
 */
public abstract class Client extends Thread implements Closeable {

    final Logger logger = Logger.getLogger("thread::" + this.getName());
    final int port;
    private final BlockingDeque<Message> messageQueue;
    Socket socket;
    boolean closed = false;
    private ConnectorObserver observer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final EventPublisher<ErrorEvent> publisher;

    /**
     * Creates a client object
     *
     * @param name of the thread
     * @param port used for network communication
     */
    Client(String name, final int port, EventPublisher<ErrorEvent> publisher) {
        super(name);
        this.port = port;
        this.messageQueue = new LinkedBlockingDeque<>();
        this.publisher = publisher;
    }

    /**
     * Starts the client and sets an observer which is notified after the
     * connection with a client/server was established
     *
     * @param observer Observer which will be notified after connection was established
     */
    public void start(ConnectorObserver observer) {
        assert observer != null;

        this.observer = observer;
        this.start();
    }

    /**
     * Establishes connection during setup process and listens to port for
     * incoming messages after connection was established. Received messages
     * are appended to messageQueue.
     */
    @Override
    public void run() {
        setup();
        this.observer.connected();
        this.observer = null;

        try {
            while (this.socket != null && !this.socket.isClosed()) {
                Object received = this.inputStream.readObject();
                if (received instanceof Message) {
                    this.messageQueue.putLast((Message) received);
                } else {
                    if (this instanceof HostClient) {
                        this.messageQueue.putLast(new Message(Message.MessageType.SYNC, null));
                    } else {
                        this.send(new Message(Message.MessageType.SYNC_REQUEST, null));
                    }
                }
            }
        } catch (IOException e) {
            if (!closed) {
                logger.severe(e.getLocalizedMessage());
                publisher.sendMessage(ErrorEvent.networkError());
            }
        } catch (ClassNotFoundException | InterruptedException e) {
            logger.severe(e.getLocalizedMessage());
        }
    }


    /**
     * Builds a stream cascade from the socket in/output streams to object
     * in/output streams.
     * Method is overwritten by derived classes but called during setup process
     * by super.setup().
     */
    void setup() {
        try {
            logger.info("Initializing streams...");
            this.outputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.outputStream.flush();
            this.inputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            logger.info("Streams were initialized.");

            logger.info("Completed setup successfully.");
        } catch (IOException e) {
            if (socket != null && !this.socket.isClosed()) {
                logger.info("Setup failed.");
                logger.severe(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Sends the given message to the connected client/server
     *
     * @param message Message which will be send to the connected client/server
     * @throws IOException          if io error appears unexpectedly
     * @throws NullPointerException if client is not connected. Check with
     *                              {@link #isConnected()}
     */
    public void send(Serializable message) throws IOException {
        logger.info("Sending message " + message.toString());
        this.outputStream.writeObject(message);
        this.outputStream.flush();
        logger.info("Message sent");
    }

    /**
     * @return true, if client is connected to a client/socket, false otherwise
     */
    public boolean isConnected() {
        return this.socket != null && socket.isConnected();
    }

    /**
     * @return message queue objects in which received messages are inserted
     * at the end
     */
    public BlockingDeque<Message> getMessageQueue() {
        return messageQueue;
    }
}