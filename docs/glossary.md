# Glossary (D2)

The same terms are used across the use cases, domain model, operation contracts, and source code. "Code / rule home" indicates the single class responsible for enforcing the corresponding business rule.

| # | Term | Definition | Rules | Aliases | Code / rule home |
|---|---|---|---|---|---|
| 1 | **Customer** | A person who orders food through FoodFlow; identified by a unique e-mail address. | BR-8 | user, client | `Customer` |
| 2 | **E-mail address** | Unique identifier of a customer; must contain `@` with non-empty local and domain parts. | BR-8 | login, id | `Customer` (format), `CustomerRegistry` (uniqueness) |
| 3 | **Delivery address** | District and street where an order is delivered. | BR-4 | address | `DeliveryAddress` |
| 4 | **District** | City area (e.g. Kadıköy, Beşiktaş) of a restaurant or delivery address; used for filtering. | - | neighbourhood, area | `DeliveryAddress`, `Restaurant` |
| 5 | **Restaurant** | A business that prepares food; has an id, name, cuisine, district, delivery fee, minimum basket, and menu. | BR-5 | shop, vendor | `Restaurant` |
| 6 | **Restaurant catalog** | The in-memory collection of all restaurants loaded at start-up; supports lookup and filtering. | - | directory | `RestaurantCatalog` |
| 7 | **Cuisine** | Style of food a restaurant serves (e.g. Turkish, Italian, Japanese); used for filtering. | - | food type | `Restaurant` |
| 8 | **Menu** | The list of menu items a restaurant offers, grouped by category. | - | - | `Restaurant.menu()`, `Restaurant.menuByCategory()` |
| 9 | **Menu item** | A dish or drink sold by a restaurant, with a unique id, name, category, and positive price. | BR-1 | product, dish, item | `MenuItem` |
| 10 | **Category** | Logical group of menu items on a menu (e.g. Main, Soup, Dessert, Drink). | - | section | `MenuItem` |
| 11 | **Price** | Positive monetary amount in TL of a single unit of a menu item. | BR-1 | unit price | `MenuItem` |
| 12 | **Delivery fee** | Flat amount in TL a restaurant charges for delivering an order. | BR-5 | shipping fee | `Restaurant` |
| 13 | **Minimum basket** | Minimum subtotal required by a restaurant (loaded in I1, enforced in I2). | - | min order amount | `Restaurant` |
| 14 | **Cart** | The customer's current selection of items to order; contains items from at most one restaurant. | BR-2, BR-3, BR-5, BR-7 | basket | `Cart` |
| 15 | **Cart line** | One menu item paired with a quantity inside the cart; at most one line per item. | BR-1, BR-3, BR-5 | basket line | `CartLine` |
| 16 | **Quantity** | Number of units on a cart line or order line; must be between 1 and 20 inclusive. | BR-1 | count, amount | `CartLine` |
| 17 | **Line total** | Unit price multiplied by quantity for a single line. | BR-5 | item total | `CartLine`, `OrderLine` |
| 18 | **Subtotal** | Sum of all line totals in the cart or order before the delivery fee. | BR-5 | items subtotal | `Cart`, `Order` |
| 19 | **Order total** | Subtotal plus the restaurant's delivery fee, formatted with two decimals and `TL`. | BR-5 | total | `Cart`, `Order` |
| 20 | **Order** | A confirmed, immutable cash-on-delivery request created from a non-empty cart; snapshots items and prices. | BR-4, BR-5, BR-6, BR-7 | purchase | `Order` |
| 21 | **Order line** | Immutable snapshot of a cart line at the moment of ordering (item id, item name, unit price, quantity, line total). | BR-7 | order item | `OrderLine` |
| 22 | **Order number** | Unique, strictly increasing integer assigned to each placed order (starting at 1001). | BR-6 | order id | `OrderNumberGenerator` |
| 23 | **Order status** | Lifecycle state of an order; initialized to `PLACED` in Iteration 1. | BR-6 | state | `OrderStatus` |
| 24 | **Receipt** | Formatted confirmation printed when an order is placed, showing lines, totals, delivery address, and payment method. | BR-5 | bill, invoice | `ConsoleFormatter` |

---

## Where Each Business Rule Lives

| Rule | Description | Enforcing Class ("Rule Home") |
|---|---|---|
| **BR-1a** | Every menu item has a strictly positive price (`price > 0`). | `MenuItem` (constructor validation) |
| **BR-1b** | A cart line has a quantity between 1 and 20 (`1 <= quantity <= 20`). | `CartLine` (`requireValidQuantity` in constructor and `changeQuantity`) |
| **BR-2** | A cart contains items from **one restaurant only**; adding from another restaurant is refused until emptied. | `Cart` (`addItem`) |
| **BR-3** | Adding an item already in the cart increases its quantity; never two lines for the same item. | `Cart` (`addItem`) |
| **BR-4** | An order can be placed only with a non-empty cart and an identified customer with a delivery address. | `Order` (`Order.place`) and `Customer` / `DeliveryAddress` |
| **BR-5** | Order total = subtotal (sum of line totals) + restaurant's delivery fee; shown with two decimals and `TL`. | `Cart` & `CartLine` (calculation), `ConsoleFormatter` (`%.2f TL` presentation) |
| **BR-6a** | Every order receives a unique, increasing order number (1001, 1002, ...). | `OrderNumberGenerator` (`next()`) |
| **BR-6b** | Every placed order starts with initial status `PLACED`. | `Order` (constructor sets `OrderStatus.PLACED`) |
| **BR-7a** | Later changes to a menu must not change the total or items of an order already placed. | `OrderLine` (`snapshotOf` copies name, unit price, quantity, line total) |
| **BR-7b** | Placing an order empties the cart. | `Order` (`Order.place` calls `cart.clear()`) |
| **BR-8a** | Customer e-mail must contain `@` with valid local/domain parts and no whitespace. | `Customer` (`requireValidEmail`) |
| **BR-8b** | Customer e-mail is unique (case-insensitive) within the session. | `CustomerRegistry` (`register`) |
| **BR-9** | Invalid input never terminates the program: the user sees a clear message and can try again. | `Command` & `ConsoleUI` (catches `InvalidInputException`, `IllegalArgumentException`, `IllegalStateException`) |
