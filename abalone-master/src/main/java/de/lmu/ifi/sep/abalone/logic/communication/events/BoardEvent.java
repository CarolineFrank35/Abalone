package de.lmu.ifi.sep.abalone.logic.communication.events;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.BoardEventListener;

public class BoardEvent implements EventMessage {

    private final EventBus.EventType type;

    private BoardEvent(EventBus.EventType type){
        this.type = type;
    }

    public static BoardEvent boardReady(){
        return new BoardEvent(EventBus.EventType.BOARD_READY);
    }

    public static BoardEvent winEvent(){
        return new BoardEvent(EventBus.EventType.WIN);
    }

    public void doWithListener(EventListener eventListener){
        if(!(eventListener instanceof BoardEventListener)){
            return;
        }
        BoardEventListener parsed = (BoardEventListener)eventListener;
        switch (type){
            case BOARD_READY:
                parsed.handleBoardReady();
                break;
            case WIN:
                parsed.handleWin();
        }
    }

    @Override
    public EventBus.Channel getChannel() {
        return EventBus.Channel.BOARD;
    }
}
