package de.lmu.ifi.sep.abalone.logic.communication.subscribers;

public interface BoardEventListener extends EventListener{

    void handleWin();

    void handleBoardReady();
}
