package de.rwth.swc.coffee4j.engine.process.phase.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.swc.coffee4j.algorithmic.report.Report.report;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SequentialExecutionReporterToGenerationReporterAdapterTest {
    
    private SequentialExecutionReporter executionReporter;
    private ArgumentConverter argumentConverter;
    private ModelConverter modelConverter;
    
    private SequentialExecutionReporterToGenerationReporterAdapter reporterManager;
    
    @BeforeEach
    private void initialize() {
        executionReporter = Mockito.mock(SequentialExecutionReporter.class);
        argumentConverter = Mockito.mock(ArgumentConverter.class);
        modelConverter = Mockito.mock(ModelConverter.class);
        reporterManager = new SequentialExecutionReporterToGenerationReporterAdapter(executionReporter, argumentConverter, modelConverter);
    }
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new SequentialExecutionReporterToGenerationReporterAdapter(null, argumentConverter, modelConverter));
        assertThrows(NullPointerException.class, () -> new SequentialExecutionReporterToGenerationReporterAdapter(executionReporter, null, modelConverter));
        assertThrows(NullPointerException.class, () -> new SequentialExecutionReporterToGenerationReporterAdapter(executionReporter, argumentConverter, null));
    }
    
    @Test
    void convertsLifeCycleMethods() {
        final FaultCharacterizationAlgorithm algorithm = Mockito.mock(FaultCharacterizationAlgorithm.class);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        final TestInputGroupContext context = new TestInputGroupContext(0, generator);
        final TestInputGroup group = new TestInputGroup("0", List.of(new int[]{0}));
        final Combination combination = Combination.of(Map.of(
                Parameter.parameter("param1").values(0, 1).build(), Value.value(0, 0)));
        
        when(argumentConverter.canConvert("0")).thenReturn(true);
        when(argumentConverter.convert("0")).thenReturn(0);
        when(modelConverter.convertCombination(any(int[].class))).thenReturn(combination);
        
        reporterManager.testInputGroupGenerated(group, generator);
        verify(executionReporter, times(1)).testInputGroupGenerated(context, Collections.singletonList(combination));
        verifyNoMoreInteractions(executionReporter);
        verify(argumentConverter, times(1)).canConvert("0");
        verify(argumentConverter, times(1)).convert("0");
        verifyNoMoreInteractions(argumentConverter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{0}));
        verifyNoMoreInteractions(modelConverter);
        
        reporterManager.faultCharacterizationStarted(group, algorithm);
        verify(executionReporter, times(1)).faultCharacterizationStarted(context, algorithm);
        verifyNoMoreInteractions(executionReporter);
        
        reporterManager.faultCharacterizationTestInputsGenerated(group, Collections.singletonList(new int[]{1}));
        verify(executionReporter, times(1)).faultCharacterizationTestInputsGenerated(context, Collections.singletonList(combination));
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{1}));
        verifyNoMoreInteractions(modelConverter);
        
        reporterManager.faultCharacterizationFinished(group, new HashMap<>(), Collections.singleton(new int[]{2}));
        verify(executionReporter, times(1)).faultCharacterizationFinished(context, new HashMap<>(),
                Collections.singletonList(combination));
        verifyNoMoreInteractions(executionReporter);
        verify(modelConverter, times(1)).convertCombination(aryEq(new int[]{2}));
        verifyNoMoreInteractions(modelConverter);
        
        reporterManager.testInputGroupFinished(group);
        verify(executionReporter, times(1)).testInputGroupFinished(context);
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
