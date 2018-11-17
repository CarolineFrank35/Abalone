package views;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.views.Layout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Layout")
class LayoutTest {
    private final int size = 522;
    private final int origin = 309;
    private Layout layout;

    @BeforeEach
    void setup() {
        layout = new Layout(size, origin);
    }

    @Test
    @DisplayName("Test transformation (hex <-> pixel)")
    void transformTest() {
        Vector v = new Vector(2, -2);
        Vector h = layout.hexToPixel(v);

        Assertions.assertEquals(v, layout.pixelToHex(h));
    }

    @Test
    @DisplayName("getSizeTest()")
    void getSizeTest() {
        Assertions.assertEquals(size, layout.getSize());
    }

    @Test
    @DisplayName("getOrigin")
    void getOriginTest() {
        Assertions.assertEquals(origin, layout.getOrigin());
    }
}
