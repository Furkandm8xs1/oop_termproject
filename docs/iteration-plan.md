# Iteration Plan, Risk List, and Retrospective (D9)

## 1. Top-5 Risks for Iteration 2 & Mitigations

| # | Risk | Likelihood / Impact | Mitigation Strategy |
|---|---|---|---|
| **R1** | Iteration 2 business rules (minimum basket amount, restaurant opening hours) leak into `ConsoleUI` or `OrderingService` instead of living in the domain layer. | Medium / High | Define the rule homes in `docs/glossary.md` first (`Restaurant` for minimum basket / opening hours, checked during `Order.place`); write domain unit tests before updating the UI; enforce in PR reviews. |
| **R2** | Order lifecycle state transitions (`PLACED` → `ACCEPTED` → `PREPARING` → `DELIVERED` / `REJECTED`) violate invalid state sequences or break `Order` immutability. | Medium / High | Keep `Order` snapshot fields (`lines`, `subtotal`, `deliveryFee`, `total`, `deliveryAddress`) `final` and immutable; encapsulate allowed state transitions inside `OrderStatus` / `Order` methods with dedicated JUnit transition tests. |
| **R3** | Adding Restaurant Owner and Courier actors causes `ConsoleUI` to grow into a monolithic 600-line class. | High / Medium | Keep `Command` parsing modular and split actor-specific commands or services (`OrderingService`, `RestaurantOwnerService`, `CourierService`) while reusing `ConsoleFormatter`. |
| **R4** | Merge conflicts and uneven workload across team members when touching shared domain classes (`Restaurant`, `Order`). | Medium / Medium | Assign clear class/feature ownership per iteration milestone, keep pull requests small and focused on a single feature/rule, and require at least one peer review before merging to `main`. |
| **R5** | Analysis artifacts (Glossary, Use Cases, Domain Model, SSD/Contracts) drift out of sync with the Java implementation as new features arrive. | High / Medium | Treat documentation updates as part of the Definition of Done for every PR; verify glossary terms and rule homes against the codebase before tagging `iteration-2`. |

---

## 2. Plan for Iteration 2

| Week | Milestone & Tasks | Deliverable / Target Classes | Owner |
|---|---|---|---|
| **Week 8** | Update Vision, Glossary, and Use-Case Model for Restaurant Owner (menu updates, accept/reject order) and new business rules (minimum basket, opening hours). | `docs/vision.md`, `docs/glossary.md`, `docs/use-cases.md` | Team (Requirements) |
| **Week 9** | Implement Domain layer extensions: `minimumBasket` enforcement in `Order.place`, `OpeningHours` value object, and valid `OrderStatus` transitions with unit tests. | `Restaurant`, `Order`, `OrderStatus`, `OrderTest` | Team (Domain) |
| **Week 10** | Extend `JsonRestaurantLoader` for opening hours; add application & console commands for owner/courier workflows and minimum-basket error reporting. | `JsonRestaurantLoader`, `OrderingService`, `ConsoleUI` | Team (App / Infra / UI) |
| **Week 11** | End-to-end integration testing, updated `docs/sample-run.txt`, Iteration 2 report, and `iteration-2` Git tag. | `README.md`, `docs/sample-run.txt`, Report | All Members |

---

## 3. Iteration 1 Plan & Retrospective

### Iteration 1 Summary
- **Goal:** Deliver the walking skeleton of FoodFlow, including requirements & domain modeling (D1–D6) and a tested, layered console application covering F1–F7 and business rules BR-1 through BR-9 (D7–D9).
- **Scope Completed:** Core domain models (`Customer`, `CustomerRegistry`, `DeliveryAddress`, `Restaurant`, `RestaurantCatalog`, `MenuItem`, `Cart`, `CartLine`, `Order`, `OrderLine`, `OrderNumberGenerator`, `OrderStatus`), UTF-8 JSON loader (`JsonRestaurantLoader`), application service (`OrderingService`, `CartSummary`), interactive console UI (`ConsoleUI`, `Command`, `ConsoleFormatter`), Docker build setup, CI workflow, and 35+ JUnit 5 tests.

### Retrospective (Iteration 1)
- **Keep (What went well):**
  - Dividing the work early across domain modeling, infrastructure, application/UI layers, and documentation.
  - Enforcing strict layer separation so domain unit tests could be written and verified rapidly without I/O dependencies.
  - Using GitHub Pull Requests and GitHub Actions CI to automatically verify that every merge compiles and passes `mvn test`.
- **Problem (What could be improved):**
  - Setting up the Git/GitHub branch workflow, Docker build context, and PlantUML/Mermaid diagrams took more time than initially estimated.
- **Try (Action items for Iteration 2):**
  - Create smaller, incremental pull requests and assign reviewers earlier in the week to speed up the merge cycle.
  - Update the documentation deliverables in the same pull request as the corresponding code changes.
