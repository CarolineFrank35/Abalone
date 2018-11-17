package views;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.views.RoundButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Round Button")
class RoundButtonTest {

    private List<RoundButton> differentButtons;
    private List<RoundButton> equalButtons;

    @BeforeEach
    void setup() {
        differentButtons = new ArrayList<>();

        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 2)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_BLACK, null, null, new Vector(1, 3)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_BLACK, null, null, new Vector(2, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(2, 2)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(2, 3)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(3, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_BLACK, null, null, new Vector(3, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(3, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(4, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(4, 2)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(4, 3)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(5, 7)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_BLACK, null, null, new Vector(5, 7)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(5, 5)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(6, 1)));
        differentButtons.add(new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(7, 2)));

        equalButtons = new ArrayList<>();
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
        equalButtons.add(new RoundButton(AbaloneBoard.Owner.PLAYER_WHITE, null, null, new Vector(1, 1)));
    }

    @Test
    @DisplayName("compareTo()")
    void compareToTest() {
        RoundButton button = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(1, 1));

        RoundButton button1 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(1, 1));
        Assertions.assertEquals(0, button.compareTo(button1));

        RoundButton button2 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(1, 0));
        Assertions.assertEquals(1, button1.compareTo(button2));

        RoundButton button3 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(1, 2));
        Assertions.assertEquals(-1, button1.compareTo(button3));

        RoundButton button4 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(2, 0));
        Assertions.assertEquals(1, button1.compareTo(button4));

        RoundButton button5 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(2, 2));
        Assertions.assertEquals(-1, button1.compareTo(button5));

        RoundButton button6 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(0, 0));
        Assertions.assertEquals(-1, button1.compareTo(button6));

        RoundButton button7 = new RoundButton(AbaloneBoard.Owner.EMPTY, null, null, new Vector(0, 2));
        Assertions.assertEquals(1, button1.compareTo(button7));
    }

    @Test
    @DisplayName("contains()")
    void containsTest() {
        int radius = 19;

        RoundButton button = equalButtons.get(0);
        button.setBounds(0, 0, radius * 2, radius * 2);

        Assertions.assertEquals(radius * 2, button.getHeight());
        Assertions.assertEquals(radius * 2, button.getWidth());

        for (int i = 0; i < 360; i++) {
            //x1 = x0 + (Math.cos(angle) * radius) - x coordinate of point on circle
            //y1 = y0 + (Math.sin(angle) * radius) - y coordinate of point on circle

            int x_outer = (int) Math.floor(radius + Math.cos(i) * (radius + 2));
            int y_outer = (int) Math.floor(radius + Math.sin(i) * (radius + 2));

            int x_inner = (int) Math.ceil(radius + (Math.cos(i) * (radius - 2)));
            int y_inner = (int) Math.ceil(radius + (Math.sin(i) * (radius - 2)));

            Assertions.assertFalse(button.contains(x_outer, y_outer));
            Assertions.assertTrue(button.contains(x_inner, y_inner));
        }
    }

    @Test
    @DisplayName("isSelected()")
    void isSelectedTest() {
        differentButtons.addAll(equalButtons);
        for (RoundButton button : differentButtons) {
            Assertions.assertFalse(button.isSelected());

            button.setSelected(true);
            Assertions.assertTrue(button.isSelected());

            button.setSelected(true);
            Assertions.assertTrue(button.isSelected());

            button.setSelected(false);
            Assertions.assertFalse(button.isSelected());

            button.setSelected(false);
            Assertions.assertFalse(button.isSelected());
        }
    }

    @Test
    @DisplayName("getPosition()")
    void getPositionTest() {
        Vector v = equalButtons.get(0).getPosition();

        for (RoundButton button : equalButtons) {
            Assertions.assertEquals(v, button.getPosition());
        }

        for (RoundButton button : differentButtons) {
            Assertions.assertNotNull(button.getPosition());
        }
    }

    @Test
    @DisplayName("isEnabled()")
    void isEnabledTest() {
        differentButtons.addAll(equalButtons);
        for (RoundButton button : differentButtons) {
            Assertions.assertFalse(button.isEnabled());

            button.setEnabled(true);
            Assertions.assertTrue(button.isEnabled());

            button.setEnabled(true);
            Assertions.assertTrue(button.isEnabled());

            button.setEnabled(false);
            Assertions.assertFalse(button.isEnabled());

            button.setEnabled(false);
            Assertions.assertFalse(button.isEnabled());
        }
    }
}
