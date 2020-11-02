package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;
import java.util.Objects;

/**
 * Class representing a group of parameters which should be tested at another strength than the default testing
 * strength.
 *
 * <p>This concept is in general called mixed-strength combinatorial testing where there is a defined default
 * testing strength and a number of parameter groups which deviate from this default strength. Such a mechanism
 * is useful to model multiple subsystems in a combinatorial test without increasing the testing strength between
 * the subsystems unnecessarily. Additionally, parameters which are prone to appear in failure-causing combinations
 * can be tested at a higher strength to increase confidence.
 */
public class PrimitiveStrengthGroup {
    
    private final IntSet parameters;
    
    private final int strength;
    
    private PrimitiveStrengthGroup(IntSet parameters, int strength) {
        Preconditions.notNull(parameters);
        Preconditions.check(strength >= 0 && strength <= parameters.size(),
                "Strength for parameters " + parameters + " must be int range [0," + parameters.size()
                + "] but was " + strength);
        
        this.parameters = new IntOpenHashSet(parameters);
        this.strength = strength;
    }
    
    /**
     * Constructs a new {@link PrimitiveStrengthGroup} between the given parameters and the used strength.
     *
     * @param parameters the parameters in the group which should be tested to the given higher testing strength.
     *     Must not be {@code null}
     * @param strength the strength to which the given parameters should be tested. Must not be negative and
     *     must be at most the number of parameters in the group
     * @return the created group at the given strength
     */
    public static PrimitiveStrengthGroup ofStrength(IntSet parameters, int strength) {
        return new PrimitiveStrengthGroup(parameters, strength);
    }
    
    /**
     * Constructs a new {@link PrimitiveStrengthGroup} between the given parameters of the highest possible strength.
     * In practice this means that the cartesian product between the values of the given parameters has to appear
     * in the final test suite.
     *
     * @param parameters the parameters to test at the highest possible strength. Must not be {@code null}
     * @return the created groups at a strength equal to the number of parameter in the group
     */
    public static PrimitiveStrengthGroup ofHighestStrength(IntSet parameters) {
        return new PrimitiveStrengthGroup(parameters, parameters.size());
    }
    
    public IntSet getParameters() {
        return parameters;
    }
    
    public int getNumberOfParameters() {
        return parameters.size();
    }
    
    public int getStrength() {
        return strength;
    }
    
    /**
     * Returns all possible sub groups of size {@code strength} in this {@link PrimitiveStrengthGroup}.
     *
     * <p>For example, if this {@link PrimitiveStrengthGroup} consists of parameters [0, 1, 2, 3] and the strength is 2,
     * then the following sets are returned: {0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}.
     *
     * <p>If the strength is zero and empty list is returned.
     *
     * @return all possible sub-combinations of the parameters in this groups which have the size {@code strength}.
     *     Never returns {@code null}
     */
    public List<IntSet> getAllSubGroups() {
        if (strength == parameters.size()) {
            return List.of(parameters);
        } else {
            return Combinator.computeParameterCombinations(parameters.toIntArray(), strength);
        }
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final PrimitiveStrengthGroup other = (PrimitiveStrengthGroup) object;
        return strength == other.strength &&
                Objects.equals(parameters, other.parameters);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parameters, strength);
    }
    
    @Override
    public String toString() {
        return "StrengthGroup{" +
                "parameters=" + parameters +
                ", strength=" + strength +
                '}';
    }
    
}
