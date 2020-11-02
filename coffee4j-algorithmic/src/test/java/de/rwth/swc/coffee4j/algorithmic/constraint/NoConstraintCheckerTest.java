package de.rwth.swc.coffee4j.algorithmic.constraint;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NoConstraintCheckerTest {
    @Test
    void testNoConstraintChecker() {
        ConstraintChecker checker = new NoConstraintChecker();

        assertTrue(checker.isValid(new int[]{0}));
        assertTrue(checker.isExtensionValid(new int[]{0}, 0));
        assertTrue(checker.isDualValid(new int[]{0}, new int[]{0}));
        assertDoesNotThrow(() -> checker.addConstraint(new int[]{0}));
    }
}
