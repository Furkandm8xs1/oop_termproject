package foodflow.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.item;

/** Constructor rules of Restaurant and RestaurantCatalog: bad data is refused, nothing half-built is kept. */
class RestaurantAndCatalogTest {

    private static Restaurant restaurant(String id, String fee, List<MenuItem> menu) {
        return new Restaurant(id, "Name " + id, "Turkish", "Kadıköy",
                fee == null ? null : new BigDecimal(fee), new BigDecimal("100"), menu);
    }

    // ---- Restaurant ----

    @Test
    void negativeDeliveryFeeIsRejectedButZeroIsAllowed_BR5() {
        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", "-1", List.of()));
        assertEquals(new BigDecimal("0.00"), restaurant("R1", "0", List.of()).deliveryFee());
    }

    @Test
    void missingDeliveryFeeIsRejected_BR5() {
        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", null, List.of()));
    }

    @Test
    void deliveryFeeIsKeptWithTwoDecimals_BR5() {
        assertEquals(new BigDecimal("25.00"), restaurant("R1", "25", List.of()).deliveryFee());
    }

    @Test
    void missingMinimumBasketDefaultsToZero() {
        Restaurant r = new Restaurant("R1", "Name", "Turkish", "Kadıköy", BigDecimal.TEN, null, List.of());

        assertEquals(new BigDecimal("0.00"), r.minimumBasket());
    }

    @Test
    void restaurantNeedsIdNameCuisineDistrictAndMenu() {
        assertThrows(IllegalArgumentException.class, () -> new Restaurant(" ", "N", "C", "D", BigDecimal.ONE, BigDecimal.ZERO, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Restaurant("R1", "", "C", "D", BigDecimal.ONE, BigDecimal.ZERO, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Restaurant("R1", "N", null, "D", BigDecimal.ONE, BigDecimal.ZERO, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Restaurant("R1", "N", "C", " ", BigDecimal.ONE, BigDecimal.ZERO, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Restaurant("R1", "N", "C", "D", BigDecimal.ONE, BigDecimal.ZERO, null));
    }

    @Test
    void duplicateItemIdInOneMenuIsRejectedEvenWithDifferentCase_BR3() {
        List<MenuItem> menu = List.of(item("R1-1", "A", "Main", "1"), item("r1-1", "B", "Main", "2"));

        assertThrows(IllegalArgumentException.class, () -> restaurant("R1", "5", menu));
    }

    @Test
    void findItemIgnoresCaseAndReturnsEmptyForUnknownIds() {
        Restaurant r = TestData.kebab();

        assertEquals("Lahmacun", r.findItem("r01-01").orElseThrow().name());
        assertTrue(r.findItem("R99-99").isEmpty());
        assertTrue(r.findItem(null).isEmpty());
    }

    @Test
    void hasItemIsTrueOnlyForItemsOfThisMenu() {
        MenuItem margherita = TestData.pizza().findItem("R02-01").orElseThrow();

        assertTrue(TestData.kebab().hasItem(TestData.kebab().findItem("R01-01").orElseThrow()));
        assertFalse(TestData.kebab().hasItem(margherita));
    }

    @Test
    void menuListCannotBeChangedFromOutside() {
        Restaurant r = TestData.kebab();

        assertThrows(UnsupportedOperationException.class, () -> r.menu().clear());
    }

    @Test
    void matchesUsesAndBetweenCuisineAndDistrictAndBlankMeansAny() {
        Restaurant r = TestData.kebab(); // Turkish, Kadıköy

        assertTrue(r.matches("turkish", "KADIKOY"));
        assertTrue(r.matches("", " "));
        assertTrue(r.matches("Turkish", null));
        assertFalse(r.matches("Turkish", "Beşiktaş"));
        assertFalse(r.matches("Italian", "Kadıköy"));
    }

    // ---- RestaurantCatalog ----

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

    @Test
    void catalogNeedsAListOfRestaurants() {
        assertThrows(IllegalArgumentException.class, () -> new RestaurantCatalog(null));
        assertTrue(new RestaurantCatalog(List.of()).all().isEmpty());
    }

    @Test
    void findByIdAndFindByItemIdWorkAndReturnEmptyForUnknownValues() {
        RestaurantCatalog catalog = TestData.catalog();

        assertEquals("R02", catalog.findById("r02").orElseThrow().id());
        assertEquals("R02", catalog.findByItemId("R02-01").orElseThrow().id());
        assertTrue(catalog.findById("R99").isEmpty());
        assertTrue(catalog.findByItemId("R99-01").isEmpty());
    }

    @Test
    void searchWithNoMatchGivesAnEmptyListNotAnError() {
        assertTrue(TestData.catalog().search("Martian", "Kadıköy").isEmpty());
    }

    @Test
    void catalogListCannotBeChangedFromOutside() {
        RestaurantCatalog catalog = TestData.catalog();

        assertThrows(UnsupportedOperationException.class, () -> catalog.all().clear());
    }
}
