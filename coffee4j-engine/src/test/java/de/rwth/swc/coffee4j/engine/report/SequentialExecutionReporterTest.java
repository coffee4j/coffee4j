package de.rwth.swc.coffee4j.engine.report;

import org.junit.jupiter.api.Test;

class SequentialExecutionReporterTest {
    
    @Test
    void doesNotThrowAnExceptionIfPassedNull() {
        final SequentialExecutionReporter executionReporter = new SequentialExecutionReporter() {
        };
        
        executionReporter.testInputGroupGenerated(null, null);
        executionReporter.testInputGroupFinished(null);
        executionReporter.faultCharacterizationStarted(null, null);
        executionReporter.faultCharacterizationFinished(null, null, null);
        executionReporter.faultCharacterizationTestInputsGenerated(null, null);
        executionReporter.testInputExecutionStarted(null);
        executionReporter.testInputExecutionFinished(null, null);
        executionReporter.report(null, null);
    }
    
}
