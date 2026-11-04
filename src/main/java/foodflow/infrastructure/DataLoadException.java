package foodflow.infrastructure;

/** The restaurant data file is missing, unreadable or invalid. */
public class DataLoadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLoadException(String message) {
        super(message);
    }
}