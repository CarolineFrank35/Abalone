package de.lmu.ifi.sep.abalone.network;

import de.lmu.ifi.sep.abalone.network.message.Message;

/**
 * An object of a class implementing this interface can be given to
 * {@link NetworkObservable#setObserver(NetworkObserver)}
 * as a parameter
 */
public interface NetworkObserver {
    /**
     * If a message was received  by the {@link NetworkObservable}
     * this method will be called
     *
     * @param message received message
     */
    void receive(Message message);
}
