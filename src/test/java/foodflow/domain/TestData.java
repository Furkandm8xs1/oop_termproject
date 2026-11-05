package foodflow.domain;

import java.math.BigDecimal;
import java.util.List;

/** Small hand-made restaurants shared by the tests. */
public final class TestData {

    private TestData() {
    }

    public static MenuItem item(String id, String name, String category, String price) {
        return new MenuItem(id, name, category, new BigDecimal(price));
    }

    public static Restaurant kebab() {
        return new Restaurant("R01", "Kebapçı Mehmet", "Turkish", "Kadıköy",
                new BigDecimal("25.0"), new BigDecimal("150.0"),
                List.of(item("R01-01", "Lahmacun", "Main", "140.0"),
                        item("R01-05", "Ayran", "Drink", "40.0")));
    }

    public static Restaurant pizza() {
        return new Restaurant("R02", "Pizzeria Napoli", "Italian", "Beşiktaş",
                new BigDecimal("30.0"), new BigDecimal("200.0"),
                List.of(item("R02-01", "Margherita", "Main", "180.0")));
    }

    public static RestaurantCatalog catalog() {
        return new RestaurantCatalog(List.of(kebab(), pizza()));
    }

    public static Customer ali() {
        return new Customer("Ali Yılmaz", "ali@example.com", new DeliveryAddress("Kadıköy", "Moda Cad. 5"));
    }
}

