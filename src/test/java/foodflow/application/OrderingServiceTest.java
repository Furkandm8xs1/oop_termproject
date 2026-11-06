package foodflow.application;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import foodflow.domain.CustomerRegistry;
import foodflow.domain.Order;
import foodflow.domain.OrderNumberGenerator;
import foodflow.domain.TestData;

class OrderingServiceTest {

    private OrderingService service;

    @BeforeEach
    void setUp() {
        service = new OrderingService(TestData.catalog(), new CustomerRegistry(), new OrderNumberGenerator());
    }

    @Test
    void placingAnOrderEmptiesTheCart_BR7() {
        service.register("Ali", "ali@example.com", "Kadıköy", "Moda Cad. 5");
        service.addItem("R01-01", 2);

        Order order = service.placeOrder();

        assertTrue(service.viewCart().isEmpty());
        assertEquals(new BigDecimal("305.00"), order.total());
    }

    @Test
    void orderWithoutIdentifyingFirstIsRefusedAndCartIsKept_BR4() {
        service.addItem("R01-01", 1);

        assertThrows(IllegalStateException.class, () -> service.placeOrder());

        assertEquals(1, service.viewCart().lines().size());
    }

    @Test
    void myOrdersListsOnlyTheCurrentCustomersOrders() {
        service.register("Ali", "ali@example.com", "Kadıköy", "Moda Cad. 5");
        service.addItem("R01-01", 1);
        service.placeOrder();
        service.register("Veli", "veli@example.com", "Beşiktaş", "Barbaros Blv. 1");

        assertTrue(service.myOrders().isEmpty());

        service.login("ali@example.com");
        assertEquals(1, service.myOrders().size());
    }

    @Test
    void unknownItemAndUnknownRestaurantAreReportedAsErrors() {
        assertThrows(IllegalArgumentException.class, () -> service.addItem("NOPE-1", 1));
        assertThrows(IllegalArgumentException.class, () -> service.viewMenu("R99"));
        assertThrows(IllegalArgumentException.class, () -> service.login("ghost@example.com"));
    }

    @Test
    void itemIdsAreFoundRegardlessOfCase() {
        service.addItem("r01-01", 1);

        assertEquals("R01-01", service.viewCart().lines().get(0).itemId());
    }

    @Test
    void cartSummaryIsAReadOnlySnapshot() {
        service.addItem("R01-01", 2);
        CartSummary summary = service.viewCart();

        assertThrows(UnsupportedOperationException.class, () -> summary.lines().clear());

        service.addItem("R01-05", 1); // later changes do not alter the snapshot taken earlier
        assertEquals(1, summary.lines().size());
        assertEquals(2, service.viewCart().lines().size());
        assertEquals(new BigDecimal("320.00"), service.viewCart().subtotal());
        assertEquals("Kebapçı Mehmet", summary.restaurantName());
    }
}