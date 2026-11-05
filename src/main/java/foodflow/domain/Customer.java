package foodflow.domain;

import java.util.Objects;

/** A customer. BR-8 (format part): identified by an e-mail address that contains "@". */
public final class Customer {

    private final String name;
    private final String email;
    private final DeliveryAddress address;

    public Customer(String name, String email, DeliveryAddress address) {
        this.name = Texts.requireText(name, "Name");
        this.email = requireValidEmail(email);
        if (address == null) {
            throw new IllegalArgumentException("A delivery address is required.");
        }
        this.address = address;
    }

    private static String requireValidEmail(String email) {
        String trimmed = Texts.requireText(email, "E-mail").toLowerCase(java.util.Locale.ROOT);
        int at = trimmed.indexOf('@');
        boolean valid = at > 0
                && at == trimmed.lastIndexOf('@')
                && at < trimmed.length() - 1
                && trimmed.chars().noneMatch(Character::isWhitespace);
        if (!valid) {
            throw new IllegalArgumentException("'" + email + "' is not a valid e-mail address (it must contain '@').");
        }
        return trimmed;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public DeliveryAddress address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Customer other && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

