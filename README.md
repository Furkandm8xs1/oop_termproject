# FoodFlow

FoodFlow is a Java 21 console food-ordering application built for the CSE3063 term project (Iteration 1, Team 11). A customer can browse restaurants and menus, filter restaurants by cuisine or district, manage a shopping cart and place a cash-on-delivery order.

Restaurant and menu data is read from the UTF-8 file [`data/restaurants.json`](data/restaurants.json). Customers and orders are kept in memory only and are lost when the application exits.

## Features

- Register a customer (name, email, delivery address) and log in by email.
- List restaurants, filtered by cuisine and/or district.
- Show a restaurant's menu.
- Add items to a cart, change quantities, remove items or clear the cart.
- Place a cash-on-delivery order and receive a receipt with an order number.
- List the orders placed by the logged-in customer in the current session.
- Friendly error messages for invalid input (unknown commands, missing arguments, unknown options, non-numeric quantities).

## Requirements

- Java 21
- Maven 3.9 or newer
- Docker and Docker Compose v2 (optional, only to run in a container)

## Build and test

Run all JUnit tests from the project root:

```bash
mvn test
```

Maven prints `BUILD SUCCESS` when all tests pass. Test sources are in `src/test/java` and test data is in `src/test/resources`. The same tests run on every push through GitHub Actions (`.github/workflows/ci.yml`).

## Run

Package the application (this also runs the tests) and start the executable JAR:

```bash
mvn package
java -jar target/foodflow.jar
```

By default the restaurant data is loaded from `data/restaurants.json`. To use another file, pass its path as an argument:

```bash
java -jar target/foodflow.jar /path/to/restaurants.json
```

### Run with Docker (optional)

```bash
docker compose build
docker compose run --rm foodflow
```

The image build compiles the sources, runs the tests and produces the JAR. FoodFlow waits for keyboard input, so use `docker compose run --rm foodflow` rather than `docker compose up`. To run only the tests in Docker: `docker compose --profile test run --rm test`.

## Console commands

Type `help` after starting the application to list all commands. Example session:

```text
register --name Ali Yılmaz --email ali@example.com --district Kadıköy --street Moda Caddesi 5
restaurants --cuisine Turkish --district Kadıköy
menu R01
add R01-01 2
cart
order
orders
exit
```

| Command | Description |
| --- | --- |
| `restaurants [--cuisine CUISINE] [--district DISTRICT]` | Lists restaurants, optionally filtered. |
| `menu RESTAURANT_ID` | Shows the menu of a restaurant. |
| `add ITEM_ID QUANTITY` | Adds an item to the cart. |
| `cart` | Shows the cart lines and totals. |
| `qty ITEM_ID QUANTITY` | Changes the quantity of a cart item. |
| `remove ITEM_ID` | Removes an item from the cart. |
| `clear` | Empties the cart. |
| `register --name NAME --email EMAIL --district DISTRICT --street STREET` | Creates a customer and logs in. |
| `login EMAIL` | Logs in as an existing customer. |
| `order` | Places a cash-on-delivery order for the cart and prints the receipt. |
| `orders` | Lists the logged-in customer's orders in this session. |
| `help`, `exit` | Shows help or quits. |

## Business rules (summary)

- A cart may contain items from one restaurant only.
- Item quantity must be between 1 and 20; adding an item that is already in the cart increases its quantity.
- Placing an order requires a logged-in customer and a non-empty cart.
- The order total is the subtotal plus the restaurant's delivery fee (money is handled with `BigDecimal`).
- When an order is placed, an order number is generated and the cart is emptied.

The full list of business rules (BR ids) is in [`docs/glossary.md`](docs/glossary.md) and [`docs/use-cases.md`](docs/use-cases.md).

## Architecture

The code is split into layers; dependencies point inwards towards the domain.

```text
src/main/java/foodflow/
├── App.java              # Wires the objects, loads the data and starts the UI
├── application/          # OrderingService (use-case logic) and CartSummary
├── domain/               # Restaurant, MenuItem, Customer, Cart, Order and the business rules
├── infrastructure/       # JsonRestaurantLoader: reads the JSON file into domain objects
└── ui/                   # ConsoleUI, Command parser and ConsoleFormatter
```

- **ui**: `Command` parses and validates a typed line; `ConsoleUI` dispatches it; `ConsoleFormatter` formats output.
- **application**: `OrderingService` coordinates customers, catalog, cart and orders.
- **domain**: holds the rules (quantity limits, single-restaurant cart, totals, order status).
- **infrastructure**: loads restaurant data and reports problems with `DataLoadException`.

## Repository layout

```text
src/main/java/          # Application source
src/test/java/          # JUnit tests
src/test/resources/     # JSON data used by tests
data/restaurants.json   # Restaurant and menu data
docs/                   # Vision, glossary, use cases, supplementary spec (FURPS+),
│                       # iteration plan, diagrams (PlantUML) and AI usage log
pom.xml                 # Maven build
Dockerfile, docker-compose.yml   # Optional container setup
```

## Documentation

- [Vision](docs/vision.md)
- [Glossary](docs/glossary.md)
- [Use cases](docs/use-cases.md)
- [Supplementary specification (FURPS+)](docs/supplementary-spec.md)
- [Iteration plan and risks](docs/iteration-plan.md)
- [AI usage log](docs/ai-usage-log.md)
- Diagrams: [`docs/diagrams/`](docs/diagrams/)
