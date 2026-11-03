CSE3063 · FoodFlow term project 

| CSE3063 · FOODFLOW TERM PROJECT · ITERATION 1 OF 4  Iteration 1 — Inception & Walking Skeleton |
| :---- |

| Goal  | Understand the product, write down its requirements and domain, and deliver a thin,  working, end-to-end slice: a customer can browse restaurants and menus, fill a cart and  place a cash-on-delivery order from a console application. |
| :---- | :---- |
| **Deadline**  | Tag iteration-1 on main and upload the report and github zip by date given in google  classroom |
| **Course topics used**  | (classes, encapsulation, relationships), (UP, iterations), (use cases, FURPS+), (domain model),  (SSDs, operation contracts). |

**1\. Timeline and checkpoints** 

| Week  | Team milestone  | Checked in class |
| :---- | :---- | :---- |
| 3  | Repository created; roles assigned; vision draft;  iteration plan with owners. | Assistant checks repository access and the plan. |
| 4  | Use-case diagram, brief use cases, first fully dressed use  case, supplementary specification draft. | Peer review of *Place Order* with the use-case  checklist. |
| 5  | Domain model draft; glossary ≥ 15 terms; first domain  classes with tests. | Domain-model review in class (common mistakes). |
| 6  | SSD and operation contracts; feature-complete console  application; report; tag iteration-1. | — |
| 7  | Demo (10 minutes) and Q\&A.  | Demo with your assistant. |

**2\. Scope**

| ID  | Feature  | Details  | Priority |
| :---- | :---- | ----- | :---- |
| F1  | Identify the customer  | Register with name, e-mail and delivery address (district  \+ street), or continue as an existing customer by e-mail.  In-memory only. | Must |
| F2  | Browse restaurants  | List all restaurants with cuisine, district and delivery fee.  | Must |
| F3  | Filter restaurants  | Filter by cuisine and/or district; an empty result is  reported politely. | Should |
| F4  | View a menu  | Show a restaurant’s items grouped by category, with  prices. | Must |
| F5  | Manage the cart  | Add an item with a quantity, change a quantity, remove  an item, view the cart with line totals, subtotal, delivery  fee and total. | Must |
| F6  | Place an order  | Cash on delivery to the customer’s address; the order  gets a number and the status PLACED; a receipt is  printed; the cart is emptied. | Must |
| F7  | My orders  | List the orders the current customer placed in this  session. | Could |

CSE3063 · FoodFlow · Iteration 1 brief · Fall 2026–27 Page 1 of 5   
CSE3063 · FoodFlow term project 

**Out of scope in I1** (they arrive later — do not build them now, but do not make them impossible either):  restaurant owners and couriers, order status changes after PLACED, opening hours and minimum basket,  payments other than cash, promotions, saving data between runs, any graphical interface. 

**3\. Business rules** 

| ID  | Rule |
| :---- | :---- |
| BR-1  | Every menu item has a positive price. A cart line has a quantity between 1 and 20\. |
| BR-2  | A cart contains items from **one restaurant only**. Adding an item from another restaurant is refused; the customer  may empty the cart and continue. |
| BR-3  | Adding an item that is already in the cart increases its quantity; there are never two lines for the same item. |
| BR-4  | An order can be placed only with a non-empty cart and a delivery address. |
| BR-5  | Order total \= subtotal (sum of line totals) \+ the restaurant’s delivery fee. All amounts are shown with two decimals  and “TL”. |
| BR-6  | Every order receives a unique, increasing order number; its initial status is PLACED. |
| BR-7  | Placing an order empties the cart. Later changes to a menu (in I2) must not change the total of an order already  placed. |
| BR-8  | A customer is identified by a unique e-mail address that contains “@”. |
| BR-9  | Invalid input never terminates the program: the user sees a clear message and can try again. |

**Watch BR-7** 

BR-7 is a design question in disguise: should an Order refer to the live MenuItem objects, or record the name  and price at the moment of ordering? Decide, and explain your decision in the report. It will matter in  Iteration 2\. 

**4\. Starter data** 

data/restaurants.json contains 6 restaurants and 35 menu items. Load it at start-up (Gson is already in the  pom.xml); do not hard-code the data in Java. The file uses UTF-8 and Turkish characters.

| data/restaurants.json (excerpt)  {   "restaurants": \[   {   "id": "R01",   "name": "Kebapçı Mehmet",   "cuisine": "Turkish",   "district": "Kadıköy",   "deliveryFee": 25.0,   "minimumBasket": 150.0,   "menu": \[   { "id": "R01-01", "name": "Lahmacun", "category": "Main", "price": 140.0 },  { "id": "R01-05", "name": "Ayran", "category": "Drink", "price": 40.0 },  ...   \]   },   ...   \]  } |
| :---- |

CSE3063 · FoodFlow · Iteration 1 brief · Fall 2026–27 Page 2 of 5   
CSE3063 · FoodFlow term project 

minimumBasket is already in the data, but its rule belongs to Iteration 2\. Your loader may read it now. 

**5\. Required deliverables** 

| \#  | Deliverable  | Expectations |
| :---- | :---- | :---- |
| D1  | Vision and scope  | At most one page (docs/vision.md): problem statement, stakeholders and goals, key  features of the whole product, I1 scope, assumptions. |
| D2  | Glossary  | At least 15 terms with definitions, rule references (BR-x) and aliases. The same words  appear in use cases, the domain model and the code. |
| D3  | Use-case model  | A UML use-case diagram for the **whole product** (all four actors), with I1 use cases marked.  Brief format for 6–8 use cases. **Fully dressed** format for *Place Order* and *Manage Cart*,  with extensions for every business rule that can fail. |
| D4  | Supplementary specification  | FURPS+ table with at least one *checkable* requirement per relevant category. |
| D5  | Domain model  | A conceptual UML class diagram: concepts, attributes and associations with multiplicities  and names. No methods, no Java types, no UI or file classes. |
| D6  | SSD and contracts  | A system sequence diagram for the main success scenario of *Place Order*, and operation  contracts (pre- and post-conditions) for two of its system operations, e.g.  addItem(itemId, quantity) and placeOrder(). |
| D7  | Working software  | Maven project from the starter; console UI covering F1–F6 (F7 optional); data loaded  from JSON; packages domain, application, infrastructure, ui; at least **10 meaningful  JUnit tests** of domain rules; README with build and run instructions; a sample run  transcript in docs/sample-run.txt. |
| D8  | Iteration report  | As defined in the Project Handbook (≤ 8 pages), including design rationale for 3–6  decisions (BR-7 among them), the contribution table and the AI usage log. |
| D9  | Plan for Iteration 2  | Top-5 risk list with mitigations, and a first plan for I2 in docs/iteration-plan.md. |

**6\. Design guidance** 

There is no single right design, but there are wrong ones. These guidelines are part of the grading. 

• **Keep the domain (Restaurant, MenuItem, Cart, CartLine, Order, Customer, …) free of input and output:  domain classes never call System.out or read from System.in.** 

• Put the use-case logic in an **application** class (for example OrderingService) that the console UI calls. One  public method per system operation from your SSD is a good start. 

• The **infrastructure** layer loads restaurants.json and turns it into domain objects; nothing else knows that  JSON exists. 

• The **UI** reads commands, calls the application layer and prints results. It contains no business rules. • Every rule BR-1 … BR-8 lives in **one** class. Write down which one in your glossary or report. • Use exceptions (for example IllegalArgumentException, IllegalStateException, or your own) for rule  violations; catch them in the UI and print a friendly message (BR-9).

| Suggested package structure (you may differ — explain why)  edu.marmara.cse3063.foodflow  ├─ App creates and connects the objects, starts the UI ├─ domain Restaurant, MenuItem, Cart, CartLine, Order, Customer, ...  ├─ application OrderingService (one method per system operation)  ├─ infrastructure JsonRestaurantLoader  └─ ui ConsoleUI |
| :---- |

CSE3063 · FoodFlow · Iteration 1 brief · Fall 2026–27 Page 3 of 5   
CSE3063 · FoodFlow term project 

**7\. Testing** 

Write at least 10 JUnit tests that check rules, not getters. Examples of good tests: 

• Adding the same item twice gives one line with the summed quantity (BR-3). 

• Adding an item from a second restaurant is refused and leaves the cart unchanged (BR-2). • Quantities 0 and 21 are rejected (BR-1). 

• The order total equals subtotal plus the restaurant’s delivery fee (BR-5). 

• Two orders get different, increasing numbers (BR-6); placing an order empties the cart (BR-7). • An invalid e-mail address is rejected; a duplicate e-mail is rejected (BR-8). 

• The loader reads a small test JSON file and creates the expected restaurants and items. 

**8\. Demo (Week 7, 10 minutes)** 

1\. Show the use-case diagram and the domain model (1 minute each). 

2\. Run mvn \-q test and show that all tests pass. 

3\. Run the application: register a customer → list restaurants → filter by cuisine → open a menu → add three  items, one of them twice → try to add an item from another restaurant → change a quantity → view the  cart → place the order → show the receipt. 

4\. Q\&A: your assistant asks individual members about models, design decisions and code. 

| An illustrative console session — your commands and layout may differ  FoodFlow \> restaurants \--cuisine Turkish   R01 Kebapçı Mehmet Turkish Kadıköy delivery 25.00 TL   R05 Dönerci Usta Turkish Üsküdar delivery 20.00 TL  FoodFlow \> add R01-01 2   Added 2 x Lahmacun. Cart subtotal: 280.00 TL  FoodFlow \> add R02-01 1   Your cart has items from Kebapçı Mehmet. Empty it first (cart clear).  FoodFlow \> order   Order \#1001 placed: 305.00 TL (incl. delivery 25.00 TL), cash on delivery. |
| :---- |

**9\. Grading checklist (8% of the course)** 

| Criterion  | Weight  | What we check in Iteration 1 |
| :---- | :---- | :---- |
| **Requirements & analysis**  | 20%  | D1–D6 complete and consistent: every BR appears in the use cases (as steps or  extensions); the domain model uses glossary terms and correct multiplicities; the SSD  matches the use case; contracts state real post-conditions (objects created, associations  formed, attributes changed). |
| **Design quality**  | 30%  | Layers respected; rules live in the right domain class; no public fields, no logic in the UI, no  god class; constructors validate; BR-7 decision explained; diagrams match the code. |
| **Working software**  | 25%  | Builds from the README; F1–F6 work as in the demo script; invalid input handled (BR-9);  data comes from the JSON file. |
| **Testing**  | 15%  | ≥ 10 meaningful domain tests covering BR-1…BR-8; tests pass with mvn test. |
| **Process & teamwork**  | 10%  | Plan and board used; every member has merged pull requests; reviews visible;  retrospective and risk list written; AI usage log present. |

**Common Iteration 1 mistakes** 

• A domain model that is really a design class diagram (methods, List\<...\>, ConsoleUI).

CSE3063 · FoodFlow · Iteration 1 brief · Fall 2026–27 Page 4 of 5   
CSE3063 · FoodFlow term project 

• Use cases that describe screens and buttons instead of goals and intentions. • A Main class with 600 lines and every rule inside a switch statement. • Static lists of restaurants and orders shared by the whole program. • Tests that only call getters.

CSE3063 · FoodFlow · Iteration 1 brief · Fall 2026–27 Page 5 of 5 