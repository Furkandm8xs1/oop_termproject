package foodflow.domain;

import java.math.BigDecimal;

/**
 * One line of the cart: a menu item and a quantity.
 * BR-1 (quantity part): the quantity is between 1 and 20.
 * Only {@link Cart} can create or change lines, so nobody can bypass the cart's rules.
 */
public final class CartLine {

    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 20;

    private final MenuItem item;
    private int quantity;

    CartLine(MenuItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required.");
        }
        requireValidQuantity(quantity);
        this.item = item;
        this.quantity = quantity;
    }

    static void requireValidQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException(
                    "Quantity must be between " + MIN_QUANTITY + " and " + MAX_QUANTITY + " (got " + quantity + ").");
        }
    }

    public MenuItem item() {
        return item;
    }

    public int quantity() {
        return quantity;
    }

    /** Validates first, so a rejected change leaves the line untouched. */
    void changeQuantity(int newQuantity) {
        requireValidQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    /** BR-5: price times quantity. */
    public BigDecimal lineTotal() {
        return item.price().multiply(BigDecimal.valueOf(quantity));
    }
}
