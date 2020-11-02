package de.rwth.swc.coffee4j.engine.configuration.model.constraints;

import de.rwth.swc.coffee4j.engine.converter.constraints.ConstraintConverterFactory;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.List;

public interface Constraint {
    /**
     * @param name new name of the constraint.
     */
    void setName(String name);

    /**
     * @return name of the constraint.
     */
    String getName();

    /**
     * @return names of all involved parameters.
     */
    List<String> getParameterNames();

    /**
     * @return status of the constraint.
     */
    ConstraintStatus getConstraintStatus();

    /**
     * Checks whether the given combination is valid or not.
     *
     * @param combination combination to be checked.
     * @return {@code true} iff the combination is valid.
     */
    boolean checkIfValid(Combination combination);

    /**
     * @return {@link ConstraintConverterFactory} that can be used to convert this constraint.
     */
    ConstraintConverterFactory getConverterFactory();

    class ConstraintConstants {
        private ConstraintConstants() {
            // empty
        }

        public static final String ANONYMOUS_CONSTRAINT = "";
    }
}
