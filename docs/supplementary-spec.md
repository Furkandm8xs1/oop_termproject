# Supplementary Specification – FURPS+ (D4)

Every requirement in the FURPS+ table below is **checkable** and specifies its verification method.

| Category | ID | Requirement | How to Check / Verify |
|---|---|---|---|
| **Functionality** | FN-1 | All Iteration 1 features (F1–F6, plus F7) are reachable and executable through the console UI. | Run the demo commands in `README.md` and compare the console output with `docs/sample-run.txt`. |
| **Functionality** | FN-2 | Business rules BR-1 through BR-8 are enforced strictly in the `domain` layer, never in `ui`. | Run `mvn test` (`CartTest`, `CustomerTest`, `OrderTest`); inspect `ConsoleUI` to verify zero validation logic on prices, quantities, or e-mail format. |
| **Functionality** | FN-3 | Restaurant and menu data are loaded dynamically at start-up from `data/restaurants.json` (not hard-coded). | Modify a price or restaurant name in `data/restaurants.json`, restart the application, and run `restaurants` / `menu`. |
| **Usability** | US-1 | Every invalid input or business rule violation produces a single-line, human-readable message starting with `Error:` and never prints a Java stack trace. | Enter `add`, `add R01-01 two`, `add R01-01 99`, or `unknowncmd`; verified in `CommandTest` and `docs/sample-run.txt`. |
| **Usability** | US-2 | Typing `help` lists all supported commands and their syntax. | Run `help` in the console UI. |
| **Usability** | US-3 | Restaurant search filters (`--cuisine`, `--district`) and IDs are case-insensitive and ignore Turkish diacritics (e.g. `kadikoy` matches `Kadıköy`). | Run `restaurants --district kadikoy`; verified in `CustomerTest.filterIgnoresCaseAndTurkishCharacters`. |
| **Reliability** | RE-1 | Invalid user input or rule violations never terminate the application loop (BR-9). | Verified by `CommandTest` and interactive error cases in `docs/sample-run.txt`. |
| **Reliability** | RE-2 | Any failed cart or order operation is atomic and leaves existing state untouched (cart lines, quantities, and order number sequence remain unchanged). | Verified by `CartTest.itemFromAnotherRestaurantIsRefusedAndCartIsUnchanged_BR2` and `OrderTest.emptyCartCannotBeOrderedAndConsumesNoNumber_BR4`. |
| **Reliability** | RE-3 | A missing, unreadable, or malformed JSON data file aborts start-up with a clear error message and non-zero exit code (`1`). | Run with `FOODFLOW_DATA=nonexistent.json` or verify `JsonRestaurantLoaderTest`. |
| **Performance** | PE-1 | Application start-up and JSON parsing of `data/restaurants.json` complete in under 1 second; console commands respond in under 50 ms. | Execute `java -jar target/foodflow.jar` and measure response time during interactive demo. |
| **Supportability** | SU-1 | Strict 4-layer separation (`domain`, `application`, `infrastructure`, `ui`): `domain` has zero imports from `com.google.gson` and never accesses `System.in` or `System.out`. | Inspect imports across `src/main/java/foodflow/domain/*.java`. |
| **Supportability** | SU-2 | The project builds, runs all unit tests, and packages into an executable JAR inside Docker with a single command. | Run `docker compose build` and `docker compose --profile test run --rm test`. |
| **Supportability** | SU-3 | Automated JUnit 5 test suite with at least 10 meaningful domain tests covering BR-1 through BR-8. | Run `mvn test` (30+ unit tests across domain, application, infrastructure, and UI). |
| **+ Design Constraints** | DC-1 | Implemented in Java 21 with Maven and Gson; in-memory session storage only (no external database in Iteration 1). | Inspect `pom.xml`. |
| **+ Implementation** | IM-1 | All source files, JSON files, and console streams use `UTF-8` encoding so Turkish characters (`ş, ğ, ı, İ, ö, ü, ç`) render accurately. | Inspect `App.java` (`StandardCharsets.UTF_8`), `JsonRestaurantLoader.java`, and `pom.xml`. |
| **+ Interface** | IF-1 | Text-based interactive command-line interface (`ConsoleUI`); no GUI in Iteration 1. | Inspect `src/main/java/foodflow/ui/ConsoleUI.java`. |
| **+ Legal / Business** | LG-1 | All monetary values are calculated with `BigDecimal` (scale 2, `HALF_UP`), displayed with two decimals and `TL`, and settled via cash on delivery. | Verified in `ConsoleFormatterTest.moneyHasTwoDecimalsAndTL_BR5` and `OrderTest`. |
