# Architectural & Design Decisions (D8)

## 1. Strict Four-Layer Architecture (`domain`, `application`, `infrastructure`, `ui`)
- **Decision:** Separate the system into four cohesive packages wired together by `App`:
  - `foodflow.domain`: Pure domain concepts and business rules (BR-1 through BR-8). Zero I/O (`System.in` / `System.out`) and zero JSON/Gson dependencies.
  - `foodflow.application`: Use-case orchestration (`OrderingService`) exposing one public method per SSD system operation (`register`, `login`, `listRestaurants`, `viewMenu`, `addItem`, `changeQuantity`, `removeItem`, `clearCart`, `viewCart`, `placeOrder`, `myOrders`).
  - `foodflow.infrastructure`: `JsonRestaurantLoader` and `DataLoadException`, isolating the UTF-8 JSON schema (`data/restaurants.json`) in private DTO classes so nothing outside `infrastructure` knows JSON exists.
  - `foodflow.ui`: `ConsoleUI`, `Command`, and `ConsoleFormatter`, responsible solely for parsing CLI input, invoking `OrderingService`, catching exceptions (BR-9), and formatting output (`%.2f TL`).
- **Rationale:** Satisfies the grading criteria for Separation of Concerns, enables fast unit testing of domain and application logic without console streams, and allows future iterations to add a GUI or persistence layer without touching domain classes.

## 2. BR-7 Decision: `OrderLine` Snapshots vs. Live `MenuItem` References
- **Question ("Watch BR-7" in Brief):** Should an `Order` refer to live `MenuItem` objects, or record the name and price at the moment of ordering?
- **Decision:** `Order` and `OrderLine` store an **immutable snapshot** (`itemId`, `itemName`, `unitPrice`, `quantity`, `lineTotal`, `restaurantName`, `deliveryFee`) copied at the instant `Order.place(...)` executes. `OrderLine` has **no reference** to `MenuItem`.
- **Alternatives Considered:**
  - *Alternative A (Live Reference):* `OrderLine` holds a reference to `MenuItem` and computes `item.price() * quantity` on the fly. While slightly simpler initially, if a restaurant owner updates a menu item's price or renames/deletes an item in Iteration 2, previously placed orders and historical receipts would silently change their totals—violating BR-7 and corrupting financial history.
  - *Alternative B (Value Snapshot — Chosen):* Copy the item ID, name, unit price, quantity, and calculated totals into immutable `OrderLine` and `Order` fields when the order is placed.
- **Consequences:** Historical orders are 100% immune to subsequent cart modifications or future Iteration 2 menu price changes (verified by unit tests in `OrderTest`).

## 3. Single Home per Business Rule ("One Rule, One Class")
- **Decision:** Every business rule BR-1 through BR-9 is enforced in a single, dedicated class (documented in [`docs/glossary.md`](glossary.md)):
  - Positive item price (BR-1a) → `MenuItem` constructor
  - Line quantity 1..20 (BR-1b) → `CartLine.requireValidQuantity`
  - Single restaurant per cart (BR-2) & single line per item (BR-3) → `Cart.addItem`
  - Non-empty cart & customer delivery address required (BR-4) → `Order.place` & `Customer`
  - Totals calculation (`subtotal + deliveryFee`, BR-5) → `Cart` / `CartLine` (and `ConsoleFormatter` for `%.2f TL` formatting)
  - Unique increasing order numbers (BR-6a) → `OrderNumberGenerator`
  - Initial `PLACED` status (BR-6b) & emptying cart on placement (BR-7b) → `Order.place`
  - E-mail format (`@`, BR-8a) → `Customer`; e-mail uniqueness (BR-8b) → `CustomerRegistry`
  - Graceful error handling on invalid input (BR-9) → `Command` & `ConsoleUI`
- **Rationale:** Eliminates duplicate validation logic across layers and prevents rules from drifting out of sync.

## 4. Monetary Representation with `BigDecimal`
- **Decision:** Represent all prices, line totals, subtotals, delivery fees, and order totals using `java.math.BigDecimal` scaled to 2 decimal places with `RoundingMode.HALF_UP`.
- **Rationale:** Binary floating-point (`double`) introduces representation artifacts (e.g., `0.1 + 0.2 = 0.30000000000000004`), which can cause rounding discrepancies on receipts. `BigDecimal` guarantees exact decimal arithmetic required by BR-5.

## 5. Exception-Driven Rule Enforcement & Atomic Failure Semantics
- **Decision:** Domain classes throw `IllegalArgumentException` for invalid values (bad price, quantity outside 1..20, invalid e-mail, unknown ID) and `IllegalStateException` for invalid state transitions (adding an item from a second restaurant, placing an order with an empty cart or unauthenticated customer, duplicate registration). All validations run **before** any state mutation.
- **Rationale:** Guarantees failure atomicity—when an operation fails, the cart contents, line quantities, and `OrderNumberGenerator` sequence remain completely untouched. `ConsoleUI` catches these exceptions in one place and prints a friendly message (BR-9).

## 6. Encapsulated Read-Only Cart Projection (`CartSummary`)
- **Decision:** `OrderingService.viewCart()` returns an immutable `CartSummary` record rather than exposing the mutable `Cart` domain object to `ConsoleUI`.
- **Rationale:** Prevents the UI layer from accidentally mutating `Cart` state directly (`cart.clear()`, `cart.addItem(...)`) outside `OrderingService`.

## 7. No Static Mutable State
- **Decision:** `OrderNumberGenerator`, `CustomerRegistry`, `RestaurantCatalog`, and `OrderingService` are instantiated as regular objects in `App.main` and injected via constructors.
- **Rationale:** Avoids global static state (a common Iteration 1 pitfall listed in the brief) and allows every JUnit test to run in complete isolation with a fresh object graph.

## 8. Diacritic- and Case-Insensitive Text Matching (`Texts.normalize`)
- **Decision:** Normalize filter strings and IDs by trimming whitespace, folding Turkish dotted/dotless I (`İ`/`ı` → `i`), converting to lower case, and stripping Unicode combining marks (`Normalizer.Form.NFD`).
- **Rationale:** Allows users on any keyboard layout to type `restaurants --district kadikoy` or `restaurants --district KADIKÖY` and match `Kadıköy` seamlessly, while preserving the original Turkish spelling for display.
