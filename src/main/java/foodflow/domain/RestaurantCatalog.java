package foodflow.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** The collection of all restaurants the customer can order from. */
public final class RestaurantCatalog {

    private final List<Restaurant> restaurants;

    public RestaurantCatalog(List<Restaurant> restaurants) {
        if (restaurants == null) {
            throw new IllegalArgumentException("Restaurants are required.");
        }
        Set<String> restaurantIds = new HashSet<>();
        Set<String> itemIds = new HashSet<>();
        for (Restaurant r : restaurants) {
            if (!restaurantIds.add(Texts.normalize(r.id()))) {
                throw new IllegalArgumentException("Duplicate restaurant id: " + r.id());
            }
            for (MenuItem item : r.menu()) {
                if (!itemIds.add(Texts.normalize(item.id()))) {
                    throw new IllegalArgumentException("Duplicate menu item id across restaurants: " + item.id());
                }
            }
        }
        this.restaurants = List.copyOf(restaurants);
    }

    public List<Restaurant> all() {
        return restaurants;
    }

    public Optional<Restaurant> findById(String restaurantId) {
        String wanted = Texts.normalize(restaurantId);
        return restaurants.stream().filter(r -> Texts.normalize(r.id()).equals(wanted)).findFirst();
    }

    public Optional<Restaurant> findByItemId(String itemId) {
        return restaurants.stream().filter(r -> r.findItem(itemId).isPresent()).findFirst();
    }

    /** Filters by cuisine and/or district; null or blank means "any". */
    public List<Restaurant> search(String cuisine, String district) {
        return restaurants.stream().filter(r -> r.matches(cuisine, district)).toList();
    }
}

