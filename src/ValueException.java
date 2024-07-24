/**
 * The ValueException class is a custom exception that extends the standard Exception class.
 * It is used to signal errors related to invalid or unexpected values in the application.
 */
class ValueException extends Exception {
    /**
     * Constructs a new ValueException with the specified detail message.
     *
     * @param message the detail message that describes the reason for the exception
     */
    public ValueException(String message) {
        super(message);
    }
}