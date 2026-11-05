package foodflow.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static foodflow.domain.TestData.kebab;
import static foodflow.domain.TestData.pizza;

class CartTest {

    private Restaurant kebab;
    private Restaurant pizza;
    private MenuItem lahmacun;
    private MenuItem ayran;
    private Cart cart;

    @BeforeEach
    void setUp() {
        kebab = kebab();
        pizza = pizza();
        lahmacun = kebab.findItem("R01-01").orElseThrow();
        ayran = kebab.findItem("R01-05").orElseThrow();
        cart = new Cart();
    }

    @Test
    void addingTheSameItemTwiceKeepsOneLineWithSummedQuantity_BR3() {
        cart.addItem(kebab, lahmacun, 2);
        cart.addItem(kebab, lahmacun, 3);

        assertEquals(1, cart.lines().size());
        assertEquals(5, cart.lines().get(0).quantity());
    }

    @Test
    void itemFromAnotherRestaurantIsRefusedAndCartIsUnchanged_BR2() {
        cart.addItem(kebab, lahmacun, 2);
        MenuItem margherita = pizza.findItem("R02-01").orElseThrow();

        assertThrows(IllegalStateException.class, () -> cart.addItem(pizza, margherita, 1));

        assertEquals(1, cart.lines().size());
        assertEquals(kebab, cart.restaurant().orElseThrow());
        assertEquals(new BigDecimal("280.00"), cart.subtotal());
    }

    @Test
    void afterEmptyingTheCartAnotherRestaurantIsAllowed_BR2() {
        cart.addItem(kebab, lahmacun, 1);
        cart.clear();

        cart.addItem(pizza, pizza.findItem("R02-01").orElseThrow(), 1);

        assertEquals(pizza, cart.restaurant().orElseThrow());
    }

    @Test
    void quantityZeroAndTwentyOneAreRejected_BR1() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(kebab, lahmacun, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(kebab, lahmacun, 21));
        assertTrue(cart.isEmpty());
    }

    @Test
    void quantitiesOneAndTwentyAreAccepted_BR1() {
        cart.addItem(kebab, lahmacun, 1);
        cart.addItem(kebab, ayran, 20);

        assertEquals(2, cart.lines().size());
    }

    @Test
    void summedQuantityAboveTwentyIsRejectedAndLineKeepsOldQuantity_BR1_BR3() {
        cart.addItem(kebab, lahmacun, 15);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(kebab, lahmacun, 6));

        assertEquals(15, cart.lines().get(0).quantity());
    }

    @Test
    void changingQuantityToInvalidValueIsRejectedAndKeepsOldValue_BR1() {
        cart.addItem(kebab, lahmacun, 2);

        assertThrows(IllegalArgumentException.class, () -> cart.changeQuantity("R01-01", 0));

        assertEquals(2, cart.lines().get(0).quantity());
    }

    @Test
    void removingTheLastItemFreesTheCartForAnyRestaurant() {
        cart.addItem(kebab, lahmacun, 1);
        cart.removeItem("R01-01");

        assertTrue(cart.isEmpty());
        assertTrue(cart.restaurant().isEmpty());
    }

    @Test
    void totalIsSubtotalPlusDeliveryFee_BR5() {
        cart.addItem(kebab, lahmacun, 2); // 280
        cart.addItem(kebab, ayran, 1);    // 40

        assertEquals(new BigDecimal("320.00"), cart.subtotal());
        assertEquals(new BigDecimal("25.00"), cart.deliveryFee());
        assertEquals(new BigDecimal("345.00"), cart.total());
    }
}

