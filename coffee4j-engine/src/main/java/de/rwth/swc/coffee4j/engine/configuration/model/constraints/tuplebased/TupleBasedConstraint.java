package de.rwth.swc.coffee4j.engine.configuration.model.constraints.tuplebased;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.AbstractConstraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import de.rwth.swc.coffee4j.engine.converter.constraints.ConstraintConverterFactory;
import de.rwth.swc.coffee4j.engine.converter.tuplebased.TupleBasedConstraintConverterFactory;

import java.util.List;

/**
 * Represents a constraint for combinatorial testing based on a forbidden tuple.
 */
public class TupleBasedConstraint extends AbstractConstraint {
    private final Combination combination;

    /**
     * @param name name to improve readability without further semantics
     * @param parameterNames the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param tuple the forbidden tuple
     */
    public TupleBasedConstraint(String name, List<String> parameterNames, Combination tuple) {
        this(name, parameterNames, ConstraintStatus.UNKNOWN, tuple);
    }

    /**
     * @param name name to improve readability without further semantics
     * @param parameterNames the names of all involved parameters. Must not be, or contain {@code null}, or be empty
     * @param constraintStatus status is either Unknown or Correct which is related to conflict detection
     * @param tuple the forbidden tuple
     */
    public TupleBasedConstraint(String name, List<String> parameterNames, ConstraintStatus constraintStatus, Combination tuple) {
        super(name, parameterNames, constraintStatus);

        this.combination = Preconditions.notNull(tuple);
    }

    @Override
    public boolean checkIfValid(Combination combination) {
        return !combination.contains(this.combination);
    }

    @Override
    public ConstraintConverterFactory getConverterFactory() {
        return new TupleBasedConstraintConverterFactory();
    }

    public Combination getCombination() {
        return combination;
    }
}
