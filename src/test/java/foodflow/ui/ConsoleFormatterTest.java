package foodflow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import foodflow.domain.Cart;
import foodflow.domain.Order;
import foodflow.domain.OrderNumberGenerator;
import foodflow.domain.Restaurant;
import foodflow.domain.TestData;

class ConsoleFormatterTest {

    private final ConsoleFormatter formatter = new ConsoleFormatter();

    @Test
    void moneyHasTwoDecimalsAndTL_BR5() {
        assertEquals("25.00 TL", formatter.money(new BigDecimal("25")));
        assertEquals("305.50 TL", formatter.money(new BigDecimal("305.5")));
    }

    @Test
    void emptyFilterResultIsReportedPolitely() {
        assertTrue(formatter.restaurantList(List.of()).contains("No restaurants match"));
    }

    @Test
    void menuShowsCategoriesAndPrices() {
        String text = formatter.menu(TestData.kebab());
        assertTrue(text.indexOf("Main") < text.indexOf("Lahmacun"));
        assertTrue(text.indexOf("Drink") < text.indexOf("Ayran"));
        assertTrue(text.contains("140.00 TL"));
    }

    @Test
    void cartShowsLineTotalsSubtotalFeeAndTotal() {
        Restaurant kebab = TestData.kebab();
        Cart cart = new Cart();
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);
        String text = formatter.cart(cart);
        assertTrue(text.contains("280.00 TL"));
        assertTrue(text.contains("25.00 TL"));
        assertTrue(text.contains("305.00 TL"));
    }

    @Test
    void orderConfirmationMatchesTheBriefsExample() {
        Restaurant kebab = TestData.kebab();
        Cart cart = new Cart();
        cart.addItem(kebab, kebab.findItem("R01-01").orElseThrow(), 2);
        Order order = Order.place(new OrderNumberGenerator(), TestData.ali(), cart);
        assertEquals("Order #1001 placed: 305.00 TL (incl. delivery 25.00 TL), cash on delivery.",
                formatter.orderPlaced(order));
    }
}
