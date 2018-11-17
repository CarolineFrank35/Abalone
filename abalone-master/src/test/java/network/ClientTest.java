package network;

import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import de.lmu.ifi.sep.abalone.network.clients.GuestClient;
import de.lmu.ifi.sep.abalone.network.clients.HostClient;
import de.lmu.ifi.sep.abalone.network.message.Message;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class ClientTest {
    private final static Logger logger = Logger.getLogger(ClientTest.class.getName());
    //private static final Message message = new Message(Message.MessageType.MOVE, "test");
    private static HostClient host;
    private static GuestClient guest;
    private static EventBus eventBus;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        int port = 12345;
        eventBus = new EventBus();
        host = new HostClient(port, new EventPublisher<>(eventBus));
        guest = new GuestClient(InetAddress.getByName("127.0.0.1"), port, new EventPublisher<>(eventBus));
        CountDownLatch latch = new CountDownLatch(2);

        host.start(() -> {
            logger.info("Connection triggered host");
            latch.countDown();
        });
        guest.start(() -> {
            logger.info("Connection triggered guest");
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
    void hostTest() throws IOException, InterruptedException {
        Message message = new Message(Message.MessageType.MOVE, "test");
        host.send(message);
        Assertions.assertEquals(Message.MessageType.MOVE, guest.getMessageQueue().takeFirst().getMessageType());
    }

    @Test
    @DisplayName("Send message from guest to host")
    void guestTest() throws InterruptedException, IOException {
        Message message = new Message(Message.MessageType.MOVE, "test");
        guest.send(message);
        Assertions.assertEquals(Message.MessageType.MOVE, host.getMessageQueue().takeFirst().getMessageType());
    }


}

