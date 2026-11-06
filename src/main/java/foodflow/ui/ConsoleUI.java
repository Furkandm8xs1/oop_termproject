package foodflow.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import foodflow.application.CartSummary;
import foodflow.application.OrderingService;

/** Reads console commands and delegates application operations to {@link OrderingService}. */
public final class ConsoleUI {

    private final OrderingService service;
    private final BufferedReader in;
    private final PrintStream out;
    private final boolean echo;
    private final ConsoleFormatter formatter = new ConsoleFormatter();

    public ConsoleUI(OrderingService service, BufferedReader in, PrintStream out, boolean echo) {
        this.service = service;
        this.in = in;
        this.out = out;
        this.echo = echo;
    }

    public void run() {
        out.println("FoodFlow — type 'help' for commands.");
        while (true) {
            out.print("> ");
            out.flush();
            final String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                out.println("Could not read input: " + e.getMessage());
                return;
            }
            if (line == null) return;
            if (echo) out.println(line);
            try {
                if (!execute(Command.parse(line))) return;
            } catch (InvalidInputException | IllegalArgumentException | IllegalStateException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private boolean execute(Command command) {
        switch (command.name()) {
            case "help" -> help();
            case "restaurants" -> {
                command.allowOnlyOptions("cuisine", "district");
                out.println(formatter.restaurantList(service.listRestaurants(
                        command.option("cuisine").orElse(null), command.option("district").orElse(null))));
            }
            case "menu" -> out.println(formatter.menu(service.viewMenu(command.argument(0, "restaurant id"))));
            case "add" -> {
                var item = service.addItem(command.argument(0, "menu item id"), command.intArgument(1, "quantity"));
                out.println("Added " + item.name() + ".");
                showCart();
            }
            case "cart" -> showCart();
            case "qty" -> {
                service.changeQuantity(command.argument(0, "menu item id"), command.intArgument(1, "quantity"));
                showCart();
            }
            case "remove" -> {
                service.removeItem(command.argument(0, "menu item id"));
                showCart();
            }
            case "clear" -> { service.clearCart(); out.println("Cart cleared."); }
            case "register" -> {
                command.allowOnlyOptions("name", "email", "district", "street");
                var customer = service.register(option(command, "name"), option(command, "email"),
                        option(command, "district"), option(command, "street"));
                out.println("Registered " + customer.name() + " <" + customer.email() + ">.");
            }
            case "login" -> out.println("Logged in as " + service.login(command.argument(0, "e-mail")).name() + ".");
            case "order" -> out.println(formatter.receipt(service.placeOrder()));
            case "orders" -> out.println(formatter.orderHistory(service.myOrders()));
            case "exit", "quit" -> { out.println("Goodbye."); return false; }
            default -> throw new InvalidInputException("Unknown command '" + command.name() + "'. Type 'help'.");
        }
        return true;
    }

    private static String option(Command command, String name) {
        return command.option(name).orElseThrow(() -> new InvalidInputException("Missing --" + name + " option."));
    }

    private void showCart() {
        CartSummary cart = service.viewCart();
        if (cart.isEmpty()) {
            out.println("Your cart is empty.");
            return;
        }
        out.println("Cart from " + cart.restaurantName());
        cart.lines().forEach(line -> out.printf("  %s  %d x %s  %s%n", line.itemId(), line.quantity(),
                line.itemName(), formatter.money(line.lineTotal())));
        out.println("  Subtotal: " + formatter.money(cart.subtotal()));
        out.println("  Delivery: " + formatter.money(cart.deliveryFee()));
        out.println("  Total:    " + formatter.money(cart.total()));
    }

    private void help() {
        List<String> lines = List.of(
                "Commands:",
                "  restaurants [--cuisine NAME] [--district NAME]",
                "  menu RESTAURANT_ID | add ITEM_ID QUANTITY | cart",
                "  qty ITEM_ID QUANTITY | remove ITEM_ID | clear",
                "  register --name NAME --email EMAIL --district DISTRICT --street STREET",
                "  login EMAIL | order | orders | exit");
        lines.forEach(out::println);
    }
}
