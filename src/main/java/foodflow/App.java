package foodflow;

import foodflow.application.OrderingService;
import foodflow.domain.CustomerRegistry;
import foodflow.domain.OrderNumberGenerator;
import foodflow.domain.RestaurantCatalog;
import foodflow.infrastructure.DataLoadException;
import foodflow.infrastructure.JsonRestaurantLoader;
import foodflow.ui.ConsoleUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/** Creates and connects the objects, then starts the console UI. */
public final class App {

    private static final String DEFAULT_DATA_FILE = "data/restaurants.json";

    private App() {
    }

    /** Data file: first argument, else the FOODFLOW_DATA environment variable, else data/restaurants.json. */
    public static void main(String[] args) {
        String dataFile = args.length > 0 ? args[0]
                : System.getenv().getOrDefault("FOODFLOW_DATA", DEFAULT_DATA_FILE);

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        RestaurantCatalog catalog;
        try {
            catalog = new RestaurantCatalog(new JsonRestaurantLoader().load(Path.of(dataFile)));
        } catch (DataLoadException | IllegalArgumentException e) {
            System.err.println("Cannot start FoodFlow: " + e.getMessage());
            System.exit(1);
            return;
        }

        OrderingService service = new OrderingService(catalog, new CustomerRegistry(), new OrderNumberGenerator());
        boolean echo = Boolean.parseBoolean(System.getenv().getOrDefault("FOODFLOW_ECHO", "false"));
        new ConsoleUI(service, in, out, echo).run();
    }
}
