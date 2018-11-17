package de.lmu.ifi.sep.abalone.logic.communication.subscribers;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.EventMessage;

public class EventBusSubscription<T extends EventListener> {

    private final EventBus eventBus;
    private boolean ready;
    private T listener;

    public EventBusSubscription(EventBus eventBus) {
        this.eventBus = eventBus;
        this.listener = null;
        this.ready = false;
    }

    public void attachEventHandler(T listener) {
        this.ready = true;
        this.listener = listener;
    }

    public void handleEvent(EventMessage eventMessage) {
        if (ready) {
            eventMessage.doWithListener(this.listener);
        }
    }

    public void unsubscribe(){

    }
}
