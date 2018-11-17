package de.lmu.ifi.sep.abalone.network;

/**
 * An object of a class implementing this interface can be given to
 * {@link ConnectorObservable#connect(ConnectorObserver)} as parameter.
 */
public interface ConnectorObserver {
    /**
     * Called if object was given to
     * {@link ConnectorObservable#connect(ConnectorObserver)}
     * as parameter and connection was established.
     */
    void connected();
}
