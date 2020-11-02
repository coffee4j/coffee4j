package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Collection;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Combination.combination;

/**
 * A representation of (partial) seed test cases.
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
public class Seed {
    
    public static final double NO_PRIORITY = PrimitiveSeed.NO_PRIORITY;
    
    private final Combination combination;
    private final SeedMode mode;
    private final double priority;
    
    private Seed(Builder builder) {
        Preconditions.notNull(builder.combination, "combination required");
        Preconditions.notNull(builder.mode, "mode required");
        
        this.combination = builder.combination;
        this.mode = builder.mode;
        this.priority = builder.priority;
    }
    
    public Combination getCombination() {
        return combination;
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
    
    /**
     * Constructs a builder instance for a seed which consists of the given combination.
     *
     * @param combination the combination of the seed. Must not be {@code null}
     * @return a builder to add additional information to the seed
     */
    public static Builder seed(Combination combination) {
        Preconditions.notNull(combination, "combination required");
        
        return new Builder(combination);
    }
    
    /**
     * Constructs a builder instance for a seed which consists of a combination with the given entries.
     *
     * @param entries the entries of a combination as a map from parameter names to parameter raw values.
     *     Must not be {@code null}
     * @return a builder to add additional information to the seed
     * @see Combination#combination(Map.Entry[]) 
     */
    @SafeVarargs
    public static Builder seed(Map.Entry<String, Object>... entries) {
        return new Builder(combination(entries));
    }
    
    /**
     * Constructs a builder instance for a seed which consists of a combination with the given entries and which
     * has {@link SeedMode#EXCLUSIVE} as a mode.
     * 
     * @param entries the entries of a combination as a map from parameter names to parameter raw values.
     *     Must not be {@code null} 
     * @return a builder to add additional information to the seed
     * @see #seed(Map.Entry[])
     */
    @SafeVarargs
    public static Builder suspiciousSeed(Map.Entry<String, Object>... entries) {
        return seed(entries)
                .mode(SeedMode.EXCLUSIVE);
    }
    
    /**
     * Class used to create new {@link Seed} instances using the builder pattern.
     */
    public static final class Builder {
    
        private Combination combination;
        private Combination.Builder combinationBuilder;
        private SeedMode mode = SeedMode.NON_EXCLUSIVE;
        private double priority = NO_PRIORITY;
        
        private Builder(Combination.Builder combinationBuilder) {
            this.combinationBuilder = combinationBuilder;
        }
        
        private Builder(Combination combination) {
            this.combination = combination;
        }
    
        /**
         * Sets the mode of the seed. The default is {@link SeedMode#NON_EXCLUSIVE}.
         *
         * @param mode the desired mode of the seed
         * @return this
         */
        public Builder mode(SeedMode mode) {
            this.mode = mode;
            
            return this;
        }
    
        /**
         * Sets the mode of the seed to {@link SeedMode#EXCLUSIVE}. The default is {@link SeedMode#NON_EXCLUSIVE}.
         *
         * @return this
         */
        public Builder suspicious() {
            return mode(SeedMode.EXCLUSIVE);
        }
    
        /**
         * Sets an explicit priority for the seed. Priorities show the relative importance of multiple seeds.
         * If the seed is not priorities the default of {@link #NO_PRIORITY} should be used.
         *
         * @param priority the desired priority
         * @return this
         */
        public Builder priority(double priority) {
            this.priority = priority;
        
            return this;
        }
    
        /**
         * Constructs the actual {@link Seed} instance from this builder.
         *
         * @param parameters the parameters to optionally build a {@link Combination.Builder}. If this is not necessary
         *     because {@link #Builder(Combination.Builder)} was not used, {@code null} or an arbitrary collection
         *     can be passed to this method
         * @return the {@link Seed} constructed using the information supplied to this builder
         */
        public Seed build(Collection<Parameter> parameters) {
            if (combination == null) {
                if (combinationBuilder == null) {
                    throw new IllegalStateException(
                            "It should not be possible for both combination and combinationBuilder to be null.");
                } else {
                    combination = combinationBuilder.build(parameters);
                }
            }
            
            return new Seed(this);
        }
    
    }
    
}
