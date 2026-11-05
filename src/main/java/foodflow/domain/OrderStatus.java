package foodflow.domain;

/** Lifecycle of an order. Only PLACED exists in Iteration 1; later states arrive in Iteration 2+. */
public enum OrderStatus {
    PLACED
}

