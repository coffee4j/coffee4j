package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;

public interface ConstraintCheckerFactory {

    ConstraintChecker createConstraintChecker(CompleteTestModel testModel);

    ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel,
                                                          TupleList toBeNegated);
}
