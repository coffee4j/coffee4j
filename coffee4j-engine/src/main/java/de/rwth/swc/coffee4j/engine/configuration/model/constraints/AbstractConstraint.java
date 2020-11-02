package de.rwth.swc.coffee4j.engine.configuration.model.constraints;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * Abstract Class providing functionality and fields that are commonly used by all types of constraints.
 */
public abstract class AbstractConstraint implements Constraint {
    private String name;
    private final List<String> parameterNames;
    private final ConstraintStatus constraintStatus;

    /**
     * @param name name to improve readability without further semantics
     * @param parameterNames the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param constraintStatus status is either Unknown or Correct which is related to conflict detection
     */
    public AbstractConstraint(String name,
                              List<String> parameterNames,
                              ConstraintStatus constraintStatus) {
        Preconditions.notNull(name);
        Preconditions.notNull(parameterNames);
        Preconditions.notNull(constraintStatus);
        Preconditions.check(!parameterNames.isEmpty());

        this.name = name;
        this.parameterNames = parameterNames;
        this.constraintStatus = constraintStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    public ConstraintStatus getConstraintStatus() {
        return constraintStatus;
    }

    @Override
    public String toString() {
        return "Constraint {name=" + name + ", parameterNames=(" + String.join(", ", parameterNames) + ")}";
    }
}
