package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static de.rwth.swc.coffee4j.algorithmic.report.Report.report;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DelegatingExecutionReporterTest {
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new DelegatingSequentialExecutionReporter(null));
        assertThrows(IllegalArgumentException.class, () -> new DelegatingSequentialExecutionReporter(Collections.singleton(null)));
        assertThrows(NullPointerException.class, () -> new DelegatingInterleavingExecutionReporter(null));
        assertThrows(IllegalArgumentException.class, () -> new DelegatingInterleavingExecutionReporter(Collections.singleton(null)));
    }
    
    @Test
    void reportLevelIsAlwaysLowestLevelOfChildReportersSequential() {
        final SequentialExecutionReporter first = Mockito.mock(SequentialExecutionReporter.class);
        final SequentialExecutionReporter second = Mockito.mock(SequentialExecutionReporter.class);

        final DelegatingSequentialExecutionReporter reporter = new DelegatingSequentialExecutionReporter(Arrays.asList(first, second));

        testReportLevel(first, second, reporter);
    }

    @Test
    void reportLevelIsAlwaysLowestLevelOfChildReportersInterleaving() {
        final InterleavingExecutionReporter first = Mockito.mock(InterleavingExecutionReporter.class);
        final InterleavingExecutionReporter second = Mockito.mock(InterleavingExecutionReporter.class);

        final DelegatingInterleavingExecutionReporter reporter = new DelegatingInterleavingExecutionReporter(Arrays.asList(first, second));

        testReportLevel(first, second, reporter);
    }

    private void testReportLevel(ExecutionReporter first, ExecutionReporter second, ExecutionReporter reporter) {
        when(first.getReportLevel()).thenReturn(ReportLevel.TRACE);
        when(second.getReportLevel()).thenReturn(ReportLevel.DEBUG);
        assertEquals(ReportLevel.TRACE, reporter.getReportLevel());

        when(first.getReportLevel()).thenReturn(ReportLevel.FATAL);
        when(second.getReportLevel()).thenReturn(ReportLevel.INFO);
        assertEquals(ReportLevel.INFO, reporter.getReportLevel());
    }

    @Test
    void testDelegationOfLifecycleMethodsInterleaving() {
        final InterleavingCombinatorialTestGroup group = Mockito.mock(InterleavingCombinatorialTestGroup.class);
        final Map<Combination, Class<? extends Throwable>> classifiedExceptionInducingCombinations = new HashMap<>();
        final Set<Combination> exceptionInducingCombinations = new HashSet<>();
        final Set<Combination> possiblyFailureInducingCombinations = new HashSet<>();
        final Combination failingTestInput = Combination.empty();
        final TestResult testResult = TestResult.failure(new ErrorConstraintException());

        final InterleavingExecutionReporter first = Mockito.mock(InterleavingExecutionReporter.class);
        final InterleavingExecutionReporter second = Mockito.mock(InterleavingExecutionReporter.class);
        final DelegatingInterleavingExecutionReporter reporter = new DelegatingInterleavingExecutionReporter(Arrays.asList(first, second));
        
        reporter.interleavingGroupGenerated(group);
        verify(first, times(1)).interleavingGroupGenerated(group);
        verify(second, times(1)).interleavingGroupGenerated(group);
        
        reporter.interleavingGroupFinished(group, classifiedExceptionInducingCombinations, possiblyFailureInducingCombinations);
        verify(first, times(1)).interleavingGroupFinished(group, classifiedExceptionInducingCombinations, possiblyFailureInducingCombinations);
        verify(second, times(1)).interleavingGroupFinished(group, classifiedExceptionInducingCombinations, possiblyFailureInducingCombinations);
        
        reporter.identificationStarted(group, failingTestInput);
        verify(first, times(1)).identificationStarted(group, failingTestInput);
        verify(second, times(1)).identificationStarted(group, failingTestInput);
        
        reporter.identificationFinished(group, exceptionInducingCombinations, possiblyFailureInducingCombinations);
        verify(first, times(1)).identificationFinished(group, exceptionInducingCombinations, possiblyFailureInducingCombinations);
        verify(second, times(1)).identificationFinished(group, exceptionInducingCombinations, possiblyFailureInducingCombinations);
        
        reporter.identificationTestInputGenerated(group, Combination.empty());
        verify(first, times(1)).identificationTestInputGenerated(group, Combination.empty());
        verify(second, times(1)).identificationTestInputGenerated(group, Combination.empty());

        reporter.checkingStarted(group, Combination.empty());
        verify(first, times(1)).checkingStarted(group, Combination.empty());
        verify(second, times(1)).checkingStarted(group, Combination.empty());

        reporter.checkingFinished(group, Combination.empty(), true);
        verify(first, times(1)).checkingFinished(group, Combination.empty(), true);
        verify(second, times(1)).checkingFinished(group, Combination.empty(), true);
        
        reporter.testInputExecutionStarted(failingTestInput);
        verify(first, times(1)).testInputExecutionStarted(failingTestInput);
        verify(second, times(1)).testInputExecutionStarted(failingTestInput);
        
        reporter.testInputExecutionFinished(failingTestInput, testResult);
        verify(first, times(1)).testInputExecutionFinished(failingTestInput, testResult);
        verify(second, times(1)).testInputExecutionFinished(failingTestInput, testResult);
        
        verifyNoMoreInteractions(first);
        verifyNoMoreInteractions(second);
    }

    @Test
    void testDelegationOfLifecycleMethodsSequential() {
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        final TestInputGroupContext context = new TestInputGroupContext("test", generator);
        final Combination testInput = Combination.empty();
        final List<Combination> testInputs = Collections.singletonList(testInput);
        final FaultCharacterizationAlgorithm algorithm = Mockito.mock(FaultCharacterizationAlgorithm.class);
        final TestResult testResult = TestResult.failure(new IllegalArgumentException("test"));

        final SequentialExecutionReporter first = Mockito.mock(SequentialExecutionReporter.class);
        final SequentialExecutionReporter second = Mockito.mock(SequentialExecutionReporter.class);
        final DelegatingSequentialExecutionReporter reporter = new DelegatingSequentialExecutionReporter(Arrays.asList(first, second));

        reporter.testInputGroupGenerated(context, testInputs);
        verify(first, times(1)).testInputGroupGenerated(context, testInputs);
        verify(second, times(1)).testInputGroupGenerated(context, testInputs);

        reporter.testInputGroupFinished(context);
        verify(first, times(1)).testInputGroupFinished(context);
        verify(second, times(1)).testInputGroupFinished(context);

        reporter.faultCharacterizationStarted(context, algorithm);
        verify(first, times(1)).faultCharacterizationStarted(context, algorithm);
        verify(second, times(1)).faultCharacterizationStarted(context, algorithm);

        reporter.faultCharacterizationFinished(context, new HashMap<>(), new HashSet<>(testInputs));
        verify(first, times(1)).faultCharacterizationFinished(context, new HashMap<>(), new HashSet<>(testInputs));
        verify(second, times(1)).faultCharacterizationFinished(context, new HashMap<>(), new HashSet<>(testInputs));

        reporter.faultCharacterizationTestInputsGenerated(context, testInputs);
        verify(first, times(1)).faultCharacterizationTestInputsGenerated(context, testInputs);
        verify(second, times(1)).faultCharacterizationTestInputsGenerated(context, testInputs);

        reporter.testInputExecutionStarted(testInput);
        verify(first, times(1)).testInputExecutionStarted(testInput);
        verify(second, times(1)).testInputExecutionStarted(testInput);

        reporter.testInputExecutionFinished(testInput, testResult);
        verify(first, times(1)).testInputExecutionFinished(testInput, testResult);
        verify(second, times(1)).testInputExecutionFinished(testInput, testResult);

        verifyNoMoreInteractions(first);
        verifyNoMoreInteractions(second);
    }
    
    @Test
    void reportsOnlyDelegatedWhenLevelHighEnoughSequential() {
        final SequentialExecutionReporter first = Mockito.mock(SequentialExecutionReporter.class);
        final SequentialExecutionReporter second = Mockito.mock(SequentialExecutionReporter.class);
        final DelegatingSequentialExecutionReporter reporter = new DelegatingSequentialExecutionReporter(Arrays.asList(first, second));

        run(first, second, reporter);
    }

    @Test
    void reportsOnlyDelegatedWhenLevelHighEnoughInterleaving() {
        final InterleavingExecutionReporter first = Mockito.mock(InterleavingExecutionReporter.class);
        final InterleavingExecutionReporter second = Mockito.mock(InterleavingExecutionReporter.class);
        final DelegatingInterleavingExecutionReporter reporter = new DelegatingInterleavingExecutionReporter(Arrays.asList(first, second));

        run(first, second, reporter);
    }

    private void run(ExecutionReporter first, ExecutionReporter second, ExecutionReporter reporter) {
        when(first.getReportLevel()).thenReturn(ReportLevel.FATAL);
        when(second.getReportLevel()).thenReturn(ReportLevel.INFO);

        final Report report = report("test");
        reporter.report(ReportLevel.DEBUG, report);
        verify(first, never()).report(any(), any());
        verify(second, never()).report(any(), any());

        reporter.report(ReportLevel.INFO, report);
        verify(second, times(1)).report(ReportLevel.INFO, report);
        verify(first, never()).report(any(), any());

        reporter.report(ReportLevel.FATAL, report);
        verify(first, times(1)).report(ReportLevel.FATAL, report);
        verify(second, times(1)).report(ReportLevel.FATAL, report);
    }

}
