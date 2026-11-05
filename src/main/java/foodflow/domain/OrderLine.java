package foodflow.domain;

import java.math.BigDecimal;

/**
 * A line of a placed order. It copies the item's id, name and price at the moment of ordering
 * instead of pointing to the live {@link MenuItem}, so later menu changes never alter an old
 * order (BR-7). The line total is copied from the cart line, not calculated again (BR-5).
 */
public record OrderLine(String itemId, String itemName, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {

    static OrderLine snapshotOf(CartLine line) {
        return new OrderLine(line.item().id(), line.item().name(), line.item().price(),
                line.quantity(), line.lineTotal());
    }
}
