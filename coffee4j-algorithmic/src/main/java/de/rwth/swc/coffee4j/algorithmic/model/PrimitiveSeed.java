package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Arrays;
import java.util.Objects;

/**
 * A representation of (partial) seed test cases build upon primitive data types.
 *
 * <p>A (partial) seed test case is a combination which is used as a base for constructing combinatorial test suites.
 * For example, if some combinations greater than the testing strength t are so important that they must always be
 * tested, a seed can be given to include it in the final test suite.
 *
 * <p>A (partial) seed test case is characterized by a given combination, the {@link SeedMode} and a priority.
 * The priority should only be used relative to other seed priorities and should not be combined with the general
 * combinatorial coverage criterion which must always be upheld. If a seed should have no priority, use
 * {@link #NO_PRIORITY}.
 */
public class PrimitiveSeed {
    
    public static final double NO_PRIORITY = 0;

    private final int[] combination;
    private final SeedMode mode;
    private final double priority;
    
    /**
     * Construct a new instance with the given parameters.
     *
     * @param combination the (partial) seed test case. Must not be {@code null}
     * @param mode the mode of the seed. If {@code null}, the default of {@link SeedMode#NON_EXCLUSIVE} is used
     * @param priority defines the importance of this seed relative to other seeds. If a combination is of no particular
     *     importance, use {@link #NO_PRIORITY}.
     */
    public PrimitiveSeed(int[] combination, SeedMode mode, double priority) {
        Preconditions.notNull(combination, "combination required");
        
        this.combination = Arrays.copyOf(combination, combination.length);
        this.mode = mode == null? SeedMode.NON_EXCLUSIVE : mode;
        this.priority = priority;
    }
    
    public int[] getCombination() {
        return Arrays.copyOf(combination, combination.length);
    }
    
    public SeedMode getMode() {
        return mode;
    }
    
    public double getPriority() {
        return priority;
    }
    
    public boolean hasPriority() {
        return priority != NO_PRIORITY;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final PrimitiveSeed other = (PrimitiveSeed) object;
        return Double.compare(other.priority, priority) == 0 &&
                Arrays.equals(combination, other.combination) &&
                mode == other.mode;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(mode, priority);
        result = 31 * result + Arrays.hashCode(combination);
        return result;
    }
    
    @Override
    public String toString() {
        return "PrimitiveBasedSeed{" +
                "combination=" + Arrays.toString(combination) +
                ", mode=" + mode +
                ", priority=" + priority +
                '}';
    }
    
}
