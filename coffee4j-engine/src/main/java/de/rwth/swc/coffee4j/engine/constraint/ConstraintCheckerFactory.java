package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class ConstraintCheckerFactory {
    
    private final TestModel testModel;
    private final List<InternalConstraint> exclusionConstraints;
    private final List<InternalConstraint> errorConstraints;
    
    public ConstraintCheckerFactory(TestModel testModel) {
        Preconditions.notNull(testModel);

        this.testModel = testModel;
        this.exclusionConstraints = testModel.getExclusionConstraints();
        this.errorConstraints = testModel.getErrorConstraints();
    }
    
    public ConstraintChecker createNoConstraintsChecker() {
        return new NoConstraintChecker();
    }
    
    public ConstraintChecker createHardConstraintsChecker() {
        return new HardConstraintChecker(testModel, exclusionConstraints, errorConstraints);
    }
    
    public ConstraintChecker createHardConstraintsCheckerWithNegation(TupleList toBeNegated) {
        Preconditions.check(checkValidIdentifier(toBeNegated.getId()));
        
        return new HardConstraintChecker(testModel, exclusionConstraints, constraintsWithNegation(toBeNegated));
    }
    
//    public ConstraintChecker createSoftConstraintsChecker(int threshold) {
//        return new SoftConstraintChecker(testModel, exclusionConstraints, errorConstraints, threshold);
//    }

//    public ConstraintChecker createSoftConstraintsCheckerWithNegation(TupleList toBeNegated, int threshold) {
//        Preconditions.check(checkValidIdentifier(toBeNegated.getId()));
//
//        List<InternalConstraint> hardConstraints = new ArrayList<>(exclusionConstraints);
//        hardConstraints.add(negateConstraint(findNegationCandidate(toBeNegated)));
//
//        List<InternalConstraint> softConstraints = filterErrorConstraint(toBeNegated);
//
//        return new SoftConstraintChecker(testModel, hardConstraints, softConstraints, threshold);
//    }
    
    private boolean checkValidIdentifier(int identifier) {
        return errorConstraints.stream().anyMatch(constraint -> constraint.getId() == identifier);
    }
    
    private List<InternalConstraint> constraintsWithNegation(TupleList tupleList) {
        final List<InternalConstraint> constraintsWithNegation = new ArrayList<>(errorConstraints.size());
        
        for (InternalConstraint constraint : errorConstraints) {
            if (constraint.getId() == tupleList.getId()) {
                constraintsWithNegation.add(negateConstraint(constraint));
            } else {
                constraintsWithNegation.add(constraint);
            }
        }
        
        return constraintsWithNegation;
    }
    
    private InternalConstraint negateConstraint(InternalConstraint constraint) {
        return new NegatingInternalConstraint(constraint);
    }
}
