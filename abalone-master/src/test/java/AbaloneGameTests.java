import de.lmu.ifi.sep.abalone.components.GameObserver;
import de.lmu.ifi.sep.abalone.components.ObservableMap;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.AbaloneGame;
import de.lmu.ifi.sep.abalone.logic.Player;
import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.InitPackage;
import de.lmu.ifi.sep.abalone.logic.communication.Move;
import de.lmu.ifi.sep.abalone.logic.communication.SyncPackage;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.network.Network;
import de.lmu.ifi.sep.abalone.network.NetworkObserver;
import de.lmu.ifi.sep.abalone.network.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Abalone Game")
class AbaloneGameTests {

    private AbaloneGame abaloneGame;
    private NetworkMock network;
    private EventBus eventBus;

    AbaloneGameTests() {

    }

    @BeforeEach
    void setup() {
        //network.reset();
        network = new NetworkMock();
        eventBus = new EventBus();

        abaloneGame = new AbaloneGame(9, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        network.reset();
    }

    @Test
    @DisplayName("Initialization Test")
    void initializationTest() {
        assertThat(abaloneGame).isNotNull();
    }

    @Test
    @DisplayName("isValidMove triggers GameObservable")
    void emptyBoardClickableNormalSize() {
        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);
        Move move = new Move(new LinkedList<>(), null);
        abaloneGame.isValidMove(move);

        assertThat(tgo.getValidClicks()).isNotNull();
    }

    @Test
    @DisplayName("Null safety when Observer list is empty")
    void emptyObserverList() {
        Move move = new Move(new LinkedList<>(), null);
        abaloneGame.isValidMove(move);
    }


    @Test
    @DisplayName("Direction to Vector adapter")
    void directionToVectorAdapter() {
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-1, 2));
        selected.add(new Vector(0, 2));
        Move move = new Move(selected, null);
        TestGameObserver testGameObserver = new TestGameObserver();
        abaloneGame.addObserver(testGameObserver);
        abaloneGame.isValidMove(move);
        Vector expected = new Vector(-1, 1);
        Vector expected2 = new Vector(0, 1);
        assertThat(testGameObserver.getValidMoves()).contains(expected);
        assertThat(testGameObserver.getValidMoves()).contains(expected2);
    }

    @Test
    @DisplayName("Moving to empty field")
    void moveEmptyField() {
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-2, 2));
        selected.add(new Vector(-1, 2));
        selected.add(new Vector(0, 2));
        Move move = new Move(selected, new Vector(-1, 1));
        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);
        abaloneGame.isValidMove(move);

        assertThat(tgo.hasTurnEnded()).isTrue();
        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();

        assertThat(board.get(new Vector(-1, 1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(0, 1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(1, 1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);

    }

    @Test
    @DisplayName("Replication of one move for issue #17")
    void replication17() {
        abaloneGame = new AbaloneGame(7, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        network.reset();
        network.reset();
        List<Vector> selected = new LinkedList<>();
        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();
        // is empty
        assertThat(board.get(new Vector(1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.EMPTY);

        selected.add(new Vector(0, 1));
        Move move = new Move(selected, new Vector(1, 0));
        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);
        abaloneGame.isValidMove(move);


        selected = new LinkedList<>();
        selected.add(new Vector(1, -3));
        selected.add(new Vector(1, -2));
        selected.add(new Vector(1, -1));
        Move toSend = new Move(selected, new Vector(1, 0));
        network.triggerMessage(new Message(Message.MessageType.MOVE, toSend));
        // after move is player black
        assertThat(board.get(new Vector(1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
    }

    @Test
    @DisplayName("Replication of one move for issue #24")
    void replication24() {
        abaloneGame = new AbaloneGame(9, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        network.reset();
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-2, 2));
        selected.add(new Vector(-1, 2));
        selected.add(new Vector(0, 2));
        // move east
        Move move = new Move(selected, new Vector(-3, 2));

        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);
        abaloneGame.isValidMove(move);

        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();
        assertThat(board.get(new Vector(-3, 2))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
    }

    @Test
    @DisplayName("Replication of one move for issue #31")
    void replication31() {
        abaloneGame = new AbaloneGame(9, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();
        network.reset();

        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);

        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-2, 2));
        selected.add(new Vector(-2, 3));
        selected.add(new Vector(-2, 4));
        Move move = new Move(selected, null);
        abaloneGame.isValidMove(move);

        selected.remove(new Vector(-2, 3));
        move = new Move(selected, null);
        abaloneGame.isValidMove(move);

        selected.remove(new Vector(-2, 4));
        move = new Move(selected, null);
        abaloneGame.isValidMove(move);

        move = new Move(selected, new Vector(-2, 1));
        abaloneGame.isValidMove(move);
        assertThat(board.get(new Vector(-2, 1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
    }

    @Test
    @DisplayName("Replication of move loop bug")
    void replicationMoveLoop() throws IOException {
        abaloneGame = new AbaloneGame(7, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        network.reset();
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(0, 1));
        Move move = new Move(selected, new Vector(1, 0));
        TestGameObserver tgo = new TestGameObserver();
        abaloneGame.addObserver(tgo);
        abaloneGame.isValidMove(move);
        NetworkMock networkMock2 = new NetworkMock();
        networkMock2.setClientType(Network.ClientType.GUEST);
        new AbaloneGame(9, networkMock2, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        networkMock2.triggerMessage(new Message(Message.MessageType.INIT,
                new InitPackage(9, null)));
        networkMock2.reset();
        assertThat(network.getSendCalledTimes()).isEqualTo(1);
        assertThat(networkMock2.getSendCalledTimes()).isEqualTo(0);
        networkMock2.triggerMessage(new Message(Message.MessageType.SYNC,
                new LinkedHashMap<>(abaloneGame.getBoard().getBoard())));
        networkMock2.reset();
        networkMock2.triggerMessage(new Message(Message.MessageType.MOVE, move));
        System.out.println(networkMock2.getSendCalledWith());
        assertThat(networkMock2.getSendCalledTimes()).isEqualTo(0);
    }


    @DisplayName("Horizontal Eastern Move #24 #31")
    @Test
    void replicationMoveEastern() {
        abaloneGame = new AbaloneGame(7, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-1, 1));
        selected.add(new Vector(0, 1));
        Move move = new Move(selected, new Vector(1, 1));

        abaloneGame.isValidMove(move);

        assertThat(abaloneGame.getBoard().getBoard().get(new Vector(1, 1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
    }

    @DisplayName("Push Test 1v2")
    @Test
    void push1v2() {
        abaloneGame = new AbaloneGame(7, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();
        for (Map.Entry<Vector, AbaloneBoard.Owner> entry : board.entrySet()) {
            board.replace(entry.getKey(), AbaloneBoard.Owner.EMPTY);
        }
        board.replace(new Vector(-1, 0), AbaloneBoard.Owner.PLAYER_BLACK);
        board.replace(new Vector(-2, 0), AbaloneBoard.Owner.PLAYER_BLACK);
        board.replace(new Vector(0, 0), AbaloneBoard.Owner.PLAYER_WHITE);
        network.triggerMessage(new Message(Message.MessageType.SYNC, new LinkedHashMap<>(board)));
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-1, 0));
        selected.add(new Vector(-2, 0));
        Move move = new Move(selected, new Vector(0, 0));
        abaloneGame.isValidMove(move);
        board = abaloneGame.getBoard().getBoard();
        assertThat(board.get(new Vector(-2, 0))).isEqualByComparingTo(AbaloneBoard.Owner.EMPTY);
        assertThat(board.get(new Vector(-1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(0, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
    }

    @DisplayName("Push Test 2v3")
    @Test
    void push2v3() {
        abaloneGame = new AbaloneGame(7, network, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus));
        Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();
        for (Map.Entry<Vector, AbaloneBoard.Owner> entry : board.entrySet()) {
            board.replace(entry.getKey(), AbaloneBoard.Owner.EMPTY);
        }
        board.replace(new Vector(-1, 0), AbaloneBoard.Owner.PLAYER_BLACK);
        board.replace(new Vector(-2, 0), AbaloneBoard.Owner.PLAYER_BLACK);
        board.replace(new Vector(0, 0), AbaloneBoard.Owner.PLAYER_BLACK);
        board.replace(new Vector(1, 0), AbaloneBoard.Owner.PLAYER_WHITE);
        board.replace(new Vector(2, 0), AbaloneBoard.Owner.PLAYER_WHITE);
        network.triggerMessage(new Message(Message.MessageType.SYNC, new LinkedHashMap<>(board)));
        List<Vector> selected = new LinkedList<>();
        selected.add(new Vector(-1, 0));
        selected.add(new Vector(-2, 0));
        selected.add(new Vector(0, 0));
        Move move = new Move(selected, new Vector(1, 0));
        abaloneGame.isValidMove(move);
        board = abaloneGame.getBoard().getBoard();
        assertThat(board.get(new Vector(-2, 0))).isEqualByComparingTo(AbaloneBoard.Owner.EMPTY);
        assertThat(board.get(new Vector(-1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(0, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(1, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_BLACK);
        assertThat(board.get(new Vector(2, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
        assertThat(board.get(new Vector(3, 0))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
    }

    @Nested
    @DisplayName("Network tests")
    class NetworkTests {
        @Test
        @DisplayName("Move with target gets sent to peer")
        void moveTargetNetwork() {
            List<Vector> selected = new LinkedList<>();
            selected.add(new Vector(-2, 2));
            selected.add(new Vector(-1, 2));
            Move move = new Move(selected, new Vector(-1, 1));
            abaloneGame.isValidMove(move);
            int actualSentTimes = network.getSendCalledTimes();
            assertThat(actualSentTimes).isEqualTo(1);
            assertThat(network.getSendCalledWith()).isInstanceOf(Message.class);
        }

        @Test
        @DisplayName("Move without target does not get sent to peer")
        void moveNoTargetNetwork() {
            List<Vector> selected = new LinkedList<>();
            selected.add(new Vector(0, 0));
            selected.add(new Vector(1, 0));
            Move move = new Move(selected, null);
            abaloneGame.isValidMove(move);
            assertThat(network.getSendCalledTimes()).isEqualTo(0);
        }

        @Test
        @DisplayName("Receiving a MOVE message")
        void receivedMove() {
            //perform simple move
            List<Vector> selected = new LinkedList<>();
            selected.add(new Vector(-2, 2));
            selected.add(new Vector(-1, 2));
            selected.add(new Vector(0, 2));
            Move move = new Move(selected, new Vector(-1, 1));
            TestGameObserver tgo = new TestGameObserver();
            abaloneGame.addObserver(tgo);
            abaloneGame.isValidMove(move);

            List<Vector> enemySelected = new LinkedList<>();
            enemySelected.add(new Vector(2, -2));
            enemySelected.add(new Vector(1, -2));
            enemySelected.add(new Vector(0, -2));
            Move enemyMove = new Move(enemySelected, new Vector(0, -1));
            Message moveMessage = new Message(Message.MessageType.MOVE, enemyMove);
            network.triggerMessage(moveMessage);


            Map<Vector, AbaloneBoard.Owner> board = abaloneGame.getBoard().getBoard();


            assertThat(board.get(new Vector(0, -1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
            assertThat(board.get(new Vector(1, -1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
            assertThat(board.get(new Vector(2, -1))).isEqualByComparingTo(AbaloneBoard.Owner.PLAYER_WHITE);
        }

        @Test
        @DisplayName("Receiving a SYNC message")
        void receivedSync() {
            Map<Vector, AbaloneBoard.Owner> board = new ObservableMap<>(abaloneGame.getBoard().getBoard());
            //perform simple move
            List<Vector> selected = new LinkedList<>();
            selected.add(new Vector(-2, 2));
            selected.add(new Vector(-1, 2));
            selected.add(new Vector(0, 2));
            Move move = new Move(selected, new Vector(-1, 1));
            TestGameObserver tgo = new TestGameObserver();
            abaloneGame.addObserver(tgo);
            abaloneGame.isValidMove(move);

            SyncPackage toSend = new SyncPackage(new LinkedHashMap<>(board),
                    new Player(AbaloneBoard.Owner.PLAYER_WHITE, false, 9, false),
                    new Player(AbaloneBoard.Owner.PLAYER_BLACK, true, 0, true));

            Message syncMessage = new Message(Message.MessageType.SYNC, toSend);

            network.triggerMessage(syncMessage);

            //assertThat(abaloneGame.getBoard().getBoard()).containsExactly(board.entrySet());
            Set<Map.Entry<Vector, AbaloneBoard.Owner>> entryExpected = board.entrySet();
            Set<Map.Entry<Vector, AbaloneBoard.Owner>> entryActual = abaloneGame.getBoard().getBoard().entrySet();
            assertThat(entryActual.containsAll(entryExpected)).isTrue();
        }

        @Test
        @DisplayName("Receiving a SYNC_REQUEST message")
        void receivedSyncReq() {
            network.triggerMessage(new Message(Message.MessageType.SYNC_REQUEST, null));
            assertThat(network.getSendCalledTimes()).isEqualTo(1);
            assertThat(network.getSendCalledWith()).extracting("messageType")
                    .containsExactly(Message.MessageType.SYNC);
            //assertThat(network.getSendCalledWith()).extracting("payload")
            //        .containsExactly(abaloneGame.getBoard().getBoard());
            SyncPackage sentPackage = (SyncPackage) ((Message) network.getSendCalledWith()).getPayload();
            assertThat(sentPackage.getBoard().entrySet().containsAll(abaloneGame.getBoard().getBoard().entrySet())).isTrue();
        }

        @Test
        @DisplayName("Receiving a SyncPackage should update the right player")
        void receiveSyncPlayerChange() {
            Map<Vector, AbaloneBoard.Owner> board = new ObservableMap<>(abaloneGame.getBoard().getBoard());
            //perform simple move
            List<Vector> selected = new LinkedList<>();
            selected.add(new Vector(-2, 2));
            selected.add(new Vector(-1, 2));
            selected.add(new Vector(0, 2));
            Move move = new Move(selected, new Vector(-1, 1));
            TestGameObserver tgo = new TestGameObserver();
            abaloneGame.addObserver(tgo);
            abaloneGame.isValidMove(move);

            SyncPackage toSend = new SyncPackage(new LinkedHashMap<>(board),
                    new Player(AbaloneBoard.Owner.PLAYER_WHITE, false, 9, true),
                    new Player(AbaloneBoard.Owner.PLAYER_BLACK, true, 1337, false));

            Message syncMessage = new Message(Message.MessageType.SYNC, toSend);
            network.triggerMessage(syncMessage);

            assertThat(abaloneGame).extracting("localPlayer").element(0)
                    .isEqualToComparingFieldByField(new Player(AbaloneBoard.Owner.PLAYER_BLACK, true, 1337, true));
        }
    }

    @Nested
    @DisplayName("Handling win events")
    class WinEvents {


    }

}

class TestGameObserver implements GameObserver {

    private List<Vector> validMoves;
    private List<Vector> validClicks;
    private boolean turnEnded = false;

    List<Vector> getValidMoves() {
        return validMoves;
    }

    @Override
    public void setValidMoves(List<Vector> validMoves) {
        this.validMoves = validMoves;
    }

    List<Vector> getValidClicks() {
        return validClicks;
    }

    @Override
    public void setValidClicks(List<Vector> validClicks) {
        this.validClicks = validClicks;
    }

    @Override
    public void endTurn(AbaloneBoard.Owner o) {
        turnEnded = true;
    }

    boolean hasTurnEnded() {
        boolean out = turnEnded;
        turnEnded = false;
        return out;
    }
}

class NetworkMock extends Network {

    private NetworkObserver observer = null;

    private int sendCalledTimes = 0;
    private Object sendCalledWith = null;
    private ClientType clientType = ClientType.HOST;

    NetworkMock() {
        super("9999", new EventPublisher<>(new EventBus()));
    }

    void triggerMessage(Message message) {
        this.observer.receive(message);
    }

    @Override
    public ClientType getClientType() {
        return this.clientType;
    }

    @Override
    public void setObserver(NetworkObserver observer) {
        this.observer = observer;
    }

    @Override
    public void send(Message message) {
        this.sendCalledTimes++;
        this.sendCalledWith = message;
    }

    void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    int getSendCalledTimes() {
        return sendCalledTimes;
    }

    Object getSendCalledWith() {
        return sendCalledWith;
    }

    void reset() {
        this.sendCalledWith = null;
        this.sendCalledTimes = 0;
        this.clientType = ClientType.HOST;
    }

}