package foodflow.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * A placed cash-on-delivery order. Immutable snapshot of the cart at the moment of ordering.
 * BR-4: needs a customer with delivery address and a non-empty cart.
 * BR-6 (status part): the initial status is PLACED.
 * BR-7: the order copies names and prices (see {@link OrderLine}) and placing it empties the cart.
 * The amounts are taken over from the cart, which is the only place that calculates them (BR-5).
 */
public final class Order {

    private final int number;
    private final OrderStatus status;
    private final Customer customer;
    private final DeliveryAddress deliveryAddress;
    private final String restaurantName;
    private final List<OrderLine> lines;
    private final BigDecimal subtotal;
    private final BigDecimal deliveryFee;
    private final BigDecimal total;

    private Order(int number, Customer customer, String restaurantName, List<OrderLine> lines,
                  BigDecimal subtotal, BigDecimal deliveryFee, BigDecimal total) {
        this.number = number;
        this.status = OrderStatus.PLACED;
        this.customer = customer;
        this.deliveryAddress = customer.address();
        this.restaurantName = restaurantName;
        this.lines = lines;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = total;
    }

    /**
     * Places an order for the cart's content. All checks come first: if one fails, no number is
     * used and the cart is unchanged. On success the cart is emptied (BR-7).
     */
    public static Order place(OrderNumberGenerator numbers, Customer customer, Cart cart) {
        if (customer == null) {
            throw new IllegalStateException("Identify yourself first (register or login) before ordering.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Add items before ordering.");
        }
        Restaurant restaurant = cart.restaurant().orElseThrow();
        List<OrderLine> snapshot = cart.lines().stream().map(OrderLine::snapshotOf).toList();
        Order order = new Order(numbers.next(), customer, restaurant.name(), snapshot,
                cart.subtotal(), cart.deliveryFee(), cart.total());
        cart.clear();
        return order;
    }

    public int number() {
        return number;
    }

    public OrderStatus status() {
        return status;
    }

    public Customer customer() {
        return customer;
    }

    public DeliveryAddress deliveryAddress() {
        return deliveryAddress;
    }

    public String restaurantName() {
        return restaurantName;
    }

    public List<OrderLine> lines() {
        return lines;
    }

    public BigDecimal subtotal() {
        return subtotal;
    }

    public BigDecimal deliveryFee() {
        return deliveryFee;
    }

    public BigDecimal total() {
        return total;
    }
}
