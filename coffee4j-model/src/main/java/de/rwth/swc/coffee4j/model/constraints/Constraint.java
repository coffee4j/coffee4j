package de.rwth.swc.coffee4j.model.constraints;

import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * Defines a constraint for combinatorial testing as a collection of parameters names and a function which can check
 * whether any given value assignment for those parameters is valid or not according to some logic defined by
 * the function.
 */
public class Constraint {
    
    private final List<String> parameterNames;
    
    private final ConstraintFunction constraintFunction;
    
    /**
     * Creates a new constraint. It is most efficient if only the parameters really involved and not additional ones
     * are given.
     *
     * @param parameterNames     the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param constraintFunction the function by which the values for the parameters are constrained.
     *                           Must not be {@code null}
     */
    public Constraint(List<String> parameterNames, ConstraintFunction constraintFunction) {
        Preconditions.notNull(parameterNames);
        Preconditions.notNull(constraintFunction);
        Preconditions.check(!parameterNames.isEmpty());
        Preconditions.check(!parameterNames.contains(null));
        
        this.parameterNames = parameterNames;
        this.constraintFunction = constraintFunction;
    }
    
    /**
     * @return the names of all involved parameters
     */
    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }
    
    /**
     * @return the function constraining the values of the involved parameters
     */
    public ConstraintFunction getConstraintFunction() {
        return constraintFunction;
    }
    
    @Override
    public String toString() {
        return "Constraint {parameterNames=(" + String.join(", ", parameterNames) + ")}";
    }
    
}


