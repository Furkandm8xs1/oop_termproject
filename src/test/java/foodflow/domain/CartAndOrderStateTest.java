package foodflow.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.ali;
import static foodflow.domain.TestData.kebab;
import static foodflow.domain.TestData.pizza;

/** Error cases and "nothing changed" checks for the cart (BR-1, BR-2, BR-3) and the order (BR-4, BR-6, BR-7). */
class CartAndOrderStateTest {

    private Restaurant kebab;
    private Restaurant pizza;
    private MenuItem lahmacun;
    private MenuItem ayran;
    private Cart cart;
    private OrderNumberGenerator numbers;

    @BeforeEach
    void setUp() {
        kebab = kebab();
        pizza = pizza();
        lahmacun = kebab.findItem("R01-01").orElseThrow();
        ayran = kebab.findItem("R01-05").orElseThrow();
        cart = new Cart();
        numbers = new OrderNumberGenerator();
    }

    @Test
    void itemThatIsNotOnTheRestaurantsMenuIsRefusedAndCartStaysEmpty_BR2() {
        MenuItem margherita = pizza.findItem("R02-01").orElseThrow();

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(kebab, margherita, 1));

        assertTrue(cart.isEmpty());
        assertTrue(cart.restaurant().isEmpty());
    }

    @Test
    void firstItemWithInvalidQuantityDoesNotTieTheCartToARestaurant_BR1_BR2() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(kebab, lahmacun, 0));

        assertTrue(cart.restaurant().isEmpty());
        // so another restaurant is still possible afterwards
        cart.addItem(pizza, pizza.findItem("R02-01").orElseThrow(), 1);
        assertEquals(pizza, cart.restaurant().orElseThrow());
    }

    @Test
    void refusedSecondRestaurantKeepsLinesQuantitiesAndTotalsExactly_BR2() {
        cart.addItem(kebab, lahmacun, 2);
        cart.addItem(kebab, ayran, 1);
        BigDecimal totalBefore = cart.total();

        assertThrows(IllegalStateException.class,
                () -> cart.addItem(pizza, pizza.findItem("R02-01").orElseThrow(), 5));

        assertEquals(2, cart.lines().size());
        assertEquals(2, cart.lines().get(0).quantity());
        assertEquals(1, cart.lines().get(1).quantity());
        assertEquals(totalBefore, cart.total());
    }

    @Test
    void addingSameItemUpToExactlyTwentyIsAllowed_BR1_BR3() {
        cart.addItem(kebab, lahmacun, 15);
        cart.addItem(kebab, lahmacun, 5);

        assertEquals(1, cart.lines().size());
        assertEquals(20, cart.lines().get(0).quantity());
    }

    @Test
    void changingOrRemovingAnItemThatIsNotInTheCartIsRefusedAndNothingChanges() {
        cart.addItem(kebab, lahmacun, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.changeQuantity("R01-05", 1));
        assertThrows(IllegalArgumentException.class, () -> cart.removeItem("R01-05"));

        assertEquals(1, cart.lines().size());
        assertEquals(new BigDecimal("280.00"), cart.subtotal());
    }

    @Test
    void nullCartIsRefusedAndNoOrderNumberIsUsed_BR4() {
        assertThrows(IllegalStateException.class, () -> Order.place(numbers, ali(), null));

        cart.addItem(kebab, lahmacun, 1);
        assertEquals(1001, Order.place(numbers, ali(), cart).number());
    }

    @Test
    void fillingTheCartAgainAfterOrderingDoesNotChangeThePlacedOrder_BR7() {
        cart.addItem(kebab, lahmacun, 1);
        Order first = Order.place(numbers, ali(), cart);

        cart.addItem(kebab, ayran, 3);

        assertEquals(1, first.lines().size());
        assertEquals(new BigDecimal("165.00"), first.total());
    }

    @Test
    void secondOrderContainsOnlyItsOwnLines_BR6_BR7() {
        cart.addItem(kebab, lahmacun, 1);
        Order first = Order.place(numbers, ali(), cart);
        cart.addItem(kebab, ayran, 2);

        Order second = Order.place(numbers, ali(), cart);

        assertEquals(1001, first.number());
        assertEquals(1002, second.number());
        assertEquals(1, second.lines().size());
        assertEquals("Ayran", second.lines().get(0).itemName());
        assertEquals(new BigDecimal("105.00"), second.total());
    }
}
