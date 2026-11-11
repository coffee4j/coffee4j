package io.github.coffee4j.engine.process.report;

import io.github.coffee4j.algorithmic.conflict.*;
import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.util.CombinationUtil;
import io.github.coffee4j.algorithmic.util.Preconditions;
import io.github.coffee4j.engine.configuration.model.Combination;
import io.github.coffee4j.engine.configuration.model.constraints.Constraint;
import io.github.coffee4j.engine.converter.model.ModelConverter;
import io.github.coffee4j.engine.report.ConflictDetectionReporter;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link ConflictDetectionReporter} that reports its findings to a {@link PrintStream}
 *
 *
 */
public class PrintStreamConflictDetectionReporter implements ConflictDetectionReporter {

    private final PrintStream printStream;
    private final ModelConverter modelConverter;

    /**
     * Creates a new {@link PrintStreamConflictDetectionReporter} based on a {@link PrintStream} and a
     * {@link ModelConverter}
     *
     * @param printStream the {@link PrintStream} to use. Should not be {@code null}
     * @param modelConverter the {@link ModelConverter} to used. Should not be {@code null}
     */
    public PrintStreamConflictDetectionReporter(PrintStream printStream, ModelConverter modelConverter) {
        Preconditions.notNull(printStream);
        Preconditions.notNull(modelConverter);

        this.printStream = printStream;
        this.modelConverter = modelConverter;
    }

    @Override
    public void reportDetectedMissingInvalidTuples(List<MissingInvalidTuple> missingInvalidTuples) {
        printStream.println("ERROR: Conflicts among constraints detected!");
        printStream.println("--------------------------------------------");
        printStream.println();

        for(MissingInvalidTuple missingInvalidTuple : missingInvalidTuples) {
            reportMissingInvalidTuple(missingInvalidTuple);
        }

        printStream.println("Please repair the constraints and re-run the tests.");
        printStream.println();
    }

    @Override
    public void reportMinimalDiagnosisHittingSets(List<DiagnosisHittingSet> minimalHittingSets) {
        printStream.println("ERROR: Conflicts among constraints detected!");
        printStream.println("--------------------------------------------");

        for(DiagnosisHittingSet set : minimalHittingSets) {
            reportMinimalDiagnosisHittingSet(set);
        }

        printStream.println("Please repair the constraints and re-run the tests.");
        printStream.println();
    }

    private void reportMinimalDiagnosisHittingSet(DiagnosisHittingSet set) {
        printStream.println("Relax the constraints as follows.");

        for(DiagnosisElement element : set.getDiagnosisElements()) {
            final Combination combination = findCombination(
                    element.getInvolvedParameters(),
                    element.getConflictingValues());
            final Constraint diagnosedConstraint = findConstraint(element.getDiagnosedConstraintId());

            printStream.println("\tRemove " + combination + " from " + diagnosedConstraint + ".");
        }

        printStream.println();
    }

    private void reportMissingInvalidTuple(MissingInvalidTuple missingInvalidTuple) {
        final Combination combination = findCombination(
                missingInvalidTuple.getInvolvedParameters(),
                missingInvalidTuple.getMissingValues());

        final Constraint negatedErrorConstraint = findConstraint(missingInvalidTuple.getNegatedErrorConstraintId());

        printStream.println("For error-constraint \n\t" + negatedErrorConstraint + ",\n\t" + combination + "\nis missing.");
        printStream.println();

        reportExplanation(missingInvalidTuple.getExplanation());

        printStream.println();
    }

    private void reportExplanation(ConflictExplanation explanation) {
        switch (explanation) {
            case UnknownConflictExplanation _ -> reportUnknownExplanation();
            case InconsistentBackground inconsistentBackground -> reportInconsistentBackground(inconsistentBackground);
            case ConflictSet conflictSet -> reportConflictSet(conflictSet);
            case DiagnosisSets diagnosisSets -> reportDiagnosisSet(diagnosisSets);
            case null, default -> throw new IllegalStateException();
        }
    }

    private void reportUnknownExplanation() {
        printStream.println("For more information, enable conflict explanation and diagnosis.");
    }

    private void reportInconsistentBackground(InconsistentBackground explanation) {
        printStream.println("\tThe constraint itself is incorrect. " + explanation);
    }

    private void reportConflictSet(ConflictSet conflictSet) {
        printStream.println("The interaction with the following constraints is causing the absence:");

        for(ConflictElement element : conflictSet.getConflictElements()) {
            final Combination combination = findCombination(element.getInvolvedParameters(), element.getConflictingValues());
            final Constraint constraint = findConstraint(element.getConflictingConstraintId());

            printStream.println("\t" + constraint + " with " + combination);
        }
    }

    private void reportDiagnosisSet(DiagnosisSets diagnosisSets) {
        for(DiagnosisSet diagnosis : diagnosisSets.getDiagnosisSets()) {
            reportDiagnosis(diagnosis);
        }
    }

    private void reportDiagnosis(DiagnosisSet diagnosis) {
        printStream.println("As a diagnosis, relax ");

        for(DiagnosisElement element : diagnosis.diagnosisElements()) {
            final Combination combination = findCombination(element.getInvolvedParameters(), element.getConflictingValues());
            final Constraint constraint = findConstraint(element.getDiagnosedConstraintId());

            printStream.println("\t" + combination + " of " + constraint);
        }

        printStream.println("to remove all conflicts.");
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
