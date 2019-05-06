package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.diagnosis.ConflictingErrorConstraintSearcher;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class ConstraintCheckerFactory {
    
    private final InputParameterModel model;
    private final List<InternalConstraint> exclusionConstraints;
    private final List<InternalConstraint> errorConstraints;
    
    public ConstraintCheckerFactory(CombinatorialTestModel model) {
        Preconditions.notNull(model);
        
        InternalConstraintConverter converter = new InternalConstraintConverter();
        
        this.model = model;
        this.exclusionConstraints = converter.convertForbiddenTuples(model);
        this.errorConstraints = converter.convertErrorTuples(model);
    }
    
    public ConstraintChecker createNoConstraintsChecker() {
        return new NoConstraintChecker();
    }
    
    public ConstraintChecker createHardConstraintsChecker() {
        return new HardConstraintChecker(model, exclusionConstraints, errorConstraints);
    }
    
    public ConstraintChecker createHardConstraintsCheckerWithNegation(TupleList toBeNegated) {
        Preconditions.check(checkValidIdentifier(toBeNegated.getId()));
        
        return new HardConstraintChecker(model, exclusionConstraints, constraintsWithNegation(toBeNegated));
    }
    
//    public ConstraintChecker createSoftConstraintsChecker(int threshold) {
//        return new SoftConstraintChecker(model, exclusionConstraints, errorConstraints, threshold);
//    }
    
//    public ConstraintChecker createSoftConstraintsCheckerWithNegation(TupleList toBeNegated, int threshold) {
//        Preconditions.check(checkValidIdentifier(toBeNegated.getId()));
//
//        List<InternalConstraint> hardConstraints = new ArrayList<>(exclusionConstraints);
//        hardConstraints.add(negateConstraint(findNegationCandidate(toBeNegated)));
//
//        List<InternalConstraint> softConstraints = filterErrorConstraint(toBeNegated);
//
//        return new SoftConstraintChecker(model, hardConstraints, softConstraints, threshold);
//    }
    
    public ConflictingErrorConstraintSearcher createConflictingErrorConstraintsSearcher(TupleList toBeNegated) {
        return new ConflictingErrorConstraintSearcher(model, exclusionConstraints, constraintsWithNegation(toBeNegated));
    }
    
//    public ConstraintChecker createAdaptiveConstraintsCheckerWithNegation(TupleList toBeNegated) {
//        Preconditions.check(checkValidIdentifier(toBeNegated.getId()));
//
//        return new AdaptiveConstraintChecker(model, exclusionConstraints, constraintsWithNegation(toBeNegated), toBeNegated);
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
    
//    @SuppressWarnings("ConstantConditions")
//    private InternalConstraint findNegationCandidate(TupleList toBeNegated) {
//        return errorConstraints.stream().filter(constraint -> constraint.getId() == toBeNegated.getId()).findFirst().orElseThrow(() -> new IllegalArgumentException("the tuples list to be negated could not be foumd"));
//    }
    
//    private List<InternalConstraint> filterErrorConstraint(TupleList toBeExcluded) {
//        return errorConstraints.stream().filter(constraint -> constraint.getId() != toBeExcluded.getId()).collect(Collectors.toList());
//    }
}
