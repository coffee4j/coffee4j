package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.NoConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * A {@link TestModel} which acts a a view on the {@link CompleteTestModel} that view the model for
 * one specific error constraint or only the positive test cases.
 *
 * <p>This is primarily needed for seeds and mixed strength groups as they are specific to positive combinatorial
 * testing or error-constraint-specific negative combinatorial testing.
 */
public class GroupSpecificTestModel implements TestModel {
    
    private static final String DELEGATE_REQUIRED_MESSAGE = "Delegate must not be null";
    
    private final int errorConstraintId;
    private final int testingStrength;
    
    private final CompleteTestModel delegate;
    private final ConstraintChecker constraintChecker;
    
    private GroupSpecificTestModel(int errorConstraintId, int testingStrength, CompleteTestModel delegate,
            ConstraintChecker constraintChecker) {
    
        Preconditions.notNull(delegate, DELEGATE_REQUIRED_MESSAGE);
        Preconditions.check(errorConstraintId >= 0 || errorConstraintId == CompleteTestModel.POSITIVE_TESTS_ID, "invalid group");
        Preconditions.check(testingStrength >= 0 && testingStrength <= delegate.getNumberOfParameters(),
                "invalid testing strength");
        
        this.errorConstraintId = errorConstraintId;
        this.testingStrength = testingStrength;
        this.delegate = delegate;
        this.constraintChecker = constraintChecker == null ? new NoConstraintChecker() : constraintChecker;
    }
    
    /**
     * Creates a new view on the {@link CompleteTestModel} which only considers the configuration for
     * positive combinatorial testing.
     *
     * @param delegate the {@link CompleteTestModel} to which this view delegates its calls
     * @param constraintChecker a constraint checker which checks whether a (partial) test case is valid
     * @return a view on the positive combinatorial test configuration
     */
    public static GroupSpecificTestModel positive(CompleteTestModel delegate, ConstraintChecker constraintChecker) {
        Preconditions.notNull(delegate, DELEGATE_REQUIRED_MESSAGE);
        return new GroupSpecificTestModel(CompleteTestModel.POSITIVE_TESTS_ID, delegate.getPositiveTestingStrength(),
                delegate, constraintChecker);
    }
    
    /**
     * Creates a new view on the {@link CompleteTestModel} which only considers the configuration for
     * negative combinatorial testing for the given error constraint.
     *
     * @param errorConstraintId the id of the error constraint for which the configuration is returned
     * @param delegate the {@link CompleteTestModel} to which this view delegates its calls
     * @param constraintChecker a constraint checker which checks whether a (partial) test case is valid.
     *     In this case "valid" means that the test case must violate the error constraint with the given id
     * @return a view on the negative combinatorial test configuration for the given error constraint
     */
    public static GroupSpecificTestModel negative(int errorConstraintId, CompleteTestModel delegate,
            ConstraintChecker constraintChecker) {
    
        Preconditions.notNull(delegate, DELEGATE_REQUIRED_MESSAGE);
        return new GroupSpecificTestModel(errorConstraintId, delegate.getNegativeTestingStrength(), delegate, constraintChecker);
    }
    
    @Override
    public int getDefaultTestingStrength() {
        return testingStrength;
    }
    
    @Override
    public List<PrimitiveStrengthGroup> getMixedStrengthGroups() {
        return delegate.getMixedStrengthGroups(errorConstraintId);
    }
    
    @Override
    public int[] getParameterSizes() {
        return delegate.getParameterSizes();
    }
    
    @Override
    public double getWeight(int parameter, int value, double defaultWeight) {
        return delegate.getWeight(parameter, value, defaultWeight);
    }
    
    @Override
    public List<PrimitiveSeed> getSeeds() {
        return delegate.getSeeds(errorConstraintId);
    }
    
    @Override
    public ConstraintChecker getConstraintChecker() {
        return constraintChecker;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final GroupSpecificTestModel other = (GroupSpecificTestModel) object;
        return errorConstraintId == other.errorConstraintId &&
                testingStrength == other.testingStrength &&
                Objects.equals(delegate, other.delegate) &&
                Objects.equals(constraintChecker, other.constraintChecker);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(errorConstraintId, testingStrength, delegate, constraintChecker);
    }
    
    @Override
    public String toString() {
        if (errorConstraintId == CompleteTestModel.POSITIVE_TESTS_ID) {
            return "GroupSpecificTestModel{" +
                    "positive" +
                    ", testingStrength=" + testingStrength +
                    ", delegate=" + delegate +
                    ", constraintChecker=" + constraintChecker +
                    '}';
        } else {
            return "GroupSpecificTestModel{" +
                    "errorConstraintId=" + errorConstraintId +
                    ", testingStrength=" + testingStrength +
                    ", delegate=" + delegate +
                    ", constraintChecker=" + constraintChecker +
                    '}';
        }
    }
    
}
