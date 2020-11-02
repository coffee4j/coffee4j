package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.constraint.Constraint;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintList;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMaps;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;

/**
 * A class containing all important information needed for one combinatorial test. This includes the desired testing
 * strength, the parameters and forbidden/error constraints.
 * Forbidden constraints should never appear in any test input as they just cannot be executed, while error
 * constraints should lead to validation or other kinds of errors in an application and should therefore be tested
 */
public class CompleteTestModel implements TestModel {
    
    public static final int POSITIVE_TESTS_ID = -1;
    
    private final int positiveTestingStrength;
    private final int negativeTestingStrength;
    
    private final int[] parameterSizes;
    
    private final List<TupleList> exclusionTupleLists;
    private final List<TupleList> errorTupleLists;
    private final ConstraintList lazyExclusionConstraints;
    private final ConstraintList lazyErrorConstraints;
    
    private final Int2ObjectMap<Int2DoubleMap> weights;
    
    private final Int2ObjectMap<List<PrimitiveSeed>> seeds;
    
    private final Int2ObjectMap<List<PrimitiveStrengthGroup>> mixedStrengthGroups;
    
    /**
     * Builder for a new {@link CompleteTestModel}.
     *
     * @return a fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder builder(CompleteTestModel oldModel) {
        return new Builder()
                .positiveTestingStrength(oldModel.positiveTestingStrength)
                .negativeTestingStrength(oldModel.negativeTestingStrength)
                .parameterSizes(oldModel.parameterSizes)
                .exclusionTupleLists(oldModel.exclusionTupleLists)
                .errorTupleLists(oldModel.errorTupleLists)
                .weights(oldModel.weights)
                .seeds(oldModel.seeds)
                .mixedStrengthGroups(oldModel.mixedStrengthGroups);
    }
    
    private CompleteTestModel(Builder builder) {
        Preconditions.notNull(builder, "builder");
        Preconditions.notNull(builder.parameterSizes, "parameterSizes");
        Preconditions.check(builder.parameterSizes.length > 0, "too few parameters");
        Preconditions.check(builder.positiveTestingStrength >= 0, "negative positive testing strength");
        Preconditions.check(builder.positiveTestingStrength <= builder.parameterSizes.length,
                "positive testing strength too high");
        Preconditions.check(builder.negativeTestingStrength >= 0, "negative negative testing strength");
        Preconditions.check(builder.negativeTestingStrength <= builder.parameterSizes.length,
                "negative testing strength too high");
        
        checkParameterSizes(builder.parameterSizes);
        checkExclusionTupleIdentifier(builder.parameterSizes, builder.exclusionTupleLists);
        checkExclusionTupleIdentifier(builder.parameterSizes, builder.errorTupleLists);
        checkTuplesListIds(builder.exclusionTupleLists, builder.errorTupleLists);
        
        this.positiveTestingStrength = builder.positiveTestingStrength;
        this.negativeTestingStrength = builder.negativeTestingStrength;
        this.parameterSizes = Arrays.copyOf(builder.parameterSizes, builder.parameterSizes.length);
        this.exclusionTupleLists = new ArrayList<>(builder.exclusionTupleLists);
        this.errorTupleLists = new ArrayList<>(builder.errorTupleLists);

        this.lazyExclusionConstraints = new ConstraintList(exclusionTupleLists);
        this.lazyErrorConstraints = new ConstraintList(errorTupleLists);
        
        this.weights = new Int2ObjectOpenHashMap<>(builder.weights.size());
        for (Int2ObjectMap.Entry<Int2DoubleMap> builderParameterEntry : builder.weights.int2ObjectEntrySet()) {
            final Int2DoubleMap parameterEntry = weights.computeIfAbsent(builderParameterEntry.getIntKey(),
                    key -> new Int2DoubleOpenHashMap(builderParameterEntry.getValue().size()));
            
            for (Int2DoubleMap.Entry builderValueEntry : builderParameterEntry.getValue().int2DoubleEntrySet()) {
                parameterEntry.put(builderValueEntry.getIntKey(), builderValueEntry.getDoubleValue());
            }
        }
        
        this.seeds = new Int2ObjectOpenHashMap<>(builder.seeds.size());
        for (Int2ObjectMap.Entry<List<PrimitiveSeed>> errorConstraintSeeds : builder.seeds.int2ObjectEntrySet()) {
            this.seeds.put(
                    errorConstraintSeeds.getIntKey(),
                    new ArrayList<>(errorConstraintSeeds.getValue()));
        }
        
        this.mixedStrengthGroups = new Int2ObjectOpenHashMap<>(builder.mixedStrengthGroups.size());
        for (Int2ObjectMap.Entry<List<PrimitiveStrengthGroup>> errorConstraintStrengthGroups
                : builder.mixedStrengthGroups.int2ObjectEntrySet()) {
            
            this.mixedStrengthGroups.put(
                    errorConstraintStrengthGroups.getIntKey(),
                    new ArrayList<>(errorConstraintStrengthGroups.getValue()));
        }
    }
    
    private static void checkTuplesListIds(Collection<TupleList> forbiddenTupleLists,
                                           Collection<TupleList> errorTupleLists) {
        IntSet uniques = new IntOpenHashSet(forbiddenTupleLists.size() + errorTupleLists.size());
        
        for (TupleList tupleList : forbiddenTupleLists) {
            Preconditions.check(uniques.add(tupleList.getId()), "duplicates tupleList with id " + tupleList.getId());
        }
        
        for (TupleList tupleList : errorTupleLists) {
            Preconditions.check(uniques.add(tupleList.getId()), "duplicates tupleList with id " + tupleList.getId());
        }
    }
    
    private static void checkParameterSizes(int[] parameterSizes) {
        for (int parameterSize : parameterSizes) {
            Preconditions.check(parameterSize > 1);
        }
    }
    
    private static void checkExclusionTupleIdentifier(int[] parameterSizes, Collection<TupleList> forbiddenTupleLists) {
        for (TupleList forbiddenTuples : forbiddenTupleLists) {
            for (int parameter : forbiddenTuples.getInvolvedParameters()) {
                Preconditions.check(parameter >= 0);
                Preconditions.check(parameter < parameterSizes.length);
            }
        }
    }
    
    @Override
    public int getDefaultTestingStrength() {
        return getPositiveTestingStrength();
    }
    
    /**
     * @return the positive testing strength
     */
    public int getPositiveTestingStrength() {
        return positiveTestingStrength;
    }

    /**
     * @return the negative testing strength
     */
    public int getNegativeTestingStrength() {
        return negativeTestingStrength;
    }
    
    /**
     * Gets the mixed strength groups for the specific given error constraint or for the positive test cases if the
     * {@code errorConstraintId} is {@link #POSITIVE_TESTS_ID}.
     *
     * @param errorConstraintId the identification of the mixed strength groups
     * @return the mixed strength grops for the given error constraint or the positive test set
     */
    public List<PrimitiveStrengthGroup> getMixedStrengthGroups(int errorConstraintId) {
        return mixedStrengthGroups.getOrDefault(errorConstraintId, List.of());
    }
    
    public Int2ObjectMap<List<PrimitiveStrengthGroup>> getMixedStrengthGroupsMap() {
        return mixedStrengthGroups;
    }
    
    @Override
    public List<PrimitiveStrengthGroup> getMixedStrengthGroups() {
        return getMixedStrengthGroups(POSITIVE_TESTS_ID);
    }
    
    /**
     * @return the number of values for each parameter
     */
    @Override
    public int[] getParameterSizes() {
        return Arrays.copyOf(parameterSizes, parameterSizes.length);
    }
    
    @Override
    public double getWeight(int parameter, int value, double defaultWeight) {
        return weights.getOrDefault(parameter, Int2DoubleMaps.EMPTY_MAP)
                .getOrDefault(value, defaultWeight);
    }
    
    /**
     * Gets the seed test cases for the specific given error constraint or for the positive test cases if the
     * {@code errorConstraintId} is {@link #POSITIVE_TESTS_ID}.
     *
     * @param errorConstraintId the identification of the seed list
     * @return the seeds for the given error constraint or the positive test set
     */
    public List<PrimitiveSeed> getSeeds(int errorConstraintId) {
        return seeds.getOrDefault(errorConstraintId, List.of());
    }
    
    public Int2ObjectMap<List<PrimitiveSeed>> getSeedsMap() {
        return new Int2ObjectOpenHashMap<>(seeds);
    }
    
    @Override
    public List<PrimitiveSeed> getSeeds() {
        return getSeeds(POSITIVE_TESTS_ID);
    }
    
    /**
     * @return all exclusion combinations. Tests containing these combinations cannot be executed
     */
    public List<TupleList> getExclusionTupleLists() {
        return Collections.unmodifiableList(exclusionTupleLists);
    }
    
    /**
     * @return all error combinations. Tests containing these combinations can be executed but should cause an error
     */
    public List<TupleList> getErrorTupleLists() {
        return Collections.unmodifiableList(errorTupleLists);
    }

    public List<Constraint> getExclusionConstraints() {
        return Collections.unmodifiableList(lazyExclusionConstraints.getConstraints());
    }

    public List<Constraint> getErrorConstraints() {
        return Collections.unmodifiableList(lazyErrorConstraints.getConstraints());
    }
    
    @Override
    public ConstraintChecker getConstraintChecker() {
        return new HardConstraintCheckerFactory()
                .createConstraintChecker(this);
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final CompleteTestModel other = (CompleteTestModel) object;
        return positiveTestingStrength == other.positiveTestingStrength
                && negativeTestingStrength == other.negativeTestingStrength
                && Arrays.equals(parameterSizes, other.parameterSizes)
                && Objects.equals(exclusionTupleLists, other.exclusionTupleLists)
                && Objects.equals(errorTupleLists, other.errorTupleLists)
                && Objects.equals(lazyExclusionConstraints, other.lazyExclusionConstraints)
                && Objects.equals(lazyErrorConstraints, other.lazyErrorConstraints)
                && Objects.equals(seeds, other.seeds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positiveTestingStrength, negativeTestingStrength, parameterSizes, exclusionTupleLists,
                errorTupleLists, lazyExclusionConstraints, lazyErrorConstraints, seeds);
    }

    @Override
    public String toString() {
        return "TestModel{" +
                "positive testing strength=" + positiveTestingStrength +
                ", negative testing strength=" + negativeTestingStrength +
                ", parameterSizes=" + Arrays.toString(parameterSizes) +
                ", exclusionTupleLists=" + exclusionTupleLists +
                ", errorTupleLists=" + errorTupleLists +
                ", lazyExclusionConstraints=" + lazyExclusionConstraints +
                ", lazyErrorConstraints=" + lazyErrorConstraints +
                ", seeds=" + seeds +
                '}';
    }
    
    /**
     * A class for constructing a new {@link CompleteTestModel} instance using the bulider pattern.
     */
    public static final class Builder {
    
        private int positiveTestingStrength = 0;
        private int negativeTestingStrength = 0;
    
        private int[] parameterSizes;
    
        private List<TupleList> exclusionTupleLists = List.of();
        private List<TupleList> errorTupleLists = List.of();
        
        private final Int2ObjectMap<Int2DoubleMap> weights = new Int2ObjectOpenHashMap<>();
    
        private final Int2ObjectMap<List<PrimitiveSeed>> seeds = new Int2ObjectOpenHashMap<>();
    
        private final Int2ObjectMap<List<PrimitiveStrengthGroup>> mixedStrengthGroups = new Int2ObjectOpenHashMap<>();
        
        private Builder() {
        }
    
        /**
         * @param positiveTestingStrength the desired default testing strength for positive combinatorial testing
         * @return this
         */
        public Builder positiveTestingStrength(int positiveTestingStrength) {
            this.positiveTestingStrength = positiveTestingStrength;
            
            return this;
        }
    
        /**
         * @param negativeTestingStrength the desired default testing strength for negative combinatorial testing
         * @return this
         */
        public Builder negativeTestingStrength(int negativeTestingStrength) {
            this.negativeTestingStrength = negativeTestingStrength;
    
            return this;
        }
    
        /**
         * @param mixedStrengthGroups additional {@link PrimitiveStrengthGroup StrengthGroups} for positive combinatorial testing
         * @return this
         */
        public Builder mixedStrengthGroups(Collection<PrimitiveStrengthGroup> mixedStrengthGroups) {
            mixedStrengthGroups(POSITIVE_TESTS_ID, mixedStrengthGroups);
            
            return this;
        }
    
        /**
         * @param errorConstraintId the id of the error constraint for which these strength groups are required
         * @param mixedStrengthGroups additional {@link PrimitiveStrengthGroup StrengthGroups} for the given error constraint
         *     in negative combinatorial testing
         * @return this
         */
        public Builder mixedStrengthGroups(int errorConstraintId, Collection<PrimitiveStrengthGroup> mixedStrengthGroups) {
            this.mixedStrengthGroups.put(errorConstraintId, new ArrayList<>(mixedStrengthGroups));
    
            return this;
        }
    
        /**
         * @param mixedStrengthGroups a map from error constraint keys to their respective required strength groups.
         *     This is a convenience function for directly added strength groups for many error constraints
         * @return this
         */
        public Builder mixedStrengthGroups(Int2ObjectMap<? extends Collection<PrimitiveStrengthGroup>> mixedStrengthGroups) {
            for (Int2ObjectMap.Entry<? extends Collection<PrimitiveStrengthGroup>> errorConstraintStrengthGroups
                    : mixedStrengthGroups.int2ObjectEntrySet()) {
    
                mixedStrengthGroups(errorConstraintStrengthGroups.getIntKey(),
                        errorConstraintStrengthGroups.getValue());
            }
        
            return this;
        }
    
        /**
         * @param parameterSizes the individual sizes of each parameter. [2, 3, 4] says that the first parameter has
         *     two values, the second one three, and the third one four
         * @return this
         */
        public Builder parameterSizes(int... parameterSizes) {
            this.parameterSizes = parameterSizes;
    
            return this;
        }
    
        /**
         * @param exclusionTupleLists lists of tuples which must never appear in any generated combinatorial test case
         *     (regardless of whether the test is negative or positive)
         * @return this
         */
        public Builder exclusionTupleLists(Collection<TupleList> exclusionTupleLists) {
            this.exclusionTupleLists = new ArrayList<>(exclusionTupleLists);
            
            return this;
        }
    
        /**
         * @param errorTupleLists lists of tuples which can be individually negated to perform negative combinatorial
         *     testing. This means that in a negative combinatorial test one given {@link TupleList} is always
         *     violated while the others are not
         * @return this
         */
        public Builder errorTupleLists(Collection<TupleList> errorTupleLists) {
            this.errorTupleLists = new ArrayList<>(errorTupleLists);
    
            return this;
        }
    
        /**
         * Sets the weight for a specific value of a parameter.
         *
         * @param parameter the index of the parameter starting at zero
         * @param value the index of the value starting at zero
         * @param weight the weight which is assigned to the value. Higher weights mean higher priority
         * @return this
         */
        public Builder weight(int parameter, int value, double weight) {
            weights.computeIfAbsent(parameter, key -> new Int2DoubleOpenHashMap())
                    .put(value, weight);
    
            return this;
        }
    
        /**
         * Sets multiple weights for specific values.
         *
         * @param weights a map from parameter indices to maps of value indices to weights. Higher weights mean
         *     higher priority. The indices start at zero
         * @return this
         */
        public Builder weights(Int2ObjectMap<Int2DoubleMap> weights) {
            for (Int2ObjectMap.Entry<Int2DoubleMap> parameterEntry : weights.int2ObjectEntrySet()) {
                for (Int2DoubleMap.Entry valueEntry : parameterEntry.getValue().int2DoubleEntrySet()) {
                    weight(parameterEntry.getIntKey(), valueEntry.getIntKey(), valueEntry.getDoubleValue());
                }
            }
            
            return this;
        }
    
        /**
         * @param seeds (partial) seed test cases which must appear in the positive combinatorial test suite
         * @return this
         */
        public Builder seeds(Collection<PrimitiveSeed> seeds) {
            seeds(POSITIVE_TESTS_ID, seeds);
            
            return this;
        }
    
        /**
         * @param errorConstraintId the id of the error constraint for which these (partial) seed test cases are needed
         * @param seeds (partial) seed test cases which must appear in the negative combinatorial test suite for the
         *     given error constraint
         * @return this
         */
        public Builder seeds(int errorConstraintId, Collection<PrimitiveSeed> seeds) {
            this.seeds.put(errorConstraintId, new ArrayList<>(seeds));
            
            return this;
        }
    
        /**
         * @param seeds a map from error constraint keys to their respective required seeds. This is a convenience
         *     function for directly added seeds of many error constraints
         * @return this
         */
        public Builder seeds(Int2ObjectMap<? extends Collection<PrimitiveSeed>> seeds) {
            for (Int2ObjectMap.Entry<? extends Collection<PrimitiveSeed>> errorConstraintSeeds
                    : seeds.int2ObjectEntrySet()) {
                
                seeds(errorConstraintSeeds.getIntKey(), errorConstraintSeeds.getValue());
            }
            
            return this;
        }
    
        /**
         * @return a {@link CompleteTestModel} constructed using all the information given to the builder
         */
        public CompleteTestModel build() {
            return new CompleteTestModel(this);
        }
        
    }
    
}
