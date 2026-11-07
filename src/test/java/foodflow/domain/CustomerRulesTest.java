package foodflow.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/** More BR-8 (e-mail) cases and the order number rule BR-6. */
class CustomerRulesTest {

    private final DeliveryAddress address = new DeliveryAddress("Kadıköy", "Moda Cad. 5");

    @Test
    void malformedEmailsAreRejected_BR8() {
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "a@@b.com", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "a@b@c.com", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "ali@", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", "al i@example.com", address));
        assertThrows(IllegalArgumentException.class, () -> new Customer("Ali", null, address));
    }

    @Test
    void failedRegistrationDoesNotStoreAnything_BR8() {
        CustomerRegistry registry = new CustomerRegistry();

        assertThrows(IllegalArgumentException.class, () -> registry.register("Ali", "not-an-email", address));

        assertTrue(registry.findByEmail("not-an-email").isEmpty());
        // the same registry still accepts a correct registration afterwards
        assertEquals("ali@example.com", registry.register("Ali", "ali@example.com", address).email());
    }

    @Test
    void duplicateRegistrationKeepsTheFirstCustomer_BR8() {
        CustomerRegistry registry = new CustomerRegistry();
        registry.register("Ali", "ali@example.com", address);

        assertThrows(IllegalStateException.class, () -> registry.register("Veli", "ali@example.com", address));

        assertEquals("Ali", registry.findByEmail("ali@example.com").orElseThrow().name());
    }

    @Test
    void orderNumbersStartAt1001AndOnlyGoUp_BR6() {
        OrderNumberGenerator numbers = new OrderNumberGenerator();

        assertEquals(1001, numbers.next());
        assertEquals(1002, numbers.next());
        assertEquals(1003, numbers.next());
    }
}
