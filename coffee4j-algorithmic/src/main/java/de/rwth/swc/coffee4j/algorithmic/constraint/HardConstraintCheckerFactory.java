package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.checkValidIdentifier;
import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.errorConstraintsWithNegation;

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
