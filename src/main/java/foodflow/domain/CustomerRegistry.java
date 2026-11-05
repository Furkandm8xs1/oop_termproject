package foodflow.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** All customers registered in this session. BR-8 (uniqueness part): e-mail addresses are unique. */
public final class CustomerRegistry {

    private final Map<String, Customer> byEmail = new LinkedHashMap<>();

    public Customer register(String name, String email, DeliveryAddress address) {
        Customer customer = new Customer(name, email, address); // validates name, e-mail format and address
        if (byEmail.containsKey(customer.email())) {
            throw new IllegalStateException("A customer with e-mail " + customer.email() + " already exists.");
        }
        byEmail.put(customer.email(), customer);
        return customer;
    }

    public Optional<Customer> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byEmail.get(email.trim().toLowerCase(java.util.Locale.ROOT)));
    }
}

