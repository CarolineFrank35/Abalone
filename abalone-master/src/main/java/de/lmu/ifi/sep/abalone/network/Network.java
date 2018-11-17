package de.lmu.ifi.sep.abalone.network;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.ErrorEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventBusSubscription;
import de.lmu.ifi.sep.abalone.network.clients.Client;
import de.lmu.ifi.sep.abalone.network.clients.GuestClient;
import de.lmu.ifi.sep.abalone.network.clients.HostClient;
import de.lmu.ifi.sep.abalone.network.message.Message;
import de.lmu.ifi.sep.abalone.network.message.MessageHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Provides an api for the network package
 */
public class Network implements NetworkObservable, ConnectorObservable, Closeable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private MessageHandler handler;
    private Client client;
    private ClientType clientType;

    /**
     * Creates a network api object with a host client.
     * Waits for a guest client after {@link #connect(ConnectorObserver)}
     * was called.
     * Throws assertion error if port validation fails.
     * Use {@link NetworkUtilities#validatePort(String)} to validate port before.
     *
     * @param port port the host client is listening on
     */
    public Network(String port,  EventPublisher<ErrorEvent> publisher) {
        assert NetworkUtilities.validatePort(port);

        this.clientType = ClientType.HOST;
        this.client = new HostClient(Integer.parseInt(port), publisher);
    }

    /**
     * Creates a network api object with a guest client.
     * Connects to the host on the given port after {@link #connect(ConnectorObserver)}
     * was called.
     * Throws assertion error if port or host validation fails.
     * Use {@link NetworkUtilities#validatePort(String)} and
     * {@link NetworkUtilities#validateHost(String)} to validate port and host
     * before.
     *
     * @param host address of the host
     * @param port port the host is listening on
     * @throws IOException if io error appears unexpectedly
     */
    public Network(String host, String port,  EventPublisher<ErrorEvent> publisher) throws IOException {
        assert NetworkUtilities.validateHost(host);
        assert NetworkUtilities.validatePort(port);

        this.clientType = ClientType.GUEST;
        this.client = new GuestClient(InetAddress.getByName(host), Integer.parseInt(port), publisher);
    }

    @Override
    public void connect(ConnectorObserver observer) {
        this.client.start(observer);
        //   observer.connected();
    }

    /**
     * @return client type of the client used for network communication.
     * @apiNote client type depends on the called constructor of the network class
     */
    public ClientType getClientType() {
        return this.clientType;
    }

    @Override
    public void setObserver(NetworkObserver observer) {
        assert client.isConnected();
        if (this.handler != null) {
            this.handler.reset(observer);
        } else {
            this.handler = new MessageHandler(this.client.getMessageQueue(), observer);
            this.handler.start();
        }

    }

    @Override
    public void send(Message message) throws IOException {
        assert client != null && client.isConnected();

        this.client.send(message);
    }

    @Override
    public void close() {
        logger.info("Closing network...");

        NetworkUtilities.close(this.handler);
        NetworkUtilities.close(this.client);

        this.client = null;
        this.handler = null;

        logger.info("Network was closed.");
    }

    /**
     * Enumeration of client types HOST and GUEST
     */
    public enum ClientType {
        HOST, GUEST
    }
}
