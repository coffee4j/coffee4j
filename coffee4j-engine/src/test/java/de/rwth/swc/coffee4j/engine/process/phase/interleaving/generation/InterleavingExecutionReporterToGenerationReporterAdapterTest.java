package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

import static de.rwth.swc.coffee4j.algorithmic.report.Report.report;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.*;

class InterleavingExecutionReporterToGenerationReporterAdapterTest {
    
    private InterleavingExecutionReporter executionReporter;
    private ArgumentConverter argumentConverter;
    private ModelConverter modelConverter;
    
    private InterleavingExecutionReporterToGenerationReporterAdapter reporterManager;
    
    @BeforeEach
    private void initialize() {
        executionReporter = Mockito.mock(InterleavingExecutionReporter.class);
        argumentConverter = Mockito.mock(ArgumentConverter.class);
        modelConverter = Mockito.mock(ModelConverter.class);
        reporterManager = new InterleavingExecutionReporterToGenerationReporterAdapter(executionReporter, argumentConverter, modelConverter);
    }
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new InterleavingExecutionReporterToGenerationReporterAdapter(null, argumentConverter, modelConverter));
        assertThrows(NullPointerException.class, () -> new InterleavingExecutionReporterToGenerationReporterAdapter(executionReporter, null, modelConverter));
        assertThrows(NullPointerException.class, () -> new InterleavingExecutionReporterToGenerationReporterAdapter(executionReporter, argumentConverter, null));
    }
    
    @Test
    void convertsLifeCycleMethods() {
        final InterleavingCombinatorialTestGroup group = Mockito.mock(InterleavingCombinatorialTestGroup.class);
        final Map<Combination, Class<? extends Throwable>> classifiedExceptionInducingCombinations = new HashMap<>();
        final Set<Combination> exceptionInducingCombinations = new HashSet<>();
        final Set<Combination> possiblyFailureInducingCombinations = new HashSet<>();
        final Combination combination = Combination.of(Map.of(
                Parameter.parameter("param1").values(0, 1).build(), Value.value(0, 0)));

        exceptionInducingCombinations.add(combination);
        possiblyFailureInducingCombinations.add(combination);
        classifiedExceptionInducingCombinations.put(combination, ErrorConstraintException.class);
        
        when(argumentConverter.canConvert("0")).thenReturn(true);
        when(argumentConverter.convert("0")).thenReturn(0);
        when(modelConverter.convertCombination(any(int[].class))).thenReturn(combination);
        
        reporterManager.interleavingGroupGenerated(group);
        verify(executionReporter, times(1)).interleavingGroupGenerated(group);
        verifyNoMoreInteractions(executionReporter);
        verifyNoMoreInteractions(argumentConverter);
        verifyNoMoreInteractions(modelConverter);
        
        reporterManager.identificationStarted(group, new int[]{0});
        verify(executionReporter, times(1)).identificationStarted(group, combination);
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{0}));
        verifyNoMoreInteractions(modelConverter);
        verifyNoMoreInteractions(argumentConverter);
        
        reporterManager.identificationTestInputGenerated(group, new int[]{1});
        verify(executionReporter, times(1)).identificationTestInputGenerated(group, combination);
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{1}));
        verifyNoMoreInteractions(modelConverter);
        verifyNoMoreInteractions(argumentConverter);
        
        reporterManager.identificationFinished(group, Collections.singleton(new int[]{2}), Collections.singleton(new int[]{2}));
        verify(executionReporter, times(1)).identificationFinished(group, exceptionInducingCombinations, possiblyFailureInducingCombinations);
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(2)).convertCombination(aryEq(new int[]{2}));
        verifyNoMoreInteractions(modelConverter);
        verifyNoMoreInteractions(argumentConverter);

        reporterManager.checkingStarted(group, new int[]{3});
        verify(executionReporter, times(1)).checkingStarted(group, combination);
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{3}));
        verifyNoMoreInteractions(modelConverter);
        verifyNoMoreInteractions(argumentConverter);

        reporterManager.checkingFinished(group, new int[]{4}, true);
        verify(executionReporter, times(1)).checkingFinished(group, combination, true);
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{4}));
        verifyNoMoreInteractions(modelConverter);
        verifyNoMoreInteractions(argumentConverter);

        Map<int[], Class<? extends Throwable>> combinations = new HashMap<>();
        combinations.put(new int[]{5}, ErrorConstraintException.class);
        
        reporterManager.interleavingGroupFinished(group, combinations, Collections.singleton(new int[]{5}));
        verify(executionReporter, times(1)).interleavingGroupFinished(group, classifiedExceptionInducingCombinations, possiblyFailureInducingCombinations);
        verifyNoMoreInteractions(executionReporter);
    }
    
    @Test
    void noReportIfLevelNotWorse() {
        when(executionReporter.getReportLevel()).thenReturn(ReportLevel.INFO);
        
        reporterManager.report(ReportLevel.DEBUG, report("test"));
        verify(executionReporter, never()).report(eq(ReportLevel.DEBUG), any());
        reporterManager.report(ReportLevel.INFO, report("test1"));
        verify(executionReporter, times(1)).report(ReportLevel.INFO, report("test1"));
        reporterManager.report(ReportLevel.ERROR, report("test2"));
        verify(executionReporter, times(1)).report(ReportLevel.ERROR, report("test2"));
        
        reporterManager.report(ReportLevel.DEBUG, () -> report("test3"));
        verify(executionReporter, never()).report(eq(ReportLevel.DEBUG), any());
        reporterManager.report(ReportLevel.INFO, () -> report("test4"));
        verify(executionReporter, times(1)).report(ReportLevel.INFO, report("test4"));
        reporterManager.report(ReportLevel.ERROR, () -> report("test5"));
        verify(executionReporter, times(1)).report(ReportLevel.ERROR, report("test5"));
    }
    
    @Test
    void argumentsAreConvertedDuringReport() {
        when(executionReporter.getReportLevel()).thenReturn(ReportLevel.INFO);
        when(argumentConverter.canConvert(any())).thenReturn(false);
        when(argumentConverter.canConvert("0")).thenReturn(true);
        when(argumentConverter.canConvert("1")).thenReturn(true);
        when(argumentConverter.convert("0")).thenReturn(0);
        when(argumentConverter.convert("1")).thenReturn(1);
        
        reporterManager.report(ReportLevel.INFO, report("test", null, "0", "2", "1"));
        final ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(executionReporter, times(1)).report(eq(ReportLevel.INFO), reportCaptor.capture());
        assertArrayEquals(new Object[]{null, 0, "2", 1}, reportCaptor.getValue().getArguments());
        reset(executionReporter);
        
        when(executionReporter.getReportLevel()).thenReturn(ReportLevel.INFO);
        reporterManager.report(ReportLevel.INFO, () -> report("test", "1", 323, null));
        verify(executionReporter, times(1)).report(eq(ReportLevel.INFO), reportCaptor.capture());
        assertArrayEquals(new Object[]{1, 323, null}, reportCaptor.getValue().getArguments());
    }
    
}
