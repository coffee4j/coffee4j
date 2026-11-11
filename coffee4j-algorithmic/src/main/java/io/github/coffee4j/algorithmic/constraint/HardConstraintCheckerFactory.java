package io.github.coffee4j.algorithmic.constraint;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;
import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.util.Preconditions;

import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.checkValidIdentifier;
import static io.github.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.errorConstraintsWithNegation;

public class HardConstraintCheckerFactory implements ConstraintCheckerFactory {

    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        return new HardConstraintChecker(
                testModel,
                testModel.getExclusionConstraints(),
                testModel.getErrorConstraints());
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        Preconditions.check(checkValidIdentifier(testModel, toBeNegated.getId()));

        return new HardConstraintChecker(
                testModel,
                testModel.getExclusionConstraints(),
                errorConstraintsWithNegation(testModel, toBeNegated));
    }
}
