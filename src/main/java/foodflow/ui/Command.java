package foodflow.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * One line typed by the user, split into a command name, positional arguments and
 * pairs. Example: {@code restaurants --cuisine Turkish} or {@code add R01-01 2}.
 * It checks the shape of the input and gives friendly error messages.
 */
public final class Command {

    private final String name;
    private final List<String> arguments;
    private final Map<String, String> options;

    private Command(String name, List<String> arguments, Map<String, String> options) {
        this.name = name;
        this.arguments = List.copyOf(arguments);
        this.options = Map.copyOf(options);
    }

    /** Parses a line. Option values run until the next option, so {@code --district Kadıköy} works. */
    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new InvalidInputException("Please type a command. Type 'help' to see all commands.");
        }
        String[] tokens = line.trim().split("\\s+");
        String name = tokens[0].toLowerCase(Locale.ROOT);
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new LinkedHashMap<>();
        int i = 1;
        while (i < tokens.length) {
            String token = tokens[i];
            if (token.startsWith("--")) {
                String option = token.substring(2).toLowerCase(Locale.ROOT);
                if (option.isEmpty()) {
                    throw new InvalidInputException("'--' must be followed by an option name, e.g. --cuisine.");
                }
                StringBuilder value = new StringBuilder();
                i++;
                while (i < tokens.length && !tokens[i].startsWith("--")) {
                    value.append(value.length() == 0 ? "" : " ").append(tokens[i]);
                    i++;
                }
                if (value.length() == 0) {
                    throw new InvalidInputException("Option --" + option + " needs a value.");
                }
                options.put(option, value.toString());
            } else {
                arguments.add(token);
                i++;
            }
        }
        return new Command(name, arguments, options);
    }

    public String name() {
        return name;
    }

    public List<String> arguments() {
        return arguments;
    }

    /** The positional argument at {@code index}, or a friendly error naming what is missing. */
    public String argument(int index, String label) {
        if (index >= arguments.size()) {
            throw new InvalidInputException("Missing " + label + ". Type 'help' to see how to use '" + name + "'.");
        }
        return arguments.get(index);
    }

    /** A whole-number argument, text such as "two" gives a friendly error instead of a crash */
    public int intArgument(int index, String label) {
        String text = argument(index, label);
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + text + "' is not a whole number for " + label + ".");
        }
    }

    public Optional<String> option(String option) {
        return Optional.ofNullable(options.get(option.toLowerCase(Locale.ROOT)));
    }

    /** Rejects options the command does not know, so typos like --cusine are not silently ignored. */
    public void allowOnlyOptions(String... allowed) {
        List<String> known = List.of(allowed);
        for (String option : options.keySet()) {
            if (!known.contains(option)) {
                throw new InvalidInputException("Unknown option --" + option + " for '" + name + "'.");
            }
        }
    }
}
