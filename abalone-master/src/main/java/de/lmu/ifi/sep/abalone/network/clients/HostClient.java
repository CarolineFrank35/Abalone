package de.lmu.ifi.sep.abalone.network.clients;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Class implements the host side variant of the client class
 */
public class HostClient extends Client {

    private ServerSocket serverSocket;

    /**
     * Creates a host client which is listening on/sending over the given port
     * after {@link #start()} was called and a connection was established.
     *
     * @param port Port on which the connection is established
     */
    public HostClient(final int port, final EventPublisher<ErrorEvent> publisher) {
        super("hostClient", port, publisher);
    }

    /**
     * Sets up the connection to a client by instantiating a server socket which
     * listens on the given port until a connection request was received. Thereafter
     * super.setup() is called.
     */
    @Override
    void setup() {
        try {
            serverSocket = new ServerSocket(this.port);

            logger.info("Waiting for the guest client...");
            this.socket = serverSocket.accept();
            logger.info("Connection with guest client was established.");

            logger.info("Closing server socket...");
            NetworkUtilities.close(serverSocket);
            logger.info("Sever socket was closed...");

            super.setup();
        } catch (IOException e) {
            if (serverSocket == null || !serverSocket.isClosed()) {
                logger.info("Setup failed.");
                logger.severe(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void close() {
        logger.info(" Closing host client...");
        closed = true;

        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            NetworkUtilities.close(this.serverSocket);
            logger.info("Server socket was closed.");
        }

        if (this.socket != null && !this.socket.isClosed()) {
            logger.info("Closing socket...");
            NetworkUtilities.close(this.socket);
            logger.info("Socket was closed.");
        }

        logger.info("Host client was closed.");
    }
}
