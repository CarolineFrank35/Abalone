package views;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.AbaloneGame;
import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.network.Network;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import de.lmu.ifi.sep.abalone.views.AbaloneView;
import de.lmu.ifi.sep.abalone.views.RoundButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Abalone View")
class AbaloneViewTest {
    private static Network hostNetwork, guestNetwork;
    private static AbaloneGame hostGame;
    private static AbaloneView hostView;
    private static EventBus eventBus;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        eventBus = new EventBus();
        hostNetwork = new Network(NetworkUtilities.getDefaultPort(), new EventPublisher<>(eventBus));
        guestNetwork = new Network("127.0.0.1", NetworkUtilities.getDefaultPort(), new EventPublisher<>(eventBus));

        hostNetwork.connect(() -> hostGame = new AbaloneGame(8, hostNetwork, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus)));
        guestNetwork.connect(() -> new AbaloneGame(0, guestNetwork, new EventPublisher<>(eventBus),
                new EventPublisher<>(eventBus)));

        Thread.sleep(2000);

        hostView = new AbaloneView(hostGame);
    }

    @Test
    @DisplayName("addSelectedButton()")
    void addSelectedButtonTest() {
        RoundButton b1 = new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, hostView, new Vector(-2, 2));
        RoundButton b2 = new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, hostView, new Vector(-2, 3));
        RoundButton b3 = new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, hostView, new Vector(-2, 4));

        b1.setSelected(true);
        b2.setSelected(true);
        b3.setSelected(true);

        List<RoundButton> buttons = new ArrayList<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);

        buttons.forEach(button -> hostView.addSelectedButton(button));

        Assertions.assertEquals(buttons, hostView.getSelected());
    }

    @Test
    @DisplayName("setCursor()")
    void setCursorTest() {
        hostView.setCursor(1);
        Assertions.assertEquals(Cursor.HAND_CURSOR, hostView.getCursor().getType());

        hostView.setCursor(2);
        Assertions.assertEquals(Cursor.CROSSHAIR_CURSOR, hostView.getCursor().getType());

        hostView.setCursor(3);
        Assertions.assertEquals(Cursor.DEFAULT_CURSOR, hostView.getCursor().getType());

        hostView.setCursor(50);
        Assertions.assertEquals(Cursor.DEFAULT_CURSOR, hostView.getCursor().getType());
    }

    @Test
    @DisplayName("getButton()")
    void getButtonTest() {
        Assertions.assertNotNull(hostView.getButton(new Vector(0, 0)));
        Assertions.assertNotNull(hostView.getButton(new Vector(0, 1)));
        Assertions.assertNotNull(hostView.getButton(new Vector(0, 2)));
        Assertions.assertNotNull(hostView.getButton(new Vector(0, 3)));
        Assertions.assertNotNull(hostView.getButton(new Vector(0, 4)));
        Assertions.assertNotNull(hostView.getButton(new Vector(1, 2)));
        Assertions.assertNotNull(hostView.getButton(new Vector(1, 3)));
        Assertions.assertNotNull(hostView.getButton(new Vector(2, 1)));
        Assertions.assertNotNull(hostView.getButton(new Vector(2, 2)));

        Assertions.assertNull(hostView.getButton(new Vector(0, 5)));
        Assertions.assertNull(hostView.getButton(new Vector(1, 4)));
        Assertions.assertNull(hostView.getButton(new Vector(2, 3)));
        Assertions.assertNull(hostView.getButton(new Vector(2, 4)));
    }
}
