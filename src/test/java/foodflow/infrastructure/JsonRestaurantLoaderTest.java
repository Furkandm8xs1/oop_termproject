package foodflow.infrastructure;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import foodflow.domain.Restaurant;

class JsonRestaurantLoaderTest {

    private final JsonRestaurantLoader loader = new JsonRestaurantLoader();

    @Test
    void loadsExpectedRestaurantsAndItemsWithTurkishCharacters() throws URISyntaxException {
        Path file = Path.of(getClass().getResource("/test-restaurants.json").toURI());

        List<Restaurant> restaurants = loader.load(file);

        assertEquals(2, restaurants.size());
        Restaurant first = restaurants.get(0);
        assertEquals("Çiğköfteci Şükrü", first.name());
        assertEquals("Üsküdar", first.district());
        assertEquals(new BigDecimal("12.50"), first.deliveryFee());
        assertEquals(2, first.menu().size());
        assertEquals("Çiğ Köfte Dürüm", first.menu().get(0).name());
        assertEquals(new BigDecimal("30.50"), first.menu().get(1).price());
        assertEquals(new BigDecimal("0.00"), restaurants.get(1).minimumBasket());
    }

    @Test
    void realDataFileHasSixRestaurantsAndThirtyFiveItems() {
        List<Restaurant> restaurants = loader.load(Path.of("data/restaurants.json"));

        assertEquals(6, restaurants.size());
        assertEquals(35, restaurants.stream().mapToInt(r -> r.menu().size()).sum());
    }

    @Test
    void invalidDataIsReportedAsDataLoadException() {
        String badPrice = """
                {"restaurants":[{"id":"X","name":"X","cuisine":"c","district":"d","deliveryFee":1,
                "menu":[{"id":"X-1","name":"Free","category":"Main","price":0}]}]}""";

        assertThrows(DataLoadException.class, () -> loader.load(new StringReader(badPrice)));
        assertThrows(DataLoadException.class, () -> loader.load(new StringReader("not json")));
        assertThrows(DataLoadException.class, () -> loader.load(new StringReader("{}")));
        assertThrows(DataLoadException.class, () -> loader.load(Path.of("does-not-exist.json")));
    }
}