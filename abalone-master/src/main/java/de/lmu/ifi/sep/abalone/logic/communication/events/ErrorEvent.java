package de.lmu.ifi.sep.abalone.logic.communication.events;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.ErrorEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventListener;

public class ErrorEvent implements EventMessage {

    private final EventBus.EventType type;

    private ErrorEvent(EventBus.EventType type) {
        this.type = type;
    }

    public static ErrorEvent syncError() {
        return new ErrorEvent(EventBus.EventType.SYNC_ERROR);
    }

    public static ErrorEvent networkError() {
        return new ErrorEvent(EventBus.EventType.NETWORK_ERROR);
    }

    @Override
    public void doWithListener(EventListener e) {
        if (!(e instanceof ErrorEventListener)) {
            return;
        }
        ErrorEventListener parsed = (ErrorEventListener) e;
        switch (type){
            case SYNC_ERROR:
                parsed.handleSyncError();
                break;
            case NETWORK_ERROR:
                parsed.handleNetworkError();
                break;
            default:

        }
    }

    @Override
    public EventBus.Channel getChannel() {
        return EventBus.Channel.ERROR;
    }
}
