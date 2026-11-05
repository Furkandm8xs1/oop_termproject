package foodflow.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CustomerTest {

    private final DeliveryAddress address = new DeliveryAddress("Kadıköy", "Moda Cad. 5");

    @Test
    void emailWithoutAtSignIsRejected_BR8() {
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "ali.example.com", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "@example.com", address));
    }

    @Test
    void duplicateEmailIsRejectedEvenWithDifferentCase_BR8() {
        CustomerRegistry registry = new CustomerRegistry();
        registry.register("Ali", "ali@example.com", address);

        assertThrows(IllegalStateException.class, () -> registry.register("Veli", "ALI@Example.com", address));
    }

    @Test
    void registeredCustomerCanBeFoundByEmail_BR8() {
        CustomerRegistry registry = new CustomerRegistry();
        registry.register("Ali", "ali@example.com", address);

        assertTrue(registry.findByEmail("Ali@Example.com").isPresent());
        assertTrue(registry.findByEmail("nobody@example.com").isEmpty());
    }

    @Test
    void customerNeedsADeliveryAddress_BR4() {
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "ali@example.com", null));
        assertThrows(IllegalArgumentException.class, () -> new DeliveryAddress("Kadıköy", " "));
    }

    @Test
    void nonPositivePriceIsRejected_BR1() {
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("X-1", "Free", "Main", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("X-2", "Negative", "Main", new BigDecimal("-5")));
        assertEquals(new BigDecimal("10.00"), new MenuItem("X-3", "Ok", "Main", BigDecimal.TEN).price());
    }

    @Test
    void filterIgnoresCaseAndTurkishCharacters() {
        RestaurantCatalog catalog = TestData.catalog();

        assertEquals(List.of("R01"), catalog.search("turkish", "kadikoy").stream().map(Restaurant::id).toList());
        assertEquals(List.of("R02"), catalog.search(null, "BESIKTAS").stream().map(Restaurant::id).toList());
        assertEquals(2, catalog.search(null, null).size());
        assertTrue(catalog.search("Japanese", null).isEmpty());
    }

    @Test
    void menuIsGroupedByCategory() {
        assertEquals(List.of("Main", "Drink"), List.copyOf(TestData.kebab().menuByCategory().keySet()));
    }
}

