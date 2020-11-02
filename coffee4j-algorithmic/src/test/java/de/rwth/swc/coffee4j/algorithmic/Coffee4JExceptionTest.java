package de.rwth.swc.coffee4j.algorithmic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Coffee4JExceptionTest {

    @Test
    void createsExceptions() {
        final String message = "Die Nachricht";
        final Throwable cause = new Throwable("Eine andere Nachricht");
        final String format = "%s etwas %s";
        final Object[] args = new Object[] {"Das", "ist"};
        final String formattedString = String.format(format, args);

        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(() -> {throw new Coffee4JException(message);})
                .withMessage(message);

        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(() -> {throw new Coffee4JException(message, cause);})
                .withMessage(message)
                .withCause(cause);

        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(() -> {throw new Coffee4JException(format, args);})
                .withMessage(formattedString);

        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(() -> {throw new Coffee4JException(cause, format, args);})
                .withMessage(formattedString)
                .withCause(cause);
    }

}
