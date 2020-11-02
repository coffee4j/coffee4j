package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;

public class NoConstraintCheckerFactory implements ConstraintCheckerFactory {
    
    private static final ConstraintChecker INSTANCE = new NoConstraintChecker();

    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        return INSTANCE;
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        return INSTANCE;
    }
}
