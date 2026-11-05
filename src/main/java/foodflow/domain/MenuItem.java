package foodflow.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** One item on a restaurant's menu. Immutable. BR-1 (price part): the price is always positive. */
public final class MenuItem {

    private final String id;
    private final String name;
    private final String category;
    private final BigDecimal price;

    public MenuItem(String id, String name, String category, BigDecimal price) {
        this.id = Texts.requireText(id, "Item id");
        this.name = Texts.requireText(name, "Item name");
        this.category = Texts.requireText(category, "Item category");
        BigDecimal scaled = Texts.money(price, "Price");
        if (scaled.signum() <= 0) {
            throw new IllegalArgumentException("Price of '" + name + "' must be positive.");
        }
        this.price = scaled;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String category() {
        return category;
    }

    public BigDecimal price() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MenuItem other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

