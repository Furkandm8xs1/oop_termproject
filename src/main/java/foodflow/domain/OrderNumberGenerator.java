package foodflow.domain;

/** Hands out unique, increasing order numbers (BR-6, number part). One instance per application, no static state. */
public final class OrderNumberGenerator {

    public static final int FIRST_NUMBER = 1001;

    private int next;

    public OrderNumberGenerator() {
        this(FIRST_NUMBER);
    }

    public OrderNumberGenerator(int firstNumber) {
        this.next = firstNumber;
    }

    public int next() {
        return next++;
    }
}

