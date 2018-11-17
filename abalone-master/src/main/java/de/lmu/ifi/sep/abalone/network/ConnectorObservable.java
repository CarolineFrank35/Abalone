package de.lmu.ifi.sep.abalone.network;

/**
 * A class implementing this interface handles at least the connection of  the client
 * on the current device to a client or server
 */
interface ConnectorObservable {
    /**
     * Starts the connection setup and establishes connection if possible
     *
     * @param observer Observer which will be notified after connection was established
     */
    void connect(ConnectorObserver observer);
}
