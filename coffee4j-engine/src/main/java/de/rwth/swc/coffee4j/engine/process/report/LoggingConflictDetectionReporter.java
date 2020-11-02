package de.rwth.swc.coffee4j.engine.process.report;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.*;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.report.ConflictDetectionReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link ConflictDetectionReporter} that reports its findings using a {@link Logger}
 *
 * This is an adapted copy of the {@link PrintStreamConflictDetectionReporter}
 */
public class LoggingConflictDetectionReporter implements ConflictDetectionReporter {

    private final Logger logger;
    private final ModelConverter modelConverter;

    /**
     * Creates a new {@link LoggingConflictDetectionReporter} configured with the supplied {@link ModelConverter}
     *
     * @param modelConverter the {@link ModelConverter} to use for conversion values
     */
    public LoggingConflictDetectionReporter(ModelConverter modelConverter) {
        this.logger = LoggerFactory.getLogger("[coffee4j]");
        this.modelConverter = modelConverter;
    }

    @Override
    public void reportDetectedMissingInvalidTuples(List<MissingInvalidTuple> missingInvalidTuples) {
        logger.error("Conflicts among constraints detected!");
        logger.error("");

        for(MissingInvalidTuple missingInvalidTuple : missingInvalidTuples) {
            reportMissingInvalidTuple(missingInvalidTuple);
        }

        logger.error("Please repair the constraints and re-run the tests.");
        logger.error("");
    }

    @Override
    public void reportMinimalDiagnosisHittingSets(List<DiagnosisHittingSet> minimalDiagnosisHittingSets) {
        for(DiagnosisHittingSet set : minimalDiagnosisHittingSets) {
            reportMinimalDiagnosisHittingSet(set);
        }

        logger.error("");
    }

    private void reportMinimalDiagnosisHittingSet(DiagnosisHittingSet set) {
        logger.error("Relax the constraints as follows.");

        for(DiagnosisElement element : set.getDiagnosisElements()) {
            final Combination combination = findCombination(
                    element.getInvolvedParameters(),
                    element.getConflictingValues());
            final Constraint diagnosedConstraint = findConstraint(element.getDiagnosedConstraintId());

            logger.error("Remove {} from constraint {} with parameters {}.",
                    combination,
                    diagnosedConstraint.getName(),
                    diagnosedConstraint.getParameterNames());
        }

        logger.error("");
    }

    private void reportMissingInvalidTuple(MissingInvalidTuple missingInvalidTuple) {
        final Combination combination = findCombination(
                missingInvalidTuple.getInvolvedParameters(),
                missingInvalidTuple.getMissingValues());

        final Constraint negatedErrorConstraint = findConstraint(missingInvalidTuple.getNegatedErrorConstraintId());

        logger.error("For error-constraint {} with parameters {}, {} is missing.",
                negatedErrorConstraint.getName(),
                negatedErrorConstraint.getParameterNames(),
                combination);

        reportExplanation(missingInvalidTuple.getExplanation());
        logger.error("");
    }

    private void reportExplanation(ConflictExplanation explanation) {
        if(explanation instanceof UnknownConflictExplanation) {
            reportUnknownExplanation();
        } else if(explanation instanceof InconsistentBackground) {
            reportInconsistentBackground((InconsistentBackground) explanation);
        } else if(explanation instanceof ConflictSet) {
            reportConflictSet((ConflictSet) explanation);
        } else if(explanation instanceof DiagnosisSets) {
            reportConflictSet(((DiagnosisSets) explanation).getRootConflictSet());
        } else {
            throw new IllegalStateException();
        }
    }

    private void reportUnknownExplanation() {
        logger.error("For more information, enable conflict explanation and diagnosis.");
    }

    private void reportInconsistentBackground(InconsistentBackground explanation) {
        logger.error("\tThe constraint itself is incorrect. {}", explanation);
    }

    private void reportConflictSet(ConflictSet conflictSet) {
        logger.error("The interaction with the following constraint(s) is causing the absence:");

        for(ConflictElement element : conflictSet.getConflictElements()) {
            final Combination combination = findCombination(element.getInvolvedParameters(), element.getConflictingValues());
            final Constraint constraint = findConstraint(element.getConflictingConstraintId());

            logger.error("For constraint {} with parameters {}, {} causes the absence.",
                    constraint.getName(),
                    constraint.getParameterNames(),
                    combination);
        }
    }

    private int[] convertTupleFromDualRepresentation(int[] parameters, int[] values) {
        final int[] convertedTuple = new int[modelConverter.getConvertedModel().getParameterSizes().length];
        Arrays.fill(convertedTuple, CombinationUtil.NO_VALUE);

        for(int i = 0; i < parameters.length; i++) {
            int parameter = parameters[i];
            int value = values[i];

            convertedTuple[parameter] = value;
        }

        return convertedTuple;
    }

    private Combination findCombination(int[] parameters, int[] values) {
        final int [] tuple = convertTupleFromDualRepresentation(parameters, values);

        return modelConverter.convertCombination(tuple);
    }

    private Constraint findConstraint(int id) {
        final TupleList negatedTupleList = modelConverter.getConvertedModel().getErrorTupleLists()
                .stream()
                .filter(tupleList -> tupleList.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("unknown constraint with id " + id));

        return modelConverter.convertConstraint(negatedTupleList);
    }
}
