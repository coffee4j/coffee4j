package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ExhaustiveConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.QuickConflictExplainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testng.Assert.assertThrows;

class ConflictDetectionConfigurationTest {

    @Test
    void testDisabledConflictDetection() {
        assertDoesNotThrow(() -> new ConflictDetectionConfiguration(
                false,
                false,
                false,
                () -> null,
                false,
                () -> null));
    }

    @Test
    void testEnableConflictExplanation() {
        assertDoesNotThrow(() -> new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                false,
                () -> null));
    }

    @Test
    void testEnableConflictDiagnostician() {
        assertDoesNotThrow(() -> new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new));
    }
    
}
