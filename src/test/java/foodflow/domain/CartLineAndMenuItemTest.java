package foodflow.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.item;

/** BR-1 (price and quantity) for the smallest domain objects. */
class CartLineAndMenuItemTest {

    private final MenuItem lahmacun = item("R01-01", "Lahmacun", "Main", "140.0");

    @Test
    void quantityBoundariesAreAcceptedAndOutsideValuesRejected_BR1() {
        assertEquals(1, new CartLine(lahmacun, 1).quantity());
        assertEquals(20, new CartLine(lahmacun, 20).quantity());

        assertThrows(IllegalArgumentException.class, () -> new CartLine(lahmacun, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartLine(lahmacun, 21));
        assertThrows(IllegalArgumentException.class, () -> new CartLine(lahmacun, -3));
    }

    @Test
    void rejectedQuantityChangeLeavesTheLineUntouched_BR1() {
        CartLine line = new CartLine(lahmacun, 5);

        assertThrows(IllegalArgumentException.class, () -> line.changeQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> line.changeQuantity(21));

        assertEquals(5, line.quantity());
        assertEquals(new BigDecimal("700.00"), line.lineTotal());
    }

    @Test
    void zeroNegativeAndRoundedToZeroPricesAreRejected_BR1() {
        assertThrows(IllegalArgumentException.class, () -> item("X-1", "Free", "Main", "0"));
        assertThrows(IllegalArgumentException.class, () -> item("X-2", "Minus", "Main", "-0.01"));
        // 0.004 is scaled to 0.00, so the item would be free: not allowed
        assertThrows(IllegalArgumentException.class, () -> item("X-3", "Tiny", "Main", "0.004"));
        assertEquals(new BigDecimal("0.01"), item("X-4", "Cheap", "Main", "0.01").price());
    }
}
