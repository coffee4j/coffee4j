package de.rwth.swc.coffee4j.engine.process.report.sequential;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.report.util.CombinationFormatter;
import de.rwth.swc.coffee4j.engine.process.report.util.NoOpFormatter;
import de.rwth.swc.coffee4j.engine.process.report.util.ReportUtility;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LoggingSequentialExecutionReporterForGeneration extends LoggingSequentialExecutionReporter implements SequentialExecutionReporter {
    protected CombinationFormatter formatter = new NoOpFormatter();

    @Override
    public void faultCharacterizationStarted(TestInputGroupContext context, FaultCharacterizationAlgorithm algorithm) {
        final String algorithmName = algorithm == null ? "null" : algorithm.getClass().getSimpleName();
        logger.info("The identification of exception-inducing combinations has started with algorithm {}.",
                algorithmName);
    }

    @Override
    protected void printExceptionInducingCombinations(Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations) {
        logger.info(ReportUtility.getFormattedExceptionInducingCombinations(exceptionInducingCombinations, formatter));
    }

    @Override
    public void faultCharacterizationTestInputsGenerated(TestInputGroupContext context, List<Combination> testInputs) {
        logger.info("The following additional test input was generated for the identification of exception-inducing combinations ");
        if (testInputs != null) {
            for (Combination testInput : testInputs) {
                if (logger.isInfoEnabled())
                    logger.info(testInput.toString());
            }
        }
    }

    @Override
    protected void printWarning(Collection<Combination> failureInducingCombinations) {
        if (failureInducingCombinations != null && !failureInducingCombinations.isEmpty()) {
            logger.info(ReportUtility.getWarningForErrorConstraintGeneration());
        }
    }
}
