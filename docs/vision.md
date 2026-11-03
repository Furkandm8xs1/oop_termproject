# Vision and Scope (D1)

## Problem statement
Ordering food from several local restaurants today means phoning each one or switching between apps with different menus, fees and rules. Customers cannot easily compare restaurants, restaurants cannot reach new customers in their district, and couriers are dispatched by hand. **FoodFlow** is a single platform where customers browse restaurants and order, restaurant owners manage menus and orders, and couriers deliver them.

## Stakeholders and goals
| Stakeholder | Goal |
|---|---|
| Customer | Find a restaurant quickly, see honest prices and fees, order without surprises |
| Restaurant owner | Receive orders, keep the menu and opening hours up to date, prepare orders in order |
| Courier | Get clear delivery assignments and report delivery status |
| Administrator (platform) | Keep restaurants, users and data consistent; handle complaints |
| Course staff | See a clean, layered, tested design that can grow over four iterations |

## Key features of the whole product
- Customer registration and identification; browsing and filtering restaurants; menus; cart; ordering
- Several payment methods, promotions and minimum-basket / opening-hours rules (later)
- Restaurant owner: manage menu, accept/reject and prepare orders
- Courier: take and complete deliveries; order status tracking
- Persistent data (later)

## Iteration 1 scope (this iteration)
F1 identify customer, F2 browse restaurants, F3 filter, F4 view menu, F5 manage cart, F6 place cash-on-delivery order, F7 my orders. Console application, data from `data/restaurants.json`, in-memory only.

**Out of scope in I1:** restaurant owners and couriers, status changes after PLACED, opening hours, minimum basket, other payments, promotions, persistence between runs, any graphical interface. The design keeps these possible (e.g. `OrderStatus` is an enum, `Restaurant` already stores `minimumBasket`, `OrderLine` is a snapshot).

## Assumptions
- One customer uses one console session; all data is lost on exit.
- Restaurant data is trusted input from a UTF-8 JSON file; an invalid file stops start-up with a clear message.
- All amounts are in Turkish lira (TL) with two decimals.
- Item ids are unique across all restaurants.
- A customer has exactly one delivery address in I1.

