package network;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.network.Network;
import de.lmu.ifi.sep.abalone.network.NetworkObserver;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import de.lmu.ifi.sep.abalone.network.message.Message;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@DisplayName("Network Class")
class NetworkTest {
    private static final Message message = new Message(Message.MessageType.MOVE, "test");
    private static final NetworkObserver networkObserver =
            message -> Assertions.assertEquals(message.getMessageType(), message.getMessageType());
    private static Network host;
    private static Network guest;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        EventBus eventBus = new EventBus();
        host = new Network(NetworkUtilities.getDefaultPort(), new EventPublisher<>(eventBus));
        guest = new Network("127.0.0.1", NetworkUtilities.getDefaultPort(), new EventPublisher<>(eventBus));
        CountDownLatch latch = new CountDownLatch(2);

        host.connect(() -> {
            host.setObserver(networkObserver);
            latch.countDown();
        });
        guest.connect(() -> {
            guest.setObserver(networkObserver);
            latch.countDown();
        });
        latch.await(15, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        NetworkUtilities.close(host);
        NetworkUtilities.close(guest);
    }

    @Test
    @DisplayName("Send message from host to guest")
    void hostTest() throws IOException {
        //Thread.sleep(1000);
        host.send(message);
    }

    @Test
    @DisplayName("Send message from guest to host")
    void guestTest() throws IOException {
        //Thread.sleep(1000);
        guest.send(message);
    }
}