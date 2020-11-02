package de.rwth.swc.coffee4j.algorithmic;

/**
 * Exception representing an exceptional-pass internally. When an {@link ErrorConstraintException} is thrown, the
 * generating approach starts the identification exception-inducing combinations for the related test input.
 */
public class ErrorConstraintException extends RuntimeException {
    /**
     * @param errorMessage error message to store
     */
    public ErrorConstraintException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * @param error thrown error to store.
     */
    public ErrorConstraintException(Throwable error) {
        super(error);
    }

    /**
     * default constructor
     */
    public ErrorConstraintException() {
        super();
    }
}
