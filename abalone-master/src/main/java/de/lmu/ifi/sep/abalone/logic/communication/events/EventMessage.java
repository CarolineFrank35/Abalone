package de.lmu.ifi.sep.abalone.logic.communication.events;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventListener;

public interface EventMessage {
    void doWithListener(EventListener e);

    EventBus.Channel getChannel();
}
