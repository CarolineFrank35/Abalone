package de.lmu.ifi.sep.abalone.logic.communication.subscribers;

public interface ErrorEventListener extends EventListener {

    void handleSyncError();

    void handleNetworkError();

}
