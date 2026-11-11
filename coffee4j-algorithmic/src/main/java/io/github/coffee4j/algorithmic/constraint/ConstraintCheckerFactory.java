package io.github.coffee4j.algorithmic.constraint;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;
import io.github.coffee4j.algorithmic.model.TupleList;

public interface ConstraintCheckerFactory {

    ConstraintChecker createConstraintChecker(CompleteTestModel testModel);

    ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel,
                                                          TupleList toBeNegated);
}
