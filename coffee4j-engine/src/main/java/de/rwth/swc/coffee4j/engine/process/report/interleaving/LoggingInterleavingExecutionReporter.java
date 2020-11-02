package de.rwth.swc.coffee4j.engine.process.report.interleaving;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.report.util.ReportUtility;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Reporter for interleaving Combinatorial Testing
 */
public class LoggingInterleavingExecutionReporter implements InterleavingExecutionReporter {
    
    private final Logger logger;

    /**
     * Creates a new reporter which uses a {@link Logger} to inform the user of output.
     */
    public LoggingInterleavingExecutionReporter() {
        this.logger = LoggerFactory.getLogger(LoggingInterleavingExecutionReporter.class);
    }

    @Override
    public void interleavingGroupGenerated(InterleavingCombinatorialTestGroup group) {
        logger.info("Generated {}", group);
    }

    @Override
    public void interleavingGroupFinished(InterleavingCombinatorialTestGroup group, Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations, Set<Combination> possiblyFailureInducingCombinations) {
        logger.info("Finished Testing with {}", group);
        logger.info("exception inducing combinations: {}", exceptionInducingCombinations);
        logger.info(ReportUtility.getFormattedFailureInducingCombinations(possiblyFailureInducingCombinations));
        logger.info("Possibly failure-inducing: {}", possiblyFailureInducingCombinations);
    }

    @Override
    public void identificationStarted(InterleavingCombinatorialTestGroup group, Combination failingTestInput) {
        logger.info("Identification-process started for test input {}", failingTestInput);
    }

    @Override
    public void identificationFinished(InterleavingCombinatorialTestGroup group, Set<Combination> exceptionInducingCombinations, Set<Combination> possiblyFailureInducingCombinations) {
        logger.info("Identification-process finished.");
        logger.info("exception inducing combinations: {}", exceptionInducingCombinations);
        logger.info(ReportUtility.getFormattedFailureInducingCombinations(possiblyFailureInducingCombinations));
    }

    @Override
    public void identificationTestInputGenerated(InterleavingCombinatorialTestGroup group, Combination testInput) {
        logger.info("Following test input was generated during identification: {}", testInput);
    }

    @Override
    public void checkingStarted(InterleavingCombinatorialTestGroup group, Combination combinationToCheck) {
        logger.info("Checking Phase started for Combination {}", combinationToCheck);
    }

    @Override
    public void checkingFinished(InterleavingCombinatorialTestGroup group, Combination combinationToCheck, boolean passed) {
        logger.info("Checking Phase finished for Combination {}", combinationToCheck);

        if (passed) {
            logger.info("Checking passed.");
        } else {
            logger.info("Checking failed.");
        }
    }

    @Override
    public void testInputExecutionStarted(Combination testInput) {
        logger.info("Test Input Execution started for Combination {}", testInput);
    }

    @Override
    public void testInputExecutionFinished(Combination testInput, TestResult result) {
        logger.info("Test Input Execution finished for Combination {} with result {}", testInput, result);
    }

    @Override
    public void report(ReportLevel level, Report report) {
        logger.info("Report with level {}: {}", level, report);
    }
    
}
