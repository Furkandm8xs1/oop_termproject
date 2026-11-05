# Use Cases

## Use Case 1: Browse Restaurants and Menus
**Primary Actor:** Customer
**Description:** The customer views the list of available restaurants and their menus.
**Main Success Scenario:**
1. Customer starts the application.
2. System loads and displays the list of available restaurants from the JSON file.
3. Customer selects a restaurant.
4. System displays the menu items (name, category, price) for the selected restaurant.

## Use Case 2: Manage Cart
**Primary Actor:** Customer
**Description:** The customer adds menu items to their cart.
**Main Success Scenario:**
1. Customer selects a menu item and specifies the quantity.
2. System validates that the cart only contains items from a single restaurant.
3. System adds the item to the cart.
4. System updates and displays the cart subtotal, delivery fee, and total.

## Use Case 3: Place a Cash Order
**Primary Actor:** Customer
**Description:** The customer confirms their cart and places a cash-on-delivery order.
**Main Success Scenario:**
1. Customer chooses to place an order.
2. System prompts for a delivery address.
3. Customer provides the delivery address.
4. System creates a new order with "PLACED" status and a unique order number.
5. System clears the cart.
6. System displays the order confirmation and the total amount to be paid in cash.