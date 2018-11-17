import de.lmu.ifi.sep.abalone.components.Vector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Vector Class")
class VectorTests {

    @Test
    @DisplayName("this is a example test")
    void example() {
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Vector Subtraction")
    void vectorSubtraction() {
        Vector v1 = new Vector(1, 2);
        Vector v2 = new Vector(2, 2);
        Vector v3 = new Vector(-5, -3);

        Assertions.assertThat(v1.subtractFrom(v2)).isEqualTo(new Vector(1, 0));
        Assertions.assertThat(v2.subtractFrom(v1)).isEqualTo(new Vector(-1, 0));
        Assertions.assertThat(v2.subtractFrom(v3)).isEqualTo(new Vector(-7, -5));
    }

    @Test
    @DisplayName("Vector Addition")
    void vectorAddition() {
        Vector v1 = new Vector(1, 2);
        Vector v2 = new Vector(2, 2);
        Vector v3 = new Vector(10, 3);
        Vector v4 = new Vector(-5, -3);

        Assertions.assertThat(v1.addVector(v2)).isEqualTo(new Vector(3, 4));
        Assertions.assertThat(v3.addVector(v1)).isEqualTo(new Vector(11, 5));
        Assertions.assertThat(v4.addVector(v2)).isEqualTo(new Vector(-3, -1));
    }

    @Test
    @DisplayName("getter methods")
    void getterTest() {
        Vector v1 = new Vector(1, 2);
        Assertions.assertThat(v1.getX()).isEqualTo(1);
        Assertions.assertThat(v1.getY()).isEqualTo(2);
        Assertions.assertThat(v1.getZ()).isEqualTo(-3);
    }

    @Test
    @DisplayName("equals/hash methods")
    void equalsTest() {
        Vector v1 = new Vector(1, 2);
        Vector v2 = new Vector(1, 3);
        Vector v3 = new Vector(1, 2);
        Assertions.assertThat(v1.equals(v2)).isFalse();
        Assertions.assertThat(v1.equals(v3)).isTrue();
        Assertions.assertThat(v1.hashCode()).isNotEqualTo(v2.hashCode());
        Assertions.assertThat(v1.hashCode()).isEqualTo(v3.hashCode());
    }

    @Test
    @DisplayName("go methods")
    void goTest() {
        Vector v1 = new Vector(1, 2);
        Assertions.assertThat(v1.go(Vector.Direction.E)).isEqualTo(new Vector(2, 2));
        Assertions.assertThat(v1.go(Vector.Direction.NW)).isEqualTo(new Vector(1, 1));
        Assertions.assertThat(v1.go(Vector.Direction.NE)).isEqualTo(new Vector(2, 1));
        Assertions.assertThat(v1.go(Vector.Direction.W)).isEqualTo(new Vector(0, 2));
        Assertions.assertThat(v1.go(Vector.Direction.SE)).isEqualTo(new Vector(1, 3));
        Assertions.assertThat(v1.go(Vector.Direction.SW)).isEqualTo(new Vector(0, 3));
    }

    @Test
    @DisplayName("go null safety")
    void goNullSafety() {
        Vector v1 = new Vector(1, 2);
        v1.go(Vector.Direction.E);
        v1.go(Vector.Direction.NW);
        v1.go(Vector.Direction.NE);
        v1.go(Vector.Direction.W);
        v1.go(Vector.Direction.SE);
        v1.go(Vector.Direction.SW);

    }

    @Test
    @DisplayName("invertGo methods")
    void invertGoTest() {
        Vector v1 = new Vector(1, 2);
        Assertions.assertThat(v1.invertGo(Vector.Direction.E)).isEqualTo(new Vector(0, 2));
        Assertions.assertThat(v1.invertGo(Vector.Direction.NW)).isEqualTo(new Vector(1, 3));
        Assertions.assertThat(v1.invertGo(Vector.Direction.NE)).isEqualTo(new Vector(0, 3));
        Assertions.assertThat(v1.invertGo(Vector.Direction.W)).isEqualTo(new Vector(2, 2));
        Assertions.assertThat(v1.invertGo(Vector.Direction.SE)).isEqualTo(new Vector(1, 1));
        Assertions.assertThat(v1.invertGo(Vector.Direction.SW)).isEqualTo(new Vector(2, 1));
    }

    @Test
    @DisplayName("neighbors methods")
    void neighborsTest() {
        Vector v1 = new Vector(1, 2);
        List<Vector> neighbor = new ArrayList<>();
        for (Vector.Direction d : Vector.Direction.values()) {
            neighbor.add(v1.go(d));
        }
        Assertions.assertThat(v1.getNeighbors()).isEqualTo(neighbor);
    }


    @Nested
    @DisplayName("Utility Method tests")
    class UtilityMethodTests {

        Vector start;

        @BeforeEach
        void setStart() {
            start = new Vector(0, 0);
        }

        @Test
        @DisplayName("getDirectionOfMove - East")
        void getDirOfMoveTestE() {
            Vector end = new Vector(1, 0);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.E);
        }

        @Test
        @DisplayName("getDirectionOfMove - West")
        void getDirOfMoveTestWest() {
            Vector end = new Vector(-1, 0);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.W);
        }

        @Test
        @DisplayName("getDirectionOfMove - NorthWest")
        void getDirOfMoveTestNorth() {
            Vector end = new Vector(0, -1);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.NW);
        }

        @Test
        @DisplayName("getDirectionOfMove - NorthEast")
        void getDirOfMoveTestNE() {
            Vector end = new Vector(1, -1);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.NE);
        }

        @Test
        @DisplayName("getDirectionOfMove - SouthWest")
        void getDirOfMoveTestSW() {
            Vector end = new Vector(-1, 1);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.SW);
        }

        @Test
        @DisplayName("getDirectionOfMove - SouthEast")
        void getDirOfMoveTestSE() {
            Vector end = new Vector(0, 1);
            assertThat(Vector.getDirectionOfMove(start, end))
                    .isEqualByComparingTo(Vector.Direction.SE);
        }

        @Test
        @DisplayName("getDirectionOfMove - illegal moves")
        void getDirOfMoveTestIllegal() {
            Vector end = new Vector(0, 0);
            Vector end2 = new Vector(3, 3);

            assertThat(Vector.getDirectionOfMove(start, end)).isNull();
            assertThat(Vector.getDirectionOfMove(start, end2)).isNull();
        }

    }

}
