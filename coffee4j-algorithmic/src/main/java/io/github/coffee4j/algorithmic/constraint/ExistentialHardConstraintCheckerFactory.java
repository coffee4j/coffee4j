package io.github.coffee4j.algorithmic.constraint;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;
import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.util.Preconditions;

import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.*;

public class ExistentialHardConstraintCheckerFactory implements ConstraintCheckerFactory {

    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        throw new UnsupportedOperationException("can only be used by IpogNeg");
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        Preconditions.check(checkValidIdentifier(testModel, toBeNegated.getId()));

        return new HardConstraintChecker(
                testModel,
                testModel.getExclusionConstraints(),
                errorConstraintsWithExistentialNegation(testModel, toBeNegated));
    }
}
