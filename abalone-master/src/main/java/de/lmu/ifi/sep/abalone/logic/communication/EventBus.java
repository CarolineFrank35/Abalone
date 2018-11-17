package de.lmu.ifi.sep.abalone.logic.communication;

import de.lmu.ifi.sep.abalone.logic.communication.events.BoardEvent;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.events.EventMessage;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.BoardEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.ErrorEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventBusSubscription;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventListener;

import java.util.LinkedList;
import java.util.List;

public class EventBus {

    private final List<EventBusSubscription<ErrorEventListener>>
            errorSubscriptions = new LinkedList<>();
    private final List<EventBusSubscription<BoardEventListener>>
            boardSubscriptions = new LinkedList<>();

    private void sendBoardEvent(BoardEvent event) {
        for (EventBusSubscription<BoardEventListener> subscription
                : boardSubscriptions) {
                subscription.handleEvent(event);
        }
    }

    private void sendErrorEvent(ErrorEvent event) {
        for (EventBusSubscription<ErrorEventListener> subscription
                : errorSubscriptions) {
            subscription.handleEvent(event);
        }
    }

    public void receive(EventMessage e){
        switch (e.getChannel()){
            case BOARD:
                sendBoardEvent((BoardEvent) e);
                break;
            case ERROR:
                sendErrorEvent((ErrorEvent) e);
                break;

        }
    }


    public EventBusSubscription<ErrorEventListener> newErrorSubscription() {
        EventBusSubscription<ErrorEventListener> out
                = new EventBusSubscription<>(this);
        errorSubscriptions.add(out);
        return out;
    }

    public EventBusSubscription<BoardEventListener> newBoardSubscription() {
        EventBusSubscription<BoardEventListener> out = new EventBusSubscription<>(this);
        boardSubscriptions.add(out);
        return out;
    }

    public enum EventType {
        SYNC_ERROR, BOARD_READY, NETWORK_ERROR, WIN
    }

    public enum Channel{
        BOARD, ERROR
    }
}
