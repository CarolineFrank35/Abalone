package de.lmu.ifi.sep.abalone.logic;

import de.lmu.ifi.sep.abalone.components.GameObservable;
import de.lmu.ifi.sep.abalone.components.GameObserver;
import de.lmu.ifi.sep.abalone.components.ObservableMap;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.communication.InitPackage;
import de.lmu.ifi.sep.abalone.logic.communication.Move;
import de.lmu.ifi.sep.abalone.logic.communication.SyncPackage;
import de.lmu.ifi.sep.abalone.logic.communication.events.BoardEvent;
import de.lmu.ifi.sep.abalone.logic.communication.events.ErrorEvent;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.network.Network;
import de.lmu.ifi.sep.abalone.network.NetworkObserver;
import de.lmu.ifi.sep.abalone.network.message.Message;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class for handling the game logic during the course of a running game
 * it will handle game-state altering events from both the GUI
 * and the changes deployed by the Network
 */
public class AbaloneGame extends GameObservable implements NetworkObserver {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * {@link Network} instance of the game logic.
     * Will be used to send and receive Moves and events.
     */
    private final Network NETWORK;
    /**
     * the size of the current board
     * the size of a board is the number of cells of the biggest
     * column of the game
     */
    private int size;
    /**
     * instance of the AbaloneBoard model for setting positions
     */
    private AbaloneBoard board;
    /**
     * host player information
     */
    private Player host;
    /**
     * peer player information
     */
    private Player peer;
    /**
     * information about the currently active player
     * will contain a pointer to either
     * {@link AbaloneGame#host} or {@link AbaloneGame#peer}
     */
    private Player active;
    private Player localPlayer;

    private boolean winFlag = false;

    private boolean confirmWinSent = false;

    private boolean confirmedWin = false;

    private AbaloneBoard.Owner colorToSend = null;

    private Timer timer = null;

    private final EventPublisher<ErrorEvent> errorPublisher;
    private final EventPublisher<BoardEvent> boardPublisher;


    public AbaloneGame(final int s, Network network, AbaloneBoard.Owner myColor,
                       EventPublisher<ErrorEvent> errorPublisher,
                       EventPublisher<BoardEvent> boardPublisher) {
        logger.info("Constructed AbaloneGame: [" + s + ","
                + network.getClientType() + "," + myColor + "]");
        int piecesToWin = 2 * s / 3;
        this.errorPublisher = errorPublisher;
        this.boardPublisher = boardPublisher;

        this.NETWORK = network;
        initializeSubscriptions();

        size = s;
        if (s != 0) {
            board = new AbaloneBoard(s);
        }

        colorToSend = getOtherColor(myColor);
        initPlayer(myColor, piecesToWin);
        active = host;
        if (network.getClientType() == Network.ClientType.GUEST) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    sendNetworkMessage(Message.MessageType.RDY, null);
                }
            };
            timer = new Timer();
            timer.schedule(task, 0, 100);
        }
    }

    public AbaloneGame(final int s, Network network, EventPublisher<ErrorEvent>
                       errorPublisher, EventPublisher<BoardEvent> boardPublisher) {
        logger.info("Constructed AbaloneGame: [" + s + "," + network.getClientType() + "]");
        int piecesToWin = 2 * s / 3;

        this.NETWORK = network;
        this.errorPublisher = errorPublisher;
        this.boardPublisher = boardPublisher;
        initializeSubscriptions();
        if (network.getClientType() == Network.ClientType.HOST) {
            size = s;
            board = new AbaloneBoard(s);
            colorToSend = null;
/*            InitPackage initPackage = new InitPackage(s, null);
            sendNetworkMessage(Message.MessageType.INIT, initPackage);*/
            AbaloneBoard.Owner myColor =
                    network.getClientType() == Network.ClientType.HOST ?
                            AbaloneBoard.Owner.PLAYER_BLACK :
                            AbaloneBoard.Owner.PLAYER_WHITE;
            initPlayer(myColor, piecesToWin);

            active = host;
        } else {
            sendNetworkMessage(Message.MessageType.RDY, null);
        }

    }

    /**
     * Handles messages received by the Network.
     * The types result in the following actions:
     * <p>
     * {@code MOVE}:        The move of the opposing player is received.
     * Move gets validated and replayed on our board.
     * If the validation of the move fails, a sync will be performed
     * {@code SYNC}:        A SYNC order by the host of the game is received.
     * The local board layout will be replaced with the one transmitted
     * in the payload
     * {@code SYNC_REQUEST}:A SYNC_REQUEST get's received if a peer players
     * validation of a received move failed and a sync
     * is necessary
     * {@code WIN}:         A WIN message is received if the peer players last executed move
     * resulted in a win situation. If a win situation occurred as well
     * on the last received MOVE locally, it is confirmed by a CONFIRM_WIN
     * message
     * {@code CONFIRM_WIN}: A CONFIRM_WIN message gets received as a response to a earlier
     * sent WIN message. When this message gets received and the local
     * win flag is set the game will be deemed won.
     * {@code INIT}:        A INIT message gets received by the peer player when
     * the connection has been established. The Message contains a {@link InitPackage}
     * containing the board size.
     *
     * @param message received message
     */
    @Override
    public void receive(Message message) {
        if (confirmedWin) {
            return;
        }

        switch (message.getMessageType()) {
            case MOVE:
                if (!(message.getPayload() instanceof Move)) {
                    //handle error
                    return;
                }
                Move move = (Move) message.getPayload();
                logger.info("Move received: " + move.toString());
                this.isValidMove(move);
                break;
            case SYNC:
                logger.info("sync order received");
                if (!(message.getPayload() instanceof SyncPackage)) {
                    //handle error
                    return;
                }
                SyncPackage syncPackage = (SyncPackage) message.getPayload();
                Map<Vector, AbaloneBoard.Owner> map = syncPackage.getBoard();
                Vector middle = new Vector(0, 0);
                //checking if contained map has right types
                if (map.containsKey(middle) &&
                        (map.get(middle) != null)) {
                    ObservableMap<Vector, AbaloneBoard.Owner> received = new ObservableMap<>(map);
                    logger.info("synchronization complete");
                    replacePlayer(syncPackage.getPlayerOne());
                    replacePlayer(syncPackage.getPlayerTwo());
                    this.board.setBoard(received);
                }
                break;

            case SYNC_REQUEST:
                logger.info("replying to sync request");
                SyncPackage syncPackageToSend = new SyncPackage(
                        new LinkedHashMap<>(this.board.getBoard()),
                        this.host, this.peer
                );
                sendNetworkMessage(Message.MessageType.SYNC, syncPackageToSend);
                break;

            case WIN:
                if (winFlag) {
                    sendNetworkMessage(Message.MessageType.CONFIRM_WIN, null);
                    confirmWinSent = true;
                } else {
                    sendNetworkMessage(Message.MessageType.ERROR, null);
                }
                break;

            case CONFIRM_WIN:
                if (winFlag) {
                    if (!confirmWinSent) {
                        sendNetworkMessage(Message.MessageType.CONFIRM_WIN, null);
                        confirmWinSent = true;
                    }
                    boardPublisher.sendMessage(BoardEvent.winEvent());
                    confirmedWin = true;

                } else {
                    errorPublisher.sendMessage(ErrorEvent.syncError());
                }
                break;

            case INIT:
                if (timer != null) {
                    timer.cancel();
                }
                if (this.board == null) {
                    if (!(message.getPayload() instanceof InitPackage)) {
                        errorPublisher.sendMessage(ErrorEvent.syncError());
                        sendNetworkMessage(Message.MessageType.SYNC_REQUEST, null);
                    }
                    InitPackage initPackage = (InitPackage) message.getPayload();
                    this.size = initPackage.getGameSize();
                    this.board = new AbaloneBoard(size);
                    logger.info("AbaloneBoard set up");
                    if (initPackage.getYourColor() != null) {
                        logger.info("non-standard color received. Setting up....");
                        initPlayer(initPackage.getYourColor(), 2 * size / 3);
                        active = host;

                    } else {
                        AbaloneBoard.Owner myColor =
                                NETWORK.getClientType() == Network.ClientType.HOST ?
                                        AbaloneBoard.Owner.PLAYER_BLACK :
                                        AbaloneBoard.Owner.PLAYER_WHITE;
                        initPlayer(myColor, 2 * size / 3);
                        active = host;
                    }
                    logger.info("sending BOARD_READY event");
                    boardPublisher.sendMessage(BoardEvent.boardReady());
                    break;
                }
                break;

            case RDY:
                InitPackage initPackage = new InitPackage(size, colorToSend);
                sendNetworkMessage(Message.MessageType.INIT, initPackage);
                break;
        }
    }

    @Override
    public void addObserver(GameObserver o) {
        super.addObserver(o);
        logger.info("GameObserver " + o.getClass().getName() + " attached");
        if (this.gameObservers.size() > 1) {
            return;
        }
        if (this.localPlayer == host) {
            start();
        } else {
            this.notifyValidClicks(new LinkedList<>());
        }
    }

    @Override
    protected void notifyValidClicks(List<Vector> validClicks) {
        for (GameObserver o : this.gameObservers) {
            logger.info("sending valid clicks: " + validClicks.toString());
            o.setValidClicks(validClicks);
        }
    }

    @Override
    protected void notifyValidMoves(List<Vector> validMoves) {
        for (GameObserver o : this.gameObservers) {
            logger.info("sending valid moves: " + validMoves.toString());
            o.setValidMoves(validMoves);
        }
    }

    @Override
    protected void notifyEndTurn(AbaloneBoard.Owner next) {
        for (GameObserver o : this.gameObservers) {
            o.endTurn(next);
        }
    }

    private void initPlayer(AbaloneBoard.Owner myColor, int piecesToWin) {

        AbaloneBoard.Owner otherColor = getOtherColor(myColor);

        if (NETWORK.getClientType() == Network.ClientType.HOST) {
            host = new Player(myColor, true, piecesToWin, true);
            peer = new Player(otherColor, false, piecesToWin, false);
            localPlayer = host;
        } else {
            host = new Player(otherColor, true, piecesToWin, false);
            peer = new Player(myColor, false, piecesToWin, true);
            localPlayer = peer;
        }
    }

    private AbaloneBoard.Owner getOtherColor(AbaloneBoard.Owner myColor) {
        return myColor == AbaloneBoard.Owner.PLAYER_BLACK ?
                AbaloneBoard.Owner.PLAYER_WHITE :
                AbaloneBoard.Owner.PLAYER_BLACK;
    }


    /**
     * initializes subscriptions to all necessary Observers
     */
    private void initializeSubscriptions() {
        //subscribe TO ALL THE OBSERVABLES
        NETWORK.setObserver(this);
    }

    private void replacePlayer(Player newPlayer) {
        boolean isLocalPlayer = false;
        if (newPlayer.getOwner().equals(localPlayer.getOwner())) {
            isLocalPlayer = true;
        }
        if (newPlayer.getOwner().equals(host.getOwner())) {
            host = new Player(newPlayer.getOwner(),
                    true, newPlayer.getPiecesToWin(), isLocalPlayer);
            if (isLocalPlayer) {
                localPlayer = host;
            }
        } else {
            peer = new Player(newPlayer.getOwner(),
                    false, newPlayer.getPiecesToWin(), isLocalPlayer);
            if (isLocalPlayer) {
                localPlayer = peer;
            }
        }
    }

    /**
     * implementation of the isValidMove method utilizing the {@link Move} data class.
     * Used to validate Moves and display clickable areas
     *
     * @param move Move data class sent by View.
     *             Contains selected pebbles and optionally the target area of a move
     */
    public synchronized void isValidMove(Move move) {

        this.notifyValidClicks(
                Context.getValidClicks(board.getBoard(), move.getSelected(),
                        active.getOwner()));
        this.notifyValidMoves(translateDirectionsToVectors(
                Context.getValidMoves(board.getBoard(),
                        move.getSelected(), active.getOwner()),
                move.getSelected()));

        if (move.getSelected() == null) {
            return;
        }
        if (move.getTarget() != null) {
            Vector.Direction moveDirection = getDirectionOfMove(
                    move.getSelected(), move.getTarget());
            if ((Context.isValidMove(board.getBoard(), move.getSelected(),
                    moveDirection, active.getOwner()))) {
                endTurn(move);
            } else {
                errorPublisher.sendMessage(ErrorEvent.syncError());
            }

        }
    }

    /**
     * Method to convert a returned List of Directions into a List
     * of Vectors to send to View
     *
     * @param validMoves List of Directions returned by {@link Context}
     * @param selected   List of selected pebbles
     * @return A List of Vectors containing all valid Moves in Vector Form
     */
    private List<Vector> translateDirectionsToVectors(List<Vector.Direction> validMoves,
                                                      List<Vector> selected) {
        List<Vector> out = new LinkedList<>();
        for (Vector selection : selected) {
            for (Vector.Direction direction : validMoves) {
                if (!selected.contains(selection.go(direction))
                        && !out.contains(selection.go(direction))) {
                    out.add(selection.go(direction));
                }
            }
        }
        return out;
    }


    /**
     * Helper method to immediately send valid clicks to the view when the game
     * has started.
     */
    private void start() {
        this.notifyValidClicks(Context.getValidClicks(board.getBoard(),
                new LinkedList<>(), active.getOwner()));
    }


    /**
     * Translates a clicked target into a direction relative to the
     * starting pebble of the selected pebbles.
     *
     * @param selected the selected pebbles of this move
     * @param target   the target to move to
     * @return The direction of the move
     * @see AbaloneGame#getStartingPebble(List, Vector)
     */
    private Vector.Direction getDirectionOfMove(List<Vector> selected,
                                                Vector target) {
        Vector leading = getStartingPebble(selected, target);


        return Vector.getDirectionOfMove(leading, target);
    }

    /**
     * The starting pebble (basically the "leading" or "most important" pebble)
     * of the move is always the most north-western pebble of the selection.
     * It will be used as the root of a move.
     *
     * @param selected the selected pebbles
     * @return the starting pebble
     */
    private Vector getStartingPebble(List<Vector> selected, Vector target) {
        Vector leading = null;
        boolean downMove = isDownMove(selected, target);
        boolean rightMove = isRightMove(selected, target);

        for (Vector selection : selected) {
            if (leading == null) {
                leading = selection;
                continue;
            }
            if (!downMove && (leading.getY() > selection.getY())) {
                leading = selection;
            } else if (downMove && (leading.getY() < selection.getY())) {
                leading = selection;
            } else {
                if (!rightMove && (leading.getX() > selection.getX())) {
                    leading = selection;
                } else if (rightMove && (leading.getX() < selection.getX())) {
                    leading = selection;
                }
            }
        }
        logger.info("Starting pebble " + leading + " selected;");
        return leading;
    }

    private boolean isDownMove(List<Vector> selected, Vector target) {
        Vector mostSouthern = selected.get(0);

        for (Vector current : selected) {
            if (current.getY() > mostSouthern.getY()) {
                mostSouthern = current;
            }
        }
        return mostSouthern.getY() < target.getY();
    }

    private boolean isRightMove(List<Vector> selected, Vector target) {
        Vector mostRight = selected.get(0);
        for (Vector current : selected) {
            if (current.getX() > mostRight.getX()) {
                mostRight = current;
            }
        }
        return mostRight.getX() < target.getX();
    }

    /**
     * Method to perform post-validation actions of the turn.
     * These include:
     * - Calculating new Positions on the game board
     * - Modifying the board model to reflect the move
     * - Sending the move to the peer player via the {@link Network}
     * instance of the class
     *
     * @param move the player-chosen validated move
     */
    private void endTurn(Move move) {
        Map<Vector, AbaloneBoard.Owner> updated = calculateNewPositions(move);
        for (Map.Entry<Vector, AbaloneBoard.Owner> update : updated.entrySet()) {
            if (board.getOwner(update.getKey()) != update.getValue()) {
                board.setOwner(update.getKey(), update.getValue());
            }
        }
        if (this.active == localPlayer) {
            sendNetworkMessage(Message.MessageType.MOVE, move);
        }

        if (winFlag) {
            return;
        }
        this.notifyEndTurn(getInactive().getOwner());
        nextPlayer();
    }

    /**
     * Method to toggle the active player
     */
    private void nextPlayer() {
        //active is a pointer to either host or peer
        //that's why == is used
        if (active == host) {
            active = peer;
        } else {
            active = host;
        }
        if (active.isLocalPlayer()) {
            start();
        }
    }

    /**
     * Method to calculate the new Positions of moved pebbles based of a sent Move.
     *
     * @param move The Move to perform
     * @return A Map containing the changed board layout
     */
    private Map<Vector, AbaloneBoard.Owner> calculateNewPositions(Move move) {
        Map<Vector, AbaloneBoard.Owner> board
                = new LinkedHashMap<>(this.board.getBoard());
        Vector startingPoint = getStartingPebble(move.getSelected(),
                move.getTarget());
        Vector.Direction direction = Vector.getDirectionOfMove(startingPoint,
                move.getTarget());
        logger.info("Direction of move selected as " + direction);
        Vector firstInLine = null;
        for (Vector pebble : move.getSelected()) {
            if (!move.getSelected().contains(pebble.go(direction))) {
                firstInLine = pebble;
            }
        }

        assert firstInLine != null;
        Vector next = firstInLine.invertGo(direction);

        if (isInline(move.getSelected(), direction)) {
            while (move.getSelected().contains(firstInLine)) {
                logger.info("Moving " + firstInLine + " " + direction);
                performMove(board, firstInLine, direction);
                firstInLine = next;
                next = firstInLine.invertGo(direction);
            }
        } else {
            for (Vector pebble : move.getSelected()) {
                logger.info("Moving " + firstInLine + " " + direction);
                performMove(board, pebble, direction);
            }
        }

        return board;
    }

    /**
     * Checks if move is a inline Move.
     * This check is required by {@link AbaloneGame#performMove(Map, Vector,
     * Vector.Direction)}
     * to select the movement order of the pebbles.
     *
     * @param selected  The selected pebbles
     * @param direction The direction to move into
     * @return {@code true} if move is inline
     */
    private boolean isInline(List<Vector> selected, Vector.Direction direction) {
        return (selected.contains(selected.get(0).go(direction))
                || selected.contains(selected.get(0).invertGo(direction)));
    }

    /**
     * Method used to move one pebble in a chosen direction and updating the board.
     * This method also handles the pushing of enemy pebbles as well as removed
     * pebbles.
     *
     * @param board     the current board
     * @param pebble    the pebble to move
     * @param direction the direction to move in
     */
    private void performMove(Map<Vector, AbaloneBoard.Owner> board, Vector pebble,
                             Vector.Direction direction) {
        Vector moveTo = pebble.go(direction);
        if (!board.get(moveTo).equals(AbaloneBoard.Owner.EMPTY)) {
            pushEnemyLine(board, pebble, direction);
        }
        board.put(moveTo, active.getOwner());
        board.put(pebble, AbaloneBoard.Owner.EMPTY);
    }

    private void pushEnemyLine(Map<Vector, AbaloneBoard.Owner> board, Vector pebble,
                               Vector.Direction direction) {
        int i = 0;
        Vector[] toMove = new Vector[2];
        Vector curr = pebble.go(direction);
        while (board.get(curr).equals(getInactive().getOwner()) && i < 2) {
            toMove[i] = curr;
            i++;
            curr = curr.go(direction);
            if (!board.containsKey(curr)) {
                break;
            }
        }
        for (i = i - 1; i >= 0; i--) {
            curr = toMove[i];
            Vector newPosition = curr.go(direction);
            board.replace(curr, AbaloneBoard.Owner.EMPTY);
            if (!board.containsKey(newPosition)) {
                handleRemovedPebble();
                continue;
            }
            board.replace(newPosition, getInactive().getOwner());
        }
    }

    /**
     * Handles the removal of a pebble.
     * Causes the players score to rise and triggers the win flow
     * if conditions are met
     */
    private void handleRemovedPebble() {
        active.didScore();
        if (active.getPiecesToWin() <= 0) {
            winFlag = true;
            logger.info("GAME WON");
            logger.info(this.board.getBoard().toString());
            sendNetworkMessage(Message.MessageType.WIN, null);
        }
    }

    public AbaloneBoard getBoard() {
        return board;
    }

    public int getSize() {
        return size;
    }

    public AbaloneBoard.Owner getSwitchedPlayerColor() {
        return getOtherColor(localPlayer.getOwner());
    }

    private Player getInactive() {
        if (active != host) {
            return host;
        } else {
            return peer;
        }
    }

    public Player getActivePlayer() {
        return this.active;
    }

    public Player getLocalPlayer() {
        return this.localPlayer;
    }

    private void sendNetworkMessage(Message.MessageType type, Serializable payload) {
        try {
            this.NETWORK.send(new Message(type, payload));
        } catch (IOException e) {
            logger.severe("Error while sending message to peer");
            e.printStackTrace();
            //more handling?
        }
    }


}