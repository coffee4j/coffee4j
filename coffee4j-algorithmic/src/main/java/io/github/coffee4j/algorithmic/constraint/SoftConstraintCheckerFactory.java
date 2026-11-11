package io.github.coffee4j.algorithmic.constraint;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;
import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.conflict.InternalDiagnosisSets;
import io.github.coffee4j.algorithmic.conflict.InternalMissingInvalidTuple;
import io.github.coffee4j.algorithmic.util.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.checkValidIdentifier;
import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.findErrorConstraintToBeNegated;
import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.filterErrorConstraintToBeNegated;
import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.negateConstraint;

public class SoftConstraintCheckerFactory implements ConstraintCheckerFactory {

    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        Preconditions.notNull(testModel);
        Preconditions.check(checkValidIdentifier(testModel, toBeNegated.getId()));

        final int threshold = computeThreshold(testModel, toBeNegated);

        if(threshold == 0) {
            return new HardConstraintCheckerFactory()
                    .createConstraintCheckerWithNegation(testModel, toBeNegated);
        } else {
            final List<Constraint> hardConstraints = new ArrayList<>();
            hardConstraints.add(negateConstraint(findErrorConstraintToBeNegated(testModel, toBeNegated)));

            final List<Constraint> softConstraints = filterErrorConstraintToBeNegated(testModel, toBeNegated);
            softConstraints.addAll(testModel.getExclusionConstraints());

            return new SoftConstraintChecker(testModel, hardConstraints, softConstraints, threshold);
        }
    }

    private int computeThreshold(CompleteTestModel testModel, TupleList toBeNegated) {
        final InternalConflictDiagnosisManager diagnostician = new InternalConflictDiagnosisManager();
        final List<InternalMissingInvalidTuple> missingInvalidTuples = diagnostician.diagnose(testModel, toBeNegated);

        return missingInvalidTuples.isEmpty() ? 0 : missingInvalidTuples.stream()
                .flatMapToInt(tuple ->
                        Arrays.stream(((InternalDiagnosisSets) tuple.getExplanation()).getDiagnosisSets())
                                .mapToInt(diagnosisSet -> diagnosisSet.length))
                .max()
                .orElseThrow(() -> new IllegalStateException("cannot compute threshold"));
    }
}
