package de.rwth.swc.coffee4j.algorithmic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestResultTest {
    
    @Test
    void successfulTestResults() {
        TestResult success = TestResult.success();
        assertSuccessful(success);
    }
    
    private void assertSuccessful(TestResult result) {
        assertTrue(result.isSuccessful());
        assertFalse(result.isUnsuccessful());
        assertFalse(result.getResultValue().isPresent());
    }
    
    @Test
    void unsuccessfulTestResults() {
        final Exception causeForFailure = new IllegalArgumentException();
        TestResult failure = TestResult.failure(causeForFailure);
        assertFailure(failure, causeForFailure);
    }
    
    private void assertFailure(TestResult result, Exception expectedCause) {
        assertTrue(result.isUnsuccessful());
        assertFalse(result.isSuccessful());
        assertTrue(result.getResultValue().isPresent());
        assertEquals(expectedCause, result.getResultValue().get());
    }
    
}
