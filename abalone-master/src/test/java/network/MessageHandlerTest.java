package network;

import de.lmu.ifi.sep.abalone.network.NetworkObserver;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import de.lmu.ifi.sep.abalone.network.message.Message;
import de.lmu.ifi.sep.abalone.network.message.MessageHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@DisplayName("MessageHandler Class")
class MessageHandlerTest {
    private final Message message = new Message(Message.MessageType.MOVE, "test");
    private final NetworkObserver observer = message -> Assertions.assertEquals(MessageHandlerTest.this.message, message);
    private MockedHandler handler;
    private BlockingDeque<Message> deque;

    @BeforeEach
    void setup() throws InterruptedException {
        deque = new LinkedBlockingDeque<>();
        CountDownLatch latch = new CountDownLatch(1);
        handler = new MockedHandler(deque, observer, latch);
        handler.start();
        latch.await(15, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("close()::void")
    void closeTest() {
        Assertions.assertTimeout(Duration.ofSeconds(15), () -> {
            NetworkUtilities.close(handler);
            handler.join();
            Assertions.assertFalse(handler.isAlive());
        });
    }

    @Test
    @DisplayName("Test message handling")
    void messageHandlerTest() {
        deque.addFirst(message);
        deque.addFirst(message);
        deque.addFirst(message);
        deque.addFirst(message);

        handler.close();
    }
}

class MockedHandler extends MessageHandler {

    private final CountDownLatch latch;

    MockedHandler(BlockingDeque<Message> messageQueue, NetworkObserver observer,
                  CountDownLatch latch) {
        super(messageQueue, observer);
        this.latch = latch;
    }

    @Override
    public void run() {
        latch.countDown();
        super.run();
    }
}

