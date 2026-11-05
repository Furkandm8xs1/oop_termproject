package foodflow.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.ali;
import static foodflow.domain.TestData.kebab;

class OrderTest {

    private Restaurant kebab;
    private Cart cart;
    private OrderNumberGenerator numbers;

    @BeforeEach
    void setUp() {
        kebab = kebab();
        cart = new Cart();
        numbers = new OrderNumberGenerator();
    }

    @Test
    void orderTotalIsSubtotalPlusDeliveryFee_BR5() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);

        Order order = Order.place(numbers, ali(), cart);

        assertEquals(new BigDecimal("280.00"), order.subtotal());
        assertEquals(new BigDecimal("305.00"), order.total());
    }

    @Test
    void twoOrdersGetDifferentIncreasingNumbersAndStatusPlaced_BR6() {
        MenuItem lahmacun = kebab.findItem("R01-01").orElseThrow();
        cart.addItem(kebab, lahmacun, 1);
        Order first = Order.place(numbers, ali(), cart);
        cart.addItem(kebab, lahmacun, 1); // placing emptied the cart (BR-7), so fill it again
        Order second = Order.place(numbers, ali(), cart);

        assertNotEquals(first.number(), second.number());
        assertTrue(second.number() > first.number());
        assertEquals(1001, first.number());
        assertEquals(OrderStatus.PLACED, first.status());
    }

    @Test
    void placingAnOrderEmptiesTheCart_BR7() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);

        Order.place(numbers, ali(), cart);

        assertTrue(cart.isEmpty());
        assertTrue(cart.restaurant().isEmpty());
    }

    @Test
    void failedOrderLeavesTheCartUntouched_BR4() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);

        assertThrows(IllegalStateException.class, () -> Order.place(numbers, null, cart));

        assertEquals(1, cart.lines().size());
    }

    @Test
    void orderLinesCarryNameUnitPriceQuantityAndLineTotal_BR7() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);

        OrderLine line = Order.place(numbers, ali(), cart).lines().get(0);

        assertEquals("R01-01", line.itemId());
        assertEquals("Lahmacun", line.itemName());
        assertEquals(new BigDecimal("140.00"), line.unitPrice());
        assertEquals(2, line.quantity());
        assertEquals(new BigDecimal("280.00"), line.lineTotal());
    }

    @Test
    void emptyCartCannotBeOrderedAndConsumesNoNumber_BR4() {
        assertThrows(IllegalStateException.class, () -> Order.place(numbers, ali(), cart));

        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 1);
        assertEquals(1001, Order.place(numbers, ali(), cart).number());
    }

    @Test
    void orderWithoutCustomerIsRejected_BR4() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 1);

        assertThrows(IllegalStateException.class, () -> Order.place(numbers, null, cart));
    }

    @Test
    void orderKeepsItsPricesWhenCartChangesLater_BR7() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);
        Order order = Order.place(numbers, ali(), cart);

        cart.clear();

        assertEquals(1, order.lines().size());
        assertEquals("Lahmacun", order.lines().get(0).itemName());
        assertEquals(new BigDecimal("305.00"), order.total());
    }

    @Test
    void orderTotalDoesNotDependOnLaterMenuPriceChanges_BR7() {
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 1);
        Order order = Order.place(numbers, ali(), cart);

        // The menu changes later (Iteration 2): a new Restaurant object with a higher price.
        Restaurant repriced = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", "Kadıköy",
                new BigDecimal("40.0"), BigDecimal.ZERO,
                java.util.List.of(TestData.item("R01-01", "Lahmacun", "Main", "999.0")));

        assertEquals(new BigDecimal("999.00"), repriced.findItem("R01-01").orElseThrow().price());
        assertEquals(new BigDecimal("165.00"), order.total()); // 140 + 25, unchanged
    }
}

