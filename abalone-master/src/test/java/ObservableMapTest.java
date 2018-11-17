import de.lmu.ifi.sep.abalone.components.EntryObserver;
import de.lmu.ifi.sep.abalone.components.ObservableMap;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard.Owner;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@DisplayName("ObservableMap Test")
class ObservableMapTest {
    private static final int size = 9;
    private final static Map<Vector, Observers> obsMap = new HashMap<>();
    private static int blacksObs = 0;
    private static int blacksMap = 0;
    private static int whiteObs = 0;
    private static int whitesMap = 0;
    private static Map<Vector, Owner> map;
    private static AbaloneBoard board;

    ObservableMapTest() {
    }

    private static void instantiateObs() {
        for (Vector v : map.keySet()) {
            Observers r = new Observers(board.getOwner(v));
            obsMap.put(v, r);
        }
    }

    @BeforeAll
    static void setUpBeforeClass() {
        board = new AbaloneBoard(size);
        map = board.getBoard();
        board.addObserver((key, value) -> obsMap.get(key).updateOwner(value));
        instantiateObs();
    }

    @AfterAll
    @DisplayName("Observer Operations")
    static void testObserverOperations() {
        ObservableMap<Vector, Owner> testMap = ((ObservableMap<Vector, Owner>) map);
        // getObservers
        List<EntryObserver<Vector, Owner>> observerList = testMap.getObservers();
        Assertions.assertThat(observerList).isNotNull();
        // has changed
        Assert.assertFalse(testMap.hasChanged());
        // removeObservers
        Assertions.assertThat(observerList.isEmpty()).isFalse();
        Assertions.assertThat(testMap.removeObservers()).isTrue();
        // The Observers are really all removed
        Assertions.assertThat(observerList.isEmpty()).isTrue();
        // hasChanged
        Assert.assertFalse(testMap.hasChanged());

    }

    @Override
    public String toString() {
        return "ObservableMapTest";
    }

    @BeforeEach
    @DisplayName("Check Observers are Synced with Values")
    void setUp() {
        //Check that ObservableMap and Observers are synced
        for (Map.Entry<Vector, Owner> e : map.entrySet()) {
            Assertions.assertThat(e.getValue()).isEqualByComparingTo(obsMap.get(e.getKey()).getOwner());
        }
    }

    @AfterEach
    @DisplayName("Check Observers are Synced with Values")
    void syncTest() {
        //Check that ObservableMap and Observers are synced
        for (Map.Entry<Vector, Owner> e : map.entrySet()) {
            Assertions.assertThat(e.getValue()).isEqualByComparingTo(obsMap.get(e.getKey()).getOwner());
        }
    }

    @Test
    @DisplayName("Testing Map Query Operations")
    void testMapQueryOperations() {
        // size
        Assertions.assertThat(map.size()).isEqualTo(61);
        // isEmpty
        Assert.assertFalse(map.isEmpty());
        // containsKey
        Assert.assertTrue(map.containsKey(new Vector(0, 0)));
        Assert.assertFalse(map.containsKey(new Vector(10, 0)));
        // containsValue
        Assert.assertTrue(map.containsValue(Owner.EMPTY));
        Assert.assertTrue(map.containsValue(Owner.PLAYER_WHITE));
        Assert.assertTrue(map.containsValue(Owner.PLAYER_BLACK));
        // get
        Assertions.assertThat(map.get(new Vector(0, 0))).isEqualTo(Owner.EMPTY);
        assert (map.get(new Vector(10, 0)) == null);
    }

    @Test
    @DisplayName("Testing Board Query Operations")
    void testBoardQueryOperations() {
        // board.getOwner
        Assertions.assertThat(board.getOwner(new Vector(0, 0))).isEqualByComparingTo(Owner.EMPTY);
        // board.hasPosition
        Assert.assertTrue(board.hasPosition(new Vector(2, -2)));
        // board.size
        Assertions.assertThat(board.getSize()).isEqualByComparingTo(9);
    }

    @Test
    @DisplayName("Modification Operations")
    void testModificationOperations() {
        //replace
        Vector v = new Vector(-1, 3);
        Assertions.assertThat((obsMap.get(v)).getOwner()).isEqualByComparingTo(Owner.PLAYER_WHITE);
        Assertions.assertThat(map.get(v)).isEqualByComparingTo(Owner.PLAYER_WHITE);
        Assertions.assertThat(map.replace(v, Owner.EMPTY)).isEqualByComparingTo(Owner.PLAYER_WHITE);
        Assertions.assertThat(map.get(v)).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat((obsMap.get(v)).getOwner()).isEqualByComparingTo(Owner.EMPTY);

        //replace(key, old, new)
        Assert.assertFalse(map.replace(v, Owner.PLAYER_BLACK, Owner.PLAYER_WHITE));
        Assertions.assertThat(map.get(v)).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(map.get(v)).isNotEqualByComparingTo(Owner.PLAYER_WHITE);
        Assertions.assertThat(map.get(v)).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(obsMap.get(v).getOwner()).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(map.replace(v, Owner.PLAYER_WHITE)).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(map.get(v)).isEqualByComparingTo(Owner.PLAYER_WHITE);
        Assertions.assertThat((obsMap.get(v)).getOwner()).isEqualByComparingTo(Owner.PLAYER_WHITE);

        //put
        Assertions.assertThat(map.get(new Vector(0, 1))).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(map.put(new Vector(0, 1), Owner.PLAYER_BLACK)).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(map.get(new Vector(0, 1))).isEqualByComparingTo(Owner.PLAYER_BLACK);
        Assertions.assertThat((obsMap.get(new Vector(0, 1))).getOwner()).isEqualByComparingTo(Owner.PLAYER_BLACK);

    }

    @Test
    @DisplayName("Bulk Operations")
    void testBulkOperations() {

        //forEach
        BiConsumer<Vector, Owner> biMap = (v, o) -> {
            if (map.get(v) == Owner.PLAYER_BLACK) {
                ++blacksMap;
            } else if ((obsMap.get(v)).getOwner() == Owner.PLAYER_WHITE) {
                ++whitesMap;
            }

        };
        BiConsumer<? super Vector, ? super Observers> biObs = (v, o) -> {
            if ((obsMap.get(v)).getOwner().equals(Owner.PLAYER_BLACK)) {
                ++blacksObs;
            } else if ((obsMap.get(v)).getOwner().equals(Owner.PLAYER_WHITE)) {
                ++whiteObs;
            }
        };

        map.forEach(biMap);

        Assertions.assertThat(blacksObs).isEqualTo(0); // obs pieces not counted so still 0
        Assertions.assertThat(blacksMap).isNotEqualTo(blacksObs);
        Assertions.assertThat(whitesMap).isNotEqualTo(whiteObs);

        obsMap.forEach(biObs::accept);
        Assertions.assertThat(blacksMap).isEqualTo(blacksObs);  //obs now counted

        //replaceAll
        BiFunction<Vector, Owner, Owner> func = (v, o) -> o == Owner.PLAYER_BLACK ? Owner.PLAYER_WHITE : o;
        //Replacing all Black Pieces with White
        map.replaceAll(func);
        // Update num pieces in map
        map.forEach(biMap);
        // Obs not updated yet
        Assertions.assertThat(whitesMap).isNotEqualTo(whiteObs);
        obsMap.forEach(biObs::accept);
        // Obs updated
        Assertions.assertThat(whitesMap).isEqualTo(whiteObs);

        // Construct ObservableMap from ObservableMap
        ObservableMap<Vector, Owner> om = new ObservableMap<>(map);
        Assertions.assertThat(om.get(new Vector(-4, 4))).isEqualByComparingTo(Owner.PLAYER_WHITE);

        // putAll
        ObservableMap<Vector, Owner> quick = new ObservableMap<>(10);
        quick.put(new Vector(-8, 8), Owner.EMPTY);
        quick.put(new Vector(-3, 2), Owner.PLAYER_BLACK);
        quick.putAll(map);
        Assertions.assertThat(quick.get(new Vector(-8, 8))).isEqualByComparingTo(Owner.EMPTY);
        Assertions.assertThat(quick.get(new Vector(-3, 2))).isEqualByComparingTo(Owner.EMPTY);

    }

    @Test
    @DisplayName("View Operations")
    void testViewOperations() {

/*        //keySet
        for (Vector v : map.keySet()){
            System.out.println(v);
        }*/

        // modification safe
        map.keySet().clear();
        Assert.assertFalse(map.isEmpty());

        // values
/*        for (Owner o : map.values()) {
            System.out.println(o);
        }*/
        // modification safe
        map.values().clear();
        Assert.assertFalse(map.isEmpty());
    }

}

class Observers {
    private Owner owner;

    Observers(Owner o) {
        owner = o;
    }

    Owner getOwner() {
        return owner;
    }

    void updateOwner(Owner o) {
        owner = o;
    }
}