package foodflow.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.item;

/** BR-1 (price and quantity) and BR-5 (line total) at the level of the smallest domain objects. */
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
    void cartLineNeedsAnItem_BR1() {
        assertThrows(IllegalArgumentException.class, () -> new CartLine(null, 1));
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
    void acceptedQuantityChangeUpdatesTheLineTotal_BR1_BR5() {
        CartLine line = new CartLine(lahmacun, 1);

        line.changeQuantity(3);

        assertEquals(3, line.quantity());
        assertEquals(new BigDecimal("420.00"), line.lineTotal());
    }

    @Test
    void zeroAndNegativePriceAreRejectedButSmallPositivePriceIsAccepted_BR1() {
        assertThrows(IllegalArgumentException.class, () -> item("X-1", "Free", "Main", "0"));
        assertThrows(IllegalArgumentException.class, () -> item("X-2", "Minus", "Main", "-0.01"));
        assertEquals(new BigDecimal("0.01"), item("X-3", "Cheap", "Main", "0.01").price());
    }

    @Test
    void priceThatRoundsToZeroIsRejected_BR1() {
        // 0.004 is scaled to 0.00, so the item would be free: not allowed.
        assertThrows(IllegalArgumentException.class, () -> item("X-4", "Tiny", "Main", "0.004"));
    }

    @Test
    void missingPriceIsRejected_BR1() {
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("X-5", "NoPrice", "Main", null));
    }

    @Test
    void priceIsAlwaysKeptWithTwoDecimals_BR5() {
        assertEquals(new BigDecimal("10.50"), item("X-6", "Tea", "Drink", "10.5").price());
        assertEquals(new BigDecimal("10.00"), item("X-7", "Water", "Drink", "10").price());
    }

    @Test
    void menuItemNeedsIdNameAndCategory() {
        assertThrows(IllegalArgumentException.class, () -> item(" ", "A", "Main", "1"));
        assertThrows(IllegalArgumentException.class, () -> item("X-8", "", "Main", "1"));
        assertThrows(IllegalArgumentException.class, () -> item("X-9", "A", null, "1"));
    }

    @Test
    void menuItemsAreEqualWhenTheyHaveTheSameId() {
        assertEquals(item("R01-01", "Lahmacun", "Main", "140"), item("R01-01", "Other name", "Main", "1"));
        assertNotEquals(item("R01-01", "Lahmacun", "Main", "140"), item("R01-02", "Lahmacun", "Main", "140"));
    }
}
