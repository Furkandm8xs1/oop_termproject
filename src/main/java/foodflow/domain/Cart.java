package foodflow.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The customer's shopping cart.
 * BR-2: items from one restaurant only. BR-3: one line per item.
 * BR-5: the only place that calculates subtotal, delivery fee and total.
 */
public final class Cart {

    private Restaurant restaurant; // null while the cart is empty
    private final Map<String, CartLine> lines = new LinkedHashMap<>(); // keyed by item id

    public void addItem(Restaurant fromRestaurant, MenuItem item, int quantity) {
        if (fromRestaurant == null || item == null) {
            throw new IllegalArgumentException("Restaurant and item are required.");
        }
        if (!fromRestaurant.hasItem(item)) {
            throw new IllegalArgumentException("'" + item.name() + "' is not on the menu of " + fromRestaurant.name() + ".");
        }
        CartLine.requireValidQuantity(quantity);
        if (restaurant != null && !restaurant.equals(fromRestaurant)) {
            throw new IllegalStateException("Your cart has items from " + restaurant.name()
                    + ". Empty the cart first to order from " + fromRestaurant.name() + ".");
        }
        CartLine existing = lines.get(item.id());
        if (existing != null) {
            existing.changeQuantity(existing.quantity() + quantity); // BR-3, validated before changing
        } else {
            lines.put(item.id(), new CartLine(item, quantity));
        }
        restaurant = fromRestaurant;
    }

    public void changeQuantity(String itemId, int newQuantity) {
        findLine(itemId).changeQuantity(newQuantity);
    }

    public void removeItem(String itemId) {
        CartLine line = findLine(itemId);
        lines.remove(line.item().id());
        if (lines.isEmpty()) {
            restaurant = null;
        }
    }

    public void clear() {
        lines.clear();
        restaurant = null;
    }

    private CartLine findLine(String itemId) {
        String wanted = Texts.normalize(itemId);
        return lines.values().stream()
                .filter(l -> Texts.normalize(l.item().id()).equals(wanted))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item " + itemId + " is not in your cart."));
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public Optional<Restaurant> restaurant() {
        return Optional.ofNullable(restaurant);
    }

    public List<CartLine> lines() {
        return List.copyOf(new ArrayList<>(lines.values()));
    }

    public BigDecimal subtotal() {
        return lines.values().stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal deliveryFee() {
        return restaurant == null ? BigDecimal.ZERO.setScale(2) : restaurant.deliveryFee();
    }

    /** BR-5: subtotal plus the restaurant's delivery fee. */
    public BigDecimal total() {
        return subtotal().add(deliveryFee());
    }
}

