import de.lmu.ifi.sep.abalone.components.ObservableMap;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.components.Vector.Direction;
import de.lmu.ifi.sep.abalone.logic.Context;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Context tests")
class ContextTests {

    private ObservableMap<Vector, Owner> board;
    private List<Vector> currentSelected;

    @BeforeEach
    void setup() {
        currentSelected = new ArrayList<>();
        board = new ObservableMap<>();
        for (int i = 0; i < 5; i++) {
            board.put(new Vector(i, -4), Owner.PLAYER_WHITE);
        }
        for (int i = -1; i < 5; i++) {
            board.put(new Vector(i, -3), Owner.PLAYER_WHITE);
        }
        for (int i = -2; i < 5; i++) {
            board.put(new Vector(i, -2), Owner.PLAYER_WHITE);
        }
        for (int i = -3; i < 5; i++) {
            board.put(new Vector(i, -1), Owner.EMPTY);
        }
        for (int i = -4; i < 5; i++) {
            board.put(new Vector(i, 0), Owner.EMPTY);
        }
        for (int i = -4; i < 4; i++) {
            board.put(new Vector(i, 1), Owner.EMPTY);
        }
        for (int i = -4; i < 3; i++) {
            board.put(new Vector(i, 2), Owner.PLAYER_BLACK);
        }
        for (int i = -4; i < 2; i++) {
            board.put(new Vector(i, 3), Owner.PLAYER_BLACK);
        }
        for (int i = -4; i < 1; i++) {
            board.put(new Vector(i, 4), Owner.PLAYER_BLACK);
        }

    }

    @DisplayName("Test: Valid clicks from fresh board")
    @Test
    void exampleTest() {
        List<Vector> expected = new ArrayList<>();

        for (int i = -4; i < 3; i++) {
            expected.add(new Vector(i, 2));
        }
        for (int i = -4; i < 2; i++) {
            expected.add(new Vector(i, 3));
        }
        for (int i = -4; i < 1; i++) {
            expected.add(new Vector(i, 4));
        }
        List<Vector> validClicks = Context.getValidClicks(board, currentSelected, Owner.PLAYER_BLACK);
        assertThat(validClicks).containsAll(expected);
        currentSelected.add(new Vector(-2, 3));
        currentSelected.add(new Vector(-2, 2));
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine1() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(3, 0));
        assertThat(Context.isInLine(currentSelected)).isTrue();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine2() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(3, 0));
        assertThat(Context.isInLine(currentSelected)).isFalse();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine3() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(3, -1));
        assertThat(Context.isInLine(currentSelected)).isFalse();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine4() {
        currentSelected.add(new Vector(1, 0));
        assertThat(Context.isInLine(currentSelected)).isTrue();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine5() {
        currentSelected.add(new Vector(-1, 0));
        currentSelected.add(new Vector(-2, 0));
        currentSelected.add(new Vector(-3, 0));
        assertThat(Context.isInLine(currentSelected)).isTrue();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine6() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(-2, 0));
        currentSelected.add(new Vector(3, 0));
        assertThat(Context.isInLine(currentSelected)).isFalse();
    }

    @DisplayName("Test: Is in line")
    @Test
    void inLine7() {
        currentSelected.add(new Vector(1, 1));
        currentSelected.add(new Vector(2, 1));
        currentSelected.add(new Vector(3, 1));
        assertThat(Context.isInLine(currentSelected)).isTrue();
    }

    @DisplayName("Test is in line in direction")
    @Test
    void inDirection1() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(3, 0));
        assertThat(Context.isInDirection(currentSelected, Direction.E)).isTrue();
    }

    @DisplayName("Test is in line in direction")
    @Test
    void inDirection2() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(3, 0));
        assertThat(Context.isInDirection(currentSelected, Direction.W)).isTrue();
    }

    @DisplayName("Test is in line in direction")
    @Test
    void inDirection3() {
        currentSelected.add(new Vector(-1, 0));
        assertThat(Context.isInDirection(currentSelected, Direction.NW)).isTrue();
    }

    @DisplayName("Test is in line in direction")
    @Test
    void inDirection4() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(4, 0));
        assertThat(Context.isInDirection(currentSelected, Direction.E)).isFalse();
    }

    @DisplayName("Test is in line in direction")
    @Test
    void inDirection5() {
        currentSelected.add(new Vector(1, 0));
        currentSelected.add(new Vector(2, 0));
        currentSelected.add(new Vector(3, 1));
        assertThat(Context.isInDirection(currentSelected, Direction.E)).isFalse();
    }

    @DisplayName("Test next space strength")
    @Test
    void nextSpaceStrength1() {
        board = new ObservableMap<>();
        board.put(new Vector(1, -2), Owner.PLAYER_WHITE);
        board.put(new Vector(1, -1), Owner.PLAYER_WHITE);
        board.put(new Vector(1, -3), Owner.PLAYER_WHITE);
        board.put(new Vector(1, 0), Owner.PLAYER_BLACK);
        board.put(new Vector(1, 1), Owner.EMPTY);
        currentSelected.add(new Vector(1, -1));
        currentSelected.add(new Vector(1, -2));
        int i = Context.nextSpaceStrength(board, new Vector(1, -1), Direction.SE, Owner.PLAYER_WHITE);
        boolean toCheck = i == 1;
        assertThat(toCheck).isTrue();
    }
}
