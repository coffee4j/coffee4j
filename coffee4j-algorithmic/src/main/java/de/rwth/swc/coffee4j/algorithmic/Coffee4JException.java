package de.rwth.swc.coffee4j.algorithmic;

/**
 * A {@link RuntimeException} that signalizes that something went wrong in the coffee4j project, either caused
 * by the user or the developer
 */
public class Coffee4JException extends RuntimeException {

    /**
     * Creates a new {@link Coffee4JException} with the supplied message
     *
     * @param message the message of the exception
     */
    public Coffee4JException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link Coffee4JException} with the supplied message and cause
     *
     * @param message the message of the exception
     * @param cause the cause of the exception
     */
    public Coffee4JException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link Coffee4JException} with the supplied format string and objects
     *
     * @param format the string to format
     * @param args the parameters for the string formatting
     * @see String#format(String, Object...)
     */
    public Coffee4JException(String format, Object... args) {
        this(String.format(format, args));
    }

    /**
     * Creates a new {@link Coffee4JException} with the supplied format string and objects, and a cause
     *
     * @param format the string to format
     * @param args the parameters for the string formatting
     * @param cause the cause of the exception
     * @see String#format(String, Object...)
     */
    public Coffee4JException(Throwable cause, String format, Object... args) {
        this(String.format(format, args), cause);
    }

}
