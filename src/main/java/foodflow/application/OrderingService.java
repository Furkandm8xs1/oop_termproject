package foodflow.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import foodflow.domain.Cart;
import foodflow.domain.Customer;
import foodflow.domain.CustomerRegistry;
import foodflow.domain.DeliveryAddress;
import foodflow.domain.MenuItem;
import foodflow.domain.Order;
import foodflow.domain.OrderNumberGenerator;
import foodflow.domain.Restaurant;
import foodflow.domain.RestaurantCatalog;

/**
 * Use-case logic of one customer session: one public method per system operation.
 * It coordinates domain objects; the business rules themselves live in the domain classes.
 * Rule violations surface as IllegalArgumentException / IllegalStateException.
 */
public class OrderingService {

    private final RestaurantCatalog catalog;
    private final CustomerRegistry customers;
    private final OrderNumberGenerator orderNumbers;
    private final Cart cart = new Cart();
    private final List<Order> placedOrders = new ArrayList<>();
    private Customer currentCustomer;

    public OrderingService(RestaurantCatalog catalog, CustomerRegistry customers, OrderNumberGenerator orderNumbers) {
        this.catalog = catalog;
        this.customers = customers;
        this.orderNumbers = orderNumbers;
    }

    // ---- F1: identify the customer ----

    public Customer register(String name, String email, String district, String street) {
        Customer customer = customers.register(name, email, new DeliveryAddress(district, street));
        currentCustomer = customer;
        return customer;
    }

    public Customer login(String email) {
        Customer customer = customers.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No customer with e-mail '" + email + "'. Register first."));
        currentCustomer = customer;
        return customer;
    }

    public Optional<Customer> currentCustomer() {
        return Optional.ofNullable(currentCustomer);
    }

    // ---- F2, F3, F4: browse ----

    public List<Restaurant> listRestaurants(String cuisine, String district) {
        return catalog.search(cuisine, district);
    }

    public Restaurant viewMenu(String restaurantId) {
        return catalog.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown restaurant: " + restaurantId));
    }

    // ---- F5: cart ----

    /** Returns the item that was added. */
    public MenuItem addItem(String itemId, int quantity) {
        Restaurant restaurant = catalog.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown menu item: " + itemId));
        MenuItem item = restaurant.findItem(itemId).orElseThrow();
        cart.addItem(restaurant, item, quantity);
        return item;
    }

    public void changeQuantity(String itemId, int quantity) {
        cart.changeQuantity(itemId, quantity);
    }

    public void removeItem(String itemId) {
        cart.removeItem(itemId);
    }

    public void clearCart() {
        cart.clear();
    }

    /** A read-only snapshot; the real cart can only be changed through this service. */
    public CartSummary viewCart() {
        return CartSummary.of(cart);
    }

    // ---- F6, F7: orders ----

    public Order placeOrder() {
        Order order = Order.place(orderNumbers, currentCustomer, cart);
        placedOrders.add(order); // the cart was emptied by Order.place (BR-7)
        return order;
    }

    public List<Order> myOrders() {
        if (currentCustomer == null) {
            throw new IllegalStateException("Identify yourself first (register or login).");
        }
        return placedOrders.stream().filter(o -> o.customer().equals(currentCustomer)).toList();
    }
}