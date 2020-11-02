package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.engine.process.report.interleaving.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class InterleavingExecutionReporterTest {
    
    static Stream<Arguments> reporters() {
        return Stream.of(
                Arguments.of(new LoggingInterleavingExecutionReporter()),
                Arguments.of(new LoggingInterleavingExecutionReporter()),
                Arguments.of(new LoggingInterleavingExecutionReporterForGeneration()),
                Arguments.of(new LoggingInterleavingExecutionReporterForGenerationJava()),
                Arguments.of(new EmptyInterleavingExecutionReporter())
        );
    }

    @ParameterizedTest
    @MethodSource("reporters")
    void doesNotThrowAnExceptionIfPassedNull(InterleavingExecutionReporter executionReporter) {
        executionReporter.interleavingGroupGenerated(null);
        executionReporter.interleavingGroupFinished(null, null, null);
        executionReporter.identificationStarted(null, null);
        executionReporter.identificationFinished(null, null, null);
        executionReporter.identificationTestInputGenerated(null, null);
        executionReporter.checkingStarted(null,null);
        executionReporter.checkingFinished(null,null, true);
        executionReporter.report(null, null);
    }
    
}
