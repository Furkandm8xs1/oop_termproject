package foodflow.application;

import java.math.BigDecimal;
import java.util.List;

import foodflow.domain.Cart;


public record CartSummary(String restaurantName, List<Line> lines,
                          BigDecimal subtotal, BigDecimal deliveryFee, BigDecimal total) {

    public record Line(String itemId, String itemName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    static CartSummary of(Cart cart) {
        List<Line> lines = cart.lines().stream()
                .map(l -> new Line(l.item().id(), l.item().name(), l.quantity(), l.item().price(), l.lineTotal()))
                .toList();
        String restaurant = cart.restaurant().map(r -> r.name()).orElse(null);
        return new CartSummary(restaurant, lines, cart.subtotal(), cart.deliveryFee(), cart.total());
    }
}