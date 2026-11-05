package foodflow.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** A restaurant with its menu. Immutable once created. */
public final class Restaurant {

    private final String id;
    private final String name;
    private final String cuisine;
    private final String district;
    private final BigDecimal deliveryFee;
    private final BigDecimal minimumBasket; // read now, its rule belongs to Iteration 2
    private final List<MenuItem> menu;

    public Restaurant(String id, String name, String cuisine, String district,
                      BigDecimal deliveryFee, BigDecimal minimumBasket, List<MenuItem> menu) {
        this.id = Texts.requireText(id, "Restaurant id");
        this.name = Texts.requireText(name, "Restaurant name");
        this.cuisine = Texts.requireText(cuisine, "Cuisine");
        this.district = Texts.requireText(district, "District");
        this.deliveryFee = Texts.money(deliveryFee, "Delivery fee");
        if (this.deliveryFee.signum() < 0) {
            throw new IllegalArgumentException("Delivery fee of '" + name + "' must not be negative.");
        }
        this.minimumBasket = Texts.money(minimumBasket == null ? BigDecimal.ZERO : minimumBasket, "Minimum basket");
        if (menu == null) {
            throw new IllegalArgumentException("Menu is required.");
        }
        Set<String> seen = new LinkedHashSet<>();
        for (MenuItem item : menu) {
            if (!seen.add(Texts.normalize(item.id()))) {
                throw new IllegalArgumentException("Duplicate menu item id: " + item.id());
            }
        }
        this.menu = List.copyOf(menu);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String cuisine() {
        return cuisine;
    }

    public String district() {
        return district;
    }

    public BigDecimal deliveryFee() {
        return deliveryFee;
    }

    public BigDecimal minimumBasket() {
        return minimumBasket;
    }

    public List<MenuItem> menu() {
        return menu;
    }

    public Optional<MenuItem> findItem(String itemId) {
        String wanted = Texts.normalize(itemId);
        return menu.stream().filter(i -> Texts.normalize(i.id()).equals(wanted)).findFirst();
    }

    public boolean hasItem(MenuItem item) {
        return menu.contains(item);
    }

    /** Menu items grouped by category, in order of first appearance. */
    public Map<String, List<MenuItem>> menuByCategory() {
        Map<String, List<MenuItem>> grouped = new LinkedHashMap<>();
        for (MenuItem item : menu) {
            grouped.computeIfAbsent(item.category(), c -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    /** A null or blank criterion matches everything; text comparison ignores case and diacritics. */
    public boolean matches(String cuisineFilter, String districtFilter) {
        return matchesText(cuisine, cuisineFilter) && matchesText(district, districtFilter);
    }

    private static boolean matchesText(String actual, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return Texts.normalize(actual).equals(Texts.normalize(filter));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Restaurant other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

