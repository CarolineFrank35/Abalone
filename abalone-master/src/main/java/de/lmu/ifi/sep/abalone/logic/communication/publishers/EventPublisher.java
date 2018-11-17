package de.lmu.ifi.sep.abalone.logic.communication.publishers;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.events.EventMessage;

public class EventPublisher<T extends EventMessage>{

    private final EventBus eventBus;

    public EventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendMessage(T message){
        eventBus.receive(message);
    }

}
