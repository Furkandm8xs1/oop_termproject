# CSE3063 · FoodFlow — Iteration 1 Report (D8)

## 1. Executive Summary & Iteration 1 Scope
Iteration 1 (*Inception & Walking Skeleton*) establishes the requirements, conceptual domain model, system sequence diagrams, operation contracts, and a working end-to-end layered console application for **FoodFlow**.

All **Must**, **Should**, and **Could** features for Iteration 1 are complete:
- **F1 (Identify Customer — Must):** Register with name, unique `@` e-mail, and delivery address (`district` + `street`), or log in by e-mail (`register`, `login`).
- **F2 (Browse Restaurants — Must):** List all restaurants loaded from `data/restaurants.json` with cuisine, district, and delivery fee (`restaurants`).
- **F3 (Filter Restaurants — Should):** Filter by `--cuisine` and/or `--district` (case- and Turkish-diacritic-insensitive) with polite empty-result messages.
- **F4 (View Menu — Must):** Display a restaurant's items grouped by category in order of appearance with formatted `TL` prices (`menu`).
- **F5 (Manage Cart — Must):** Add items (`add`), update quantities (`qty`), remove items (`remove`), clear (`clear`), and inspect cart totals (`cart`).
- **F6 (Place Order — Must):** Place a cash-on-delivery order (`order`) receiving a unique increasing order number (`1001+`), status `PLACED`, and a formatted receipt while emptying the cart.
- **F7 (My Orders — Could):** List all orders placed by the current customer in the active session (`orders`).

---

## 2. Deliverable Index (D1–D9)

| Deliverable | Document / Path | Summary |
|---|---|---|
| **D1: Vision & Scope** | [`docs/vision.md`](vision.md) | Problem statement, stakeholders, product features, I1 scope, and assumptions. |
| **D2: Glossary** | [`docs/glossary.md`](glossary.md) | 24 domain terms with definitions, business rule references (BR-1..BR-9), aliases, and rule-home mapping table. |
| **D3: Use-Case Model** | [`docs/use-cases.md`](use-cases.md), [`docs/diagrams/use-cases.puml`](diagrams/use-cases.puml) | Whole-product UML diagram (4 actors), 7 brief use cases (UC1–UC7), and fully dressed *Manage Cart* & *Place Order* with rule extensions. |
| **D4: Supplementary Spec** | [`docs/supplementary-spec.md`](supplementary-spec.md) | Checkable FURPS+ requirements table with verification procedures. |
| **D5: Domain Model** | [`docs/domain-model.md`](domain-model.md), [`docs/diagrams/domain-model.puml`](diagrams/domain-model.puml) | Conceptual UML class diagram with concepts, attributes, associations, and multiplicities (no methods or Java types). |
| **D6: SSD & Contracts** | [`docs/ssd-and-contracts.md`](ssd-and-contracts.md), [`docs/diagrams/ssd-place-order.puml`](diagrams/ssd-place-order.puml) | System Sequence Diagram for *Place Order* and operation contracts CO1 (`addItem`) & CO2 (`placeOrder`). |
| **D7: Working Software** | [`src/`](../src), [`README.md`](../README.md), [`docs/sample-run.txt`](sample-run.txt) | Java 21 Maven project, 4-layer architecture, 30+ JUnit 5 tests, Docker support, and live sample run transcript. |
| **D8: Iteration Report** | [`docs/iteration1-report.md`](iteration1-report.md), [`docs/design-decisions.md`](design-decisions.md), [`docs/ai-usage-log.md`](ai-usage-log.md) | Architectural rationale (including BR-7), rule traceability, contribution table, and AI usage log. |
| **D9: Plan for Iteration 2** | [`docs/iteration-plan.md`](iteration-plan.md) | Top-5 risk table with mitigations, Iteration 2 weekly plan, and Iteration 1 retrospective. |

---

## 3. Key Design Decisions & Rationale

Full details are documented in [`docs/design-decisions.md`](design-decisions.md). Below are the six primary design decisions:

1. **BR-7 Design Decision — Snapshot (`OrderLine`) vs. Live `MenuItem` Reference:**
   - *Context:* Brief section "Watch BR-7" asks whether `Order` should refer to live `MenuItem` objects or copy item names and prices at order time.
   - *Decision:* `Order.place(...)` converts each `CartLine` into an immutable `OrderLine` record (`OrderLine.snapshotOf`) that copies `itemId`, `itemName`, `unitPrice`, `quantity`, and `lineTotal`, and copies `restaurantName`, `subtotal`, `deliveryFee`, and `total` into `Order`.
   - *Rationale:* In Iteration 2, restaurant owners will edit menu prices and items. If `Order` referenced live `MenuItem` instances, changing a menu price would retroactively alter the total of already placed orders, violating BR-7. With the snapshot design, placed orders are immutable historical records (`OrderTest.orderKeepsItsPricesWhenCartChangesLater_BR7`).
2. **Strict Four-Layer Separation (`domain`, `application`, `infrastructure`, `ui`):**
   - Domain classes never read from `System.in`, never print to `System.out`, and have no knowledge of Gson or JSON. `OrderingService` provides one method per SSD operation, `JsonRestaurantLoader` encapsulates JSON parsing via private DTOs, and `ConsoleUI` handles user interaction and formatting.
3. **One Business Rule, One Class ("Rule Home"):**
   - Every rule lives in exactly one place: BR-1a (`MenuItem`), BR-1b (`CartLine`), BR-2 & BR-3 (`Cart.addItem`), BR-4 (`Order.place` & `Customer`), BR-5 (`Cart` / `CartLine` & `ConsoleFormatter`), BR-6 (`OrderNumberGenerator` & `Order`), BR-7 (`OrderLine` & `Order.place`), BR-8 (`Customer` & `CustomerRegistry`), and BR-9 (`Command` & `ConsoleUI`).
4. **Exact Monetary Arithmetic with `BigDecimal`:**
   - All monetary values use `BigDecimal` with scale `2` and `RoundingMode.HALF_UP` (`Texts.money`), avoiding floating-point precision errors and ensuring `%.2f TL` consistency (BR-5).
5. **Atomic Failure Semantics via Domain Exceptions:**
   - All domain preconditions are validated before mutating state. When `Cart.addItem` or `Order.place` throws `IllegalArgumentException` or `IllegalStateException`, the cart lines, quantities, and order number sequence remain unchanged.
6. **Read-Only UI Projection (`CartSummary`):**
   - `OrderingService.viewCart()` returns an immutable `CartSummary` DTO so the UI layer cannot mutate the internal `Cart` directly.

---

## 4. Verification & Testing Summary

Our JUnit 5 test suite covers all business rules and layers:
- **`CartTest` (9 tests):** Verifies BR-1 (quantities 0 and 21 rejected, 1 and 20 accepted, summed quantity > 20 rejected), BR-2 (second restaurant refused leaving cart untouched; allowed after `clear` or removing last item), BR-3 (duplicate item merges into single line), and BR-5 (`total = subtotal + deliveryFee`).
- **`CustomerTest` (7 tests):** Verifies BR-8 (invalid `@` formats rejected, duplicate e-mail rejected case-insensitively, lookup by e-mail), BR-4 (missing delivery address rejected), BR-1 (zero/negative menu item prices rejected), and Turkish character/case-insensitive catalog filtering.
- **`OrderTest` (9 tests):** Verifies BR-4 (empty cart or missing customer rejected without consuming an order number), BR-5 (order total calculation), BR-6 (unique increasing numbers starting at 1001 and `PLACED` status), and BR-7 (`OrderLine` snapshot immutability and cart emptying upon order placement).
- **`OrderingServiceTest`, `JsonRestaurantLoaderTest`, `CommandTest`, `ConsoleFormatterTest`:** Verify end-to-end service operations, UTF-8 JSON loading and error handling, CLI argument/option parsing (BR-9), and `%.2f TL` receipt/menu formatting.

---

## 5. Team Contribution Table

| Team Member (GitHub) | Role / Focus Areas | Key Contributions & Pull Requests |
|---|---|---|
| **Team Member 1** | DevOps, Infrastructure & App Wiring | Repository setup, Dockerfile & `docker-compose.yml`, GitHub Actions CI (`ci.yml`), `JsonRestaurantLoader`, `DataLoadException`, `JsonRestaurantLoaderTest`, `App.java` (PR #1, #2, #3, #5, #7, #12). |
| **Team Member 2** | Domain Model & Business Rules | Domain entities (`Cart`, `CartLine`, `Customer`, `CustomerRegistry`, `DeliveryAddress`, `MenuItem`, `Order`, `OrderLine`, `OrderNumberGenerator`, `Restaurant`, `RestaurantCatalog`, `Texts`) and domain unit tests (`CartTest`, `CustomerTest`, `OrderTest`, `TestData`) (PR #4, #6, #8). |
| **Team Member 3** | Application Service & Console UI | `OrderingService`, `CartSummary`, `OrderingServiceTest`, `ConsoleUI`, `Command`, `ConsoleFormatter`, `InvalidInputException`, `CommandTest`, `ConsoleFormatterTest`, `README.md` (PR #9, #11). |
| **Team Member 4** | Requirements, UML Models & Docs | Vision (`vision.md`), Glossary (`glossary.md`), Use Cases (`use-cases.md`, `use-cases.puml`), FURPS+ (`supplementary-spec.md`), Domain Model (`domain-model.md`, `domain-model.puml`), SSD & Contracts (`ssd-and-contracts.md`, `ssd-place-order.puml`), Design Decisions (`design-decisions.md`), Iteration Plan (`iteration-plan.md`), Sample Run (`sample-run.txt`) (PR #10). |

---

## 6. AI Usage Log

*(Also maintained in [`docs/ai-usage-log.md`](ai-usage-log.md).)*

| Tool | Purpose & Scope | Verification & Human Review |
|---|---|---|
| **GitHub Copilot / Gemini** | Assisted with GitHub Actions CI configuration, PlantUML/Mermaid diagram syntax, reviewing UP deliverable completeness (D1–D9) against the Iteration 1 brief, and formatting markdown tables. | All domain classes, business rule validations (BR-1..BR-9), and unit tests were verified against the project brief and tested with JUnit 5 (`mvn test`) and live console execution (`docs/sample-run.txt`). |
