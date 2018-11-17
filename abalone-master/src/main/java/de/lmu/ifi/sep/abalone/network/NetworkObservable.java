package de.lmu.ifi.sep.abalone.network;

import de.lmu.ifi.sep.abalone.network.message.Message;

import java.io.IOException;

/**
 * A class implementing this interface handles at least the network communication
 * with the corresponding socket
 */
interface NetworkObservable {

    /**
     * Sets the network observer to which received messages are forwarded.
     * Only one observer at a time is allowed.
     * Connection must be established first, throws error otherwise
     *
     * @param observer Observer which will be given a message if such was received
     */
    void setObserver(NetworkObserver observer);

    /**
     * Sends the given message to the corresponding socket
     *
     * @param message Message which will be send to the connected client/server
     * @throws IOException if io error appears unexpectedly
     */
    void send(Message message) throws IOException;
}
