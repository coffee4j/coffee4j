package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.commons.function.Try;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Delegating Reporter for interleaving testing and generation.
 */
public class DelegatingInterleavingExecutionReporter implements InterleavingExecutionReporter {
    private final Set<InterleavingExecutionReporter> executionReporters;

    public DelegatingInterleavingExecutionReporter(Collection<InterleavingExecutionReporter> reporters) {
        Preconditions.notNull(reporters);
        Preconditions.check(Try.call(() -> !reporters.contains(null)).toOptional().orElse(true));

        executionReporters = new HashSet<>(reporters);
    }

    @Override
    public void interleavingGroupGenerated(InterleavingCombinatorialTestGroup group){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.interleavingGroupGenerated(group);
        }
    }

    @Override
    public void interleavingGroupFinished(InterleavingCombinatorialTestGroup group, Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations, Set<Combination> possibleFailureInducingCombinations){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.interleavingGroupFinished(group, exceptionInducingCombinations, possibleFailureInducingCombinations);
        }
    }

    @Override
    public void identificationStarted(InterleavingCombinatorialTestGroup group, Combination failingTestInput){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.identificationStarted(group, failingTestInput);
        }
    }

    @Override
    public void identificationFinished(InterleavingCombinatorialTestGroup group, Set<Combination> exceptionInducingCombinations, Set<Combination> failureInducingCombinations){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.identificationFinished(group, exceptionInducingCombinations, failureInducingCombinations);
        }
    }

    @Override
    public void identificationTestInputGenerated(InterleavingCombinatorialTestGroup group, Combination testInput){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.identificationTestInputGenerated(group, testInput);
        }
    }

    @Override
    public void checkingStarted(InterleavingCombinatorialTestGroup group, Combination failureInducingCombination){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.checkingStarted(group, failureInducingCombination);
        }
    }

    @Override
    public void checkingFinished(InterleavingCombinatorialTestGroup group, Combination failureInducingCombination, boolean isFailureInducing){
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.checkingFinished(group, failureInducingCombination, isFailureInducing);
        }
    }

    @Override
    public void testInputExecutionStarted(Combination testInput) {
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.testInputExecutionStarted(testInput);
        }
    }

    @Override
    public void testInputExecutionFinished(Combination testInput, TestResult result) {
        for (InterleavingExecutionReporter reporter : executionReporters) {
            reporter.testInputExecutionFinished(testInput, result);
        }
    }

    @Override
    public ReportLevel getReportLevel() {
        ReportLevel leastWorstLevel = ReportLevel.FATAL;

        for (InterleavingExecutionReporter executionReporter : executionReporters) {
            if (!executionReporter.getReportLevel().isWorseThanOrEqualTo(leastWorstLevel)) {
                leastWorstLevel = executionReporter.getReportLevel();
            }
        }

        return leastWorstLevel;
    }

    @Override
    public void report(ReportLevel level, Report report) {
        Preconditions.notNull(level);

        for (InterleavingExecutionReporter executionReporter : executionReporters) {
            if (level.isWorseThanOrEqualTo(executionReporter.getReportLevel())) {
                executionReporter.report(level, new Report(report));
            }
        }
    }
    
    @Override
    public void modelModified(InputParameterModel original, InputParameterModel modified) {
        for (InterleavingExecutionReporter executionReporter : executionReporters) {
            executionReporter.modelModified(original, modified);
        }
    }
}
