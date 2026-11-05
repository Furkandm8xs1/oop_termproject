package foodflow.infrastructure;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import foodflow.domain.MenuItem;
import foodflow.domain.Restaurant;

/**
 * Reads restaurants.json (UTF-8) and turns it into domain objects.
 * This is the only class that knows the JSON format.
 */
public class JsonRestaurantLoader {

    private final Gson gson = new Gson();

    public List<Restaurant> load(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return load(reader);
        } catch (IOException e) {
            throw new DataLoadException("Cannot read restaurant data file: " + file, e);
        }
    }

    public List<Restaurant> load(Reader reader) {
        FileDto dto;
        try {
            dto = gson.fromJson(reader, FileDto.class);
        } catch (JsonParseException e) {
            throw new DataLoadException("Restaurant data is not valid JSON: " + e.getMessage(), e);
        }
        if (dto == null || dto.restaurants == null) {
            throw new DataLoadException("Restaurant data must contain a 'restaurants' array.");
        }
        List<Restaurant> result = new ArrayList<>();
        for (RestaurantDto r : dto.restaurants) {
            try {
                result.add(toDomain(r));
            } catch (IllegalArgumentException e) {
                throw new DataLoadException("Invalid restaurant entry '" + r.id + "': " + e.getMessage(), e);
            }
        }
        return result;
    }

    private Restaurant toDomain(RestaurantDto r) {
        List<MenuItem> menu = new ArrayList<>();
        if (r.menu != null) {
            for (MenuItemDto m : r.menu) {
                menu.add(new MenuItem(m.id, m.name, m.category, m.price));
            }
        }
        return new Restaurant(r.id, r.name, r.cuisine, r.district, r.deliveryFee, r.minimumBasket, menu);
    }

    // ---- JSON shapes (private: nothing outside this class knows them) ----

    private static class FileDto {
        List<RestaurantDto> restaurants;
    }

    private static class RestaurantDto {
        String id;
        String name;
        String cuisine;
        String district;
        BigDecimal deliveryFee;
        BigDecimal minimumBasket;
        List<MenuItemDto> menu;
    }

    private static class MenuItemDto {
        String id;
        String name;
        String category;
        BigDecimal price;
    }
}