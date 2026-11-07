package foodflow.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.item;

/** Constructor rules of Restaurant and RestaurantCatalog: bad data is refused. */
class RestaurantAndCatalogTest {

    private static Restaurant restaurant(String id, String fee, List<MenuItem> menu) {
        return new Restaurant(id, "Name " + id, "Turkish", "Kadıköy",
                fee == null ? null : new BigDecimal(fee), new BigDecimal("100"), menu);
    }

    @Test
    void negativeOrMissingDeliveryFeeIsRejectedButZeroIsAllowed_BR5() {
        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", "-1", List.of()));
        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", null, List.of()));
        assertEquals(new BigDecimal("0.00"), restaurant("R1", "0", List.of()).deliveryFee());
    }

    @Test
    void duplicateItemIdInOneMenuIsRejectedEvenWithDifferentCase_BR3() {
        List<MenuItem> menu = List.of(item("R1-1", "A", "Main", "1"), item("r1-1", "B", "Main", "2"));

        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", "5", menu));
    }

    @Test
    void duplicateRestaurantIdsAreRejected() {
        Restaurant a = restaurant("R1", "5", List.of(item("R1-1", "A", "Main", "1")));
        Restaurant b = restaurant("r1", "5", List.of(item("R9-1", "B", "Main", "1")));

        assertThrows(IllegalArgumentException.class, () -> new RestaurantCatalog(List.of(a, b)));
    }

    @Test
    void sameItemIdInTwoRestaurantsIsRejected() {
        Restaurant a = restaurant("R1", "5", List.of(item("X-1", "A", "Main", "1")));
        Restaurant b = restaurant("R2", "5", List.of(item("X-1", "B", "Main", "1")));

        assertThrows(IllegalArgumentException.class, () -> new RestaurantCatalog(List.of(a, b)));
    }
}
