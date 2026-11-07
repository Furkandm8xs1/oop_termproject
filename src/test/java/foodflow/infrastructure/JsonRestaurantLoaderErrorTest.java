package foodflow.infrastructure;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import foodflow.domain.Restaurant;

/**
 * Infrastructure tests for the loader: file handling, malformed input and
 * domain rules (BR-1) reported as DataLoadException instead of crashing.
 */
class JsonRestaurantLoaderErrorTest {

    private final JsonRestaurantLoader loader = new JsonRestaurantLoader();

    private static String restaurant(String extraFields, String menu) {
        return "{\"restaurants\":[{\"id\":\"R1\",\"name\":\"Test\",\"cuisine\":\"Turkish\","
                + "\"district\":\"Kadıköy\"," + extraFields + "\"menu\":" + menu + "}]}";
    }

    private static final String GOOD_MENU =
            "[{\"id\":\"R1-1\",\"name\":\"Soup\",\"category\":\"Starter\",\"price\":10}]";

    @Test
    void validJsonFromReaderIsLoaded() {
        List<Restaurant> result = loader.load(new StringReader(restaurant("\"deliveryFee\":5,", GOOD_MENU)));

        assertEquals(1, result.size());
        assertEquals("R1", result.get(0).id());
        assertEquals(new BigDecimal("5.00"), result.get(0).deliveryFee());
        assertEquals(new BigDecimal("10.00"), result.get(0).menu().get(0).price());
    }

    @Test
    void emptyRestaurantListIsAccepted() {
        assertTrue(loader.load(new StringReader("{\"restaurants\":[]}")).isEmpty());
    }

    @Test
    void restaurantWithEmptyMenuIsAccepted() {
        List<Restaurant> result = loader.load(new StringReader(restaurant("\"deliveryFee\":5,", "[]")));

        assertTrue(result.get(0).menu().isEmpty());
    }

    @Test
    void missingMinimumBasketDefaultsToZero() {
        List<Restaurant> result = loader.load(new StringReader(restaurant("\"deliveryFee\":5,", GOOD_MENU)));

        assertEquals(new BigDecimal("0.00"), result.get(0).minimumBasket());
    }

    @Test
    void emptyInputIsReportedAsDataLoadException() {
        assertThrows(DataLoadException.class, () -> loader.load(new StringReader("")));
    }

    @Test
    void restaurantsFieldOfWrongTypeIsReportedAsDataLoadException() {
        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader("{\"restaurants\":\"none\"}")));
    }

    @Test
    void missingRestaurantsArrayIsReportedWithAClearMessage() {
        DataLoadException e = assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader("{\"other\":[]}")));

        assertTrue(e.getMessage().contains("restaurants"));
    }

    @Test
    void negativePriceIsRejected_BR1() {
        String menu = "[{\"id\":\"R1-1\",\"name\":\"Bad\",\"category\":\"Main\",\"price\":-5}]";

        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("\"deliveryFee\":5,", menu))));
    }

    @Test
    void missingPriceIsRejected_BR1() {
        String menu = "[{\"id\":\"R1-1\",\"name\":\"NoPrice\",\"category\":\"Main\"}]";

        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("\"deliveryFee\":5,", menu))));
    }

    @Test
    void missingDeliveryFeeIsRejected() {
        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("", GOOD_MENU))));
    }

    @Test
    void negativeDeliveryFeeIsRejected() {
        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("\"deliveryFee\":-1,", GOOD_MENU))));
    }

    @Test
    void duplicateItemIdsInOneMenuAreRejected() {
        String menu = "[{\"id\":\"R1-1\",\"name\":\"A\",\"category\":\"Main\",\"price\":1},"
                + "{\"id\":\"r1-1\",\"name\":\"B\",\"category\":\"Main\",\"price\":2}]";

        assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("\"deliveryFee\":5,", menu))));
    }

    @Test
    void errorMessageNamesTheBrokenRestaurant() {
        String menu = "[{\"id\":\"R1-1\",\"name\":\"Bad\",\"category\":\"Main\",\"price\":0}]";

        DataLoadException e = assertThrows(DataLoadException.class,
                () -> loader.load(new StringReader(restaurant("\"deliveryFee\":5,", menu))));

        assertTrue(e.getMessage().contains("R1"));
    }

    @Test
    void directoryInsteadOfFileIsReportedAsDataLoadException(@TempDir Path dir) {
        DataLoadException e = assertThrows(DataLoadException.class, () -> loader.load(dir));

        assertNotNull(e.getCause());
    }

    @Test
    void utf8FileWithTurkishCharactersIsReadCorrectly(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("r.json");
        String json = restaurant("\"deliveryFee\":5,",
                "[{\"id\":\"R1-1\",\"name\":\"Çiğ Köfte\",\"category\":\"Ana Yemek\",\"price\":10}]");
        Files.writeString(file, json, StandardCharsets.UTF_8);

        List<Restaurant> result = loader.load(file);

        assertEquals("Çiğ Köfte", result.get(0).menu().get(0).name());
        assertEquals("Kadıköy", result.get(0).district());
    }

    @Test
    void emptyFileIsReportedAsDataLoadException(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("empty.json");
        Files.writeString(file, "", StandardCharsets.UTF_8);

        assertThrows(DataLoadException.class, () -> loader.load(file));
    }

    @Test
    void invalidJsonFileIsReportedAsDataLoadException(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("broken.json");
        Files.writeString(file, "{\"restaurants\": [ {", StandardCharsets.UTF_8);

        assertThrows(DataLoadException.class, () -> loader.load(file));
    }

    @Test
    void realDataFileHasUniqueRestaurantAndItemIds() {
        List<Restaurant> restaurants = loader.load(Path.of("data/restaurants.json"));

        long distinctRestaurants = restaurants.stream().map(Restaurant::id).distinct().count();
        long distinctItems = restaurants.stream().flatMap(r -> r.menu().stream())
                .map(i -> i.id().toLowerCase()).distinct().count();
        assertEquals(restaurants.size(), distinctRestaurants);
        assertEquals(restaurants.stream().mapToInt(r -> r.menu().size()).sum(), distinctItems);
    }

    @Test
    void everyPriceInTheRealDataFileIsPositive_BR1() {
        List<Restaurant> restaurants = loader.load(Path.of("data/restaurants.json"));

        assertTrue(restaurants.stream().flatMap(r -> r.menu().stream())
                .allMatch(i -> i.price().signum() > 0));
    }
}
