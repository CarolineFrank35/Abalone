package de.lmu.ifi.sep.abalone.network.clients;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class implements the guest side variant of the client class
 */
public class GuestClient extends Client {

    private final InetAddress host;

    /**
     * Creates a guest client which is listening on/sending over the given port
     * after {@link #start()} was called and a connection to the given host was
     * established.
     *
     * @param host InetAddress of the host to which the connection is established
     * @param port Port on which the connection is established
     */
    public GuestClient(InetAddress host, int port, EventPublisher<ErrorEvent> publisher) {
        super("guestClient", port,publisher);
        this.host = host;
    }

    /**
     * Sets up the connection to a client/server by instantiating a socket
     * with the given host and port. If a connection attempt failed instantiation
     * will be repeated until connection was established.
     */
    @Override
    void setup() {
        logger.info("Connecting with the host client...");

        while (!this.closed) {
            try {
                this.socket = new Socket(this.host, this.port);
                logger.info("Connection with host client was established.");
                super.setup();
                break;
            } catch (IOException e) {
                logger.info("Connection attempt failed.");
                logger.info(e.getLocalizedMessage());
                logger.info("Trying again...");

                sleep();
            }
        }
    }

    @Override
    public void close() {
        logger.info(" Closing guest client...");
        this.closed = true;

        if (this.socket != null && !this.socket.isClosed()) {
            NetworkUtilities.close(this.socket);
            sleep();

            logger.info("Closing socket...");
            logger.info("Socket was closed.");
        }
        logger.info("Guest client was closed.");
    }

    /**
     * Lets the thread sleep for 1000 milli seconds
     */
    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            logger.severe(ie.getLocalizedMessage());
        }
    }
}
