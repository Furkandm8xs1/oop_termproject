package foodflow.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import foodflow.domain.Cart;
import foodflow.domain.CartLine;
import foodflow.domain.MenuItem;
import foodflow.domain.Order;
import foodflow.domain.OrderLine;
import foodflow.domain.Restaurant;

/**
 * Turns domain objects into console text. It only formats, it calculates nothing
 * (amounts come from the domain, BR-5) and prints nothing (the caller decides where text goes).
 */
public final class ConsoleFormatter {

    private static final String NL = System.lineSeparator();

    /** Every amount is shown with two decimals and TL */
    public String money(BigDecimal amount) {
        return String.format(Locale.ROOT, "%.2f TL", amount);
    }

    public String restaurantList(List<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            return "  No restaurants match your search. Try another cuisine or district.";
        }
        StringJoiner out = new StringJoiner(NL);
        for (Restaurant r : restaurants) {
            out.add(String.format(Locale.ROOT, "  %-4s %-22s %-11s %-11s delivery %s",
                    r.id(), r.name(), r.cuisine(), r.district(), money(r.deliveryFee())));
        }
        return out.toString();
    }

    public String menu(Restaurant restaurant) {
        StringJoiner out = new StringJoiner(NL);
        out.add("  " + restaurant.name() + " (" + restaurant.cuisine() + ", " + restaurant.district() + ")");
        for (Map.Entry<String, List<MenuItem>> group : restaurant.menuByCategory().entrySet()) {
            out.add("  " + group.getKey());
            for (MenuItem item : group.getValue()) {
                out.add(String.format(Locale.ROOT, "    %-8s %-24s %12s",
                        item.id(), item.name(), money(item.price())));
            }
        }
        return out.toString();
    }

    public String cart(Cart cart) {
        if (cart.isEmpty()) {
            return "  Your cart is empty.";
        }
        StringJoiner out = new StringJoiner(NL);
        cart.restaurant().ifPresent(r -> out.add("  Cart from " + r.name()));
        for (CartLine line : cart.lines()) {
            out.add(String.format(Locale.ROOT, "    %-8s %2d x %-20s %12s",
                    line.item().id(), line.quantity(), line.item().name(), money(line.lineTotal())));
        }
        out.add(amountRow("Subtotal", cart.subtotal()));
        out.add(amountRow("Delivery fee", cart.deliveryFee()));
        out.add(amountRow("Total", cart.total()));
        return out.toString();
    }

    public String receipt(Order order) {
        StringJoiner out = new StringJoiner(NL);
        out.add("  ---------------- RECEIPT ----------------");
        out.add("  Order #" + order.number() + "  status: " + order.status());
        out.add("  Restaurant: " + order.restaurantName());
        out.add("  Customer:   " + order.customer().name() + " <" + order.customer().email() + ">");
        out.add("  Deliver to: " + order.deliveryAddress());
        for (OrderLine line : order.lines()) {
            out.add(String.format(Locale.ROOT, "    %2d x %-20s %10s %12s",
                    line.quantity(), line.itemName(), money(line.unitPrice()), money(line.lineTotal())));
        }
        out.add(amountRow("Subtotal", order.subtotal()));
        out.add(amountRow("Delivery fee", order.deliveryFee()));
        out.add(amountRow("Total", order.total()));
        out.add("  Payment: cash on delivery");
        out.add("  -----------------------------------------");
        return out.toString();
    }

    public String orderPlaced(Order order) {
        return "Order #" + order.number() + " placed: " + money(order.total())
                + " (incl. delivery " + money(order.deliveryFee()) + "), cash on delivery.";
    }

    public String orderHistory(List<Order> orders) {
        if (orders.isEmpty()) {
            return "  You have not placed any orders in this session.";
        }
        StringJoiner out = new StringJoiner(NL);
        for (Order o : orders) {
            out.add(String.format(Locale.ROOT, "  #%d  %-22s %-7s %12s",
                    o.number(), o.restaurantName(), o.status(), money(o.total())));
        }
        return out.toString();
    }

    private String amountRow(String label, BigDecimal amount) {
        return String.format(Locale.ROOT, "  %-37s %12s", label + ":", money(amount));
    }
}
