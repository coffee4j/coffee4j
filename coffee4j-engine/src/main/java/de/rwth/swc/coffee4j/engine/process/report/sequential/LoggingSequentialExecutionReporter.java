package de.rwth.swc.coffee4j.engine.process.report.sequential;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import de.rwth.swc.coffee4j.engine.process.report.util.ReportUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A {@link SequentialExecutionReporter} that reports the occurring events using a {@link Logger}
 */
public class LoggingSequentialExecutionReporter implements SequentialExecutionReporter {

    protected final Logger logger;

    /**
     * Creates a new {@link LoggingSequentialExecutionReporter} using a logger corresponding to this class
     */
    public LoggingSequentialExecutionReporter() {
        this(LoggerFactory.getLogger("[coffee4j]"));
    }

    public LoggingSequentialExecutionReporter(Logger logger) {
        Preconditions.notNull(logger);

        this.logger = logger;
    }

    @Override
    public void testInputGroupGenerated(TestInputGroupContext context, List<Combination> testInputs) {
        if (logger.isInfoEnabled()) {
            logger.info("Generated test input group \"{}\".", identifierAsString(context));
        }

        if (testInputs != null) {
            for (Combination combination : testInputs) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.valueOf(combination));
                }
            }
        }
    }

    private String identifierAsString(TestInputGroupContext context) {
        final Object identifier = context.getIdentifier();

        if(identifier instanceof Constraint) {
            final String generatorNameAsString = (context.getGenerator()).getClass().getSimpleName();

            return generatorNameAsString + " for Constraint " + ((Constraint) identifier).getName();
        } else {
            return identifier.toString();
        }
    }

    @Override
    public void testInputGroupFinished(TestInputGroupContext context) {
        if (logger.isInfoEnabled()) {
            logger.info("Finished test input group \"{}\".", identifierAsString(context));
        }
    }

    @Override
    public void faultCharacterizationStarted(TestInputGroupContext context, FaultCharacterizationAlgorithm algorithm) {
        if (logger.isInfoEnabled()) {
            logger.info("Started fault characterization for \"{}\".", identifierAsString(context));
        }
    }

    @Override
    public void faultCharacterizationFinished(TestInputGroupContext context, Map<Combination, Class<?
            extends Throwable>> exceptionInducingCombinations, Collection<Combination> failureInducingCombinations) {
        if (logger.isInfoEnabled()) {
            logger.info("Finished fault characterization for \"{}\".", identifierAsString(context));
        }

        printExceptionInducingCombinations(exceptionInducingCombinations);
        logger.info(ReportUtility.getFormattedFailureInducingCombinations(failureInducingCombinations));
        printWarning(failureInducingCombinations);
    }

    /**
     * template method implemented by sub-classes reporting found exception-inducing combinations
     *
     * @param failureInducingCombinations found failure-inducing combinations.
     */
    protected void printWarning(Collection<Combination> failureInducingCombinations) {
        // empty for crt
    }

    /**
     * template method implemented by sub-classes reporting found exception-inducing combinations
     *
     * @param exceptionInducingCombinations exception-inducing combinations to print together with exceptions.
     */
    protected void printExceptionInducingCombinations(Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations) {
        // empty for crt
    }

    @Override
    public void faultCharacterizationTestInputsGenerated(TestInputGroupContext context, List<Combination> testInputs) {
        if (logger.isInfoEnabled()) {
            logger.info("Generated additional fault characterization test inputs for \"{}\".", identifierAsString(context));
        }

        if (testInputs != null) {
            for (Combination testInput : testInputs) {
                if (logger.isDebugEnabled())
                    logger.debug(String.valueOf(testInput));
            }
        }
    }

    @Override
    public void testInputExecutionStarted(Combination testInput) {
        logger.info("Started execution for test input {}.", testInput);
    }

    @Override
    public void testInputExecutionFinished(Combination testInput, TestResult result) {
        logger.info("Finished execution for test input {}: {}", testInput, result);
    }

    @Override
    public void report(ReportLevel level, Report report) {
        logger.info("Report with level {}: {}", level, report);
    }
}
