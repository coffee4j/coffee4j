package de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.AbstractConstraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import de.rwth.swc.coffee4j.engine.converter.constraints.ConstraintConverterFactory;
import de.rwth.swc.coffee4j.engine.converter.constraints.methodbased.SimpleCartesianProductConstraintConverterFactory;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines a constraint for combinatorial testing as a collection of parameters names and a function which can check
 * whether any given value assignment for those parameters is valid or not according to some logic defined by
 * the function.
 */
public class MethodBasedConstraint extends AbstractConstraint {
    
    private final ConstraintFunction constraintFunction;

    /**
     * Creates a constraint using {@link #MethodBasedConstraint(String, List, ConstraintFunction, ConstraintStatus)}
     * with {@link ConstraintStatus#UNKNOWN}
     *
     * @param name name to improve readability without further semantics
     * @param parameterNames the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param constraintFunction the function by which the values for the parameters are constrained.
     *                           Must not be {@code null}
     */
    public MethodBasedConstraint(String name, List<String> parameterNames, ConstraintFunction constraintFunction) {
        this(name, parameterNames, constraintFunction, ConstraintStatus.UNKNOWN);
    }

    /**
     * Creates a new constraint. It is most efficient if only the parameters really involved and not additional ones
     * are given.
     * @param name               a name to improve readability without further semantics
     * @param parameterNames     the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param constraintFunction the function by which the values for the parameters are constrained.
     *                           Must not be {@code null}
     * @param constraintStatus   status is either Unknown or Correct which is related to conflict detection
     */
    public MethodBasedConstraint(String name,
                                 List<String> parameterNames,
                                 ConstraintFunction constraintFunction,
                                 ConstraintStatus constraintStatus) {
        super(name, parameterNames, constraintStatus);

        Preconditions.notNull(constraintFunction);
        this.constraintFunction = constraintFunction;
    }

    @Override
    public boolean checkIfValid(Combination combination) {
        List<?> input = getParameterNames().stream()
                .map(combination::getRawValue)
                .collect(Collectors.toList());

        try {
            return constraintFunction.check(input);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Could not call constraint \"" + getName() + "\" on parameters "
                    + getParameterNames() + " with the given combination due to type issues. " +
                    "This most likely means that the constraint expects a different type than the actual value " +
                    "type of the constrained parameter.", e);
        }
    }

    @Override
    public ConstraintConverterFactory getConverterFactory() {
        return new SimpleCartesianProductConstraintConverterFactory();
    }

    public ConstraintFunction getConstraintFunction() {
        return constraintFunction;
    }
    
}


