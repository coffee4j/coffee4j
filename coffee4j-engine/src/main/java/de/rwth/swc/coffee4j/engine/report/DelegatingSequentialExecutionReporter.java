package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.platform.commons.function.Try;

import java.util.*;

public class DelegatingSequentialExecutionReporter implements SequentialExecutionReporter {
    
    private final Set<SequentialExecutionReporter> executionReporters;
    
    public DelegatingSequentialExecutionReporter(Collection<SequentialExecutionReporter> executionReporters) {
        Preconditions.notNull(executionReporters);
        Preconditions.check(Try.call(() -> !executionReporters.contains(null)).toOptional().orElse(true));

        this.executionReporters = new HashSet<>();
        this.executionReporters.addAll(executionReporters);
    }
    
    @Override
    public ReportLevel getReportLevel() {
        ReportLevel leastWorstLevel = ReportLevel.FATAL;
        
        for (ExecutionReporter executionReporter : executionReporters) {
            if (!executionReporter.getReportLevel().isWorseThanOrEqualTo(leastWorstLevel)) {
                leastWorstLevel = executionReporter.getReportLevel();
            }
        }
        
        return leastWorstLevel;
    }
    
    @Override
    public void testInputGroupGenerated(TestInputGroupContext context, List<Combination> testInputs) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.testInputGroupGenerated(context, testInputs);
        }
    }
    
    @Override
    public void testInputGroupFinished(TestInputGroupContext context) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.testInputGroupFinished(context);
        }
    }
    
    @Override
    public void faultCharacterizationStarted(TestInputGroupContext context, FaultCharacterizationAlgorithm algorithm) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.faultCharacterizationStarted(context, algorithm);
        }
    }
    
    @Override
    public void faultCharacterizationFinished(TestInputGroupContext context, Map<Combination, Class<?
            extends Throwable>> exceptionInducingCombinations,
            Collection<Combination> possiblyFailureInducingCombinations) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.faultCharacterizationFinished(context, exceptionInducingCombinations, possiblyFailureInducingCombinations);
        }
    }
    
    @Override
    public void faultCharacterizationTestInputsGenerated(TestInputGroupContext context, List<Combination> testInputs) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.faultCharacterizationTestInputsGenerated(context, testInputs);
        }
    }
    
    @Override
    public void testInputExecutionStarted(Combination testInput) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.testInputExecutionStarted(testInput);
        }
    }
    
    @Override
    public void testInputExecutionFinished(Combination testInput, TestResult result) {
        for (SequentialExecutionReporter executionReporter : executionReporters) {
            executionReporter.testInputExecutionFinished(testInput, result);
        }
    }
    
    @Override
    public void report(ReportLevel level, Report report) {
        Preconditions.notNull(level);
        
        for (ExecutionReporter executionReporter : executionReporters) {
            if (level.isWorseThanOrEqualTo(executionReporter.getReportLevel())) {
                executionReporter.report(level, new Report(report));
            }
        }
    }
    
    @Override
    public void modelModified(InputParameterModel original, InputParameterModel modified) {
        for (ExecutionReporter executionReporter : executionReporters) {
            executionReporter.modelModified(original, modified);
        }
    }
}
