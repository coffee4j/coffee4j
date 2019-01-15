package de.rwth.swc.coffee4j.engine;

import de.rwth.swc.coffee4j.engine.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class containing all important information needed for one combinatorial test. This includes the desired testing
 * strength, the parameters and forbidden/error constraints.
 * Forbidden constraints should never appear in any test input as they just cannot be executed, while error
 * constraints should lead to validation or other kinds of errors in an application and should therefore be tested
 */
public final class CombinatorialTestModel implements InputParameterModel {
    
    private final int strength;
    private final int[] parameterSizes;
    private final List<TupleList> forbiddenTupleLists;
    private final List<TupleList> errorTupleLists;
    
    /**
     * Defines a new model without any constraints.
     *
     * @param strength       the desired testing strength. Must be equal to or greater than one and at most the number of parameters
     * @param parameterSizes the sizes of all parameters. [2, 3, 5] means the first parameter has 2 values, the second
     *                       one 3 and the last one 5. All parameters need to have at least two values. Must not be
     *                       {@code null}
     */
    public CombinatorialTestModel(int strength, int[] parameterSizes) {
        this(strength, parameterSizes, Collections.emptyList(), Collections.emptyList());
    }
    
    /**
     * Defines a new model with just forbidden constraints but no error constraints.
     *
     * @param strength            the desired testing strength. Must be equal to or greater than one and at most the number of parameters
     * @param parameterSizes      the sizes of all parameters. [2, 3, 5] means the first parameter has 2 values, the second
     *                            one 3 and the last one 5. All parameters need to have at least two values. Must not be
     *                            {@code null}
     * @param forbiddenTupleLists all forbidden combinations. Must not be {@code null}. The sizes of the corresponding
     *                            parameters must be respected
     */
    public CombinatorialTestModel(int strength, int[] parameterSizes, Collection<TupleList> forbiddenTupleLists) {
        this(strength, parameterSizes, forbiddenTupleLists, Collections.emptyList());
    }
    
    /**
     * @param strength            the desired testing strength. Must be equal to or greater than one and at most the number of parameters
     * @param parameterSizes      the sizes of all parameters. [2, 3, 5] means the first parameter has 2 values, the second
     *                            one 3 and the last one 5. All parameters need to have at least two values. Must not be
     *                            {@code null}
     * @param forbiddenTupleLists all forbidden combinations. Must not be {@code null}. The sizes of the corresponding
     *                            parameters must be respected. Must not contain duplicate ids
     * @param errorTupleLists     all error combinations. Must not be {@code null}. The sizes of the corresponding
     *                            parameters must be respected. Must not contain duplicate ids or ids contained in
     *                            the forbiddenTupleLists
     */
    public CombinatorialTestModel(int strength, int[] parameterSizes, Collection<TupleList> forbiddenTupleLists, Collection<TupleList> errorTupleLists) {
        Preconditions.notNull(parameterSizes);
        Preconditions.check(strength >= 0);
        Preconditions.check(strength <= parameterSizes.length);
        Preconditions.notNull(forbiddenTupleLists);
        Preconditions.notNull(errorTupleLists);
        
        checkParameterSizes(parameterSizes);
        checkForbiddenTupleIdentifier(parameterSizes, forbiddenTupleLists);
        checkForbiddenTupleIdentifier(parameterSizes, errorTupleLists);
        checkTuplesListIds(forbiddenTupleLists, errorTupleLists);
        
        this.strength = strength;
        this.parameterSizes = Arrays.copyOf(parameterSizes, parameterSizes.length);
        this.forbiddenTupleLists = new ArrayList<>(forbiddenTupleLists);
        this.errorTupleLists = new ArrayList<>(errorTupleLists);
    }
    
    private static void checkTuplesListIds(Collection<TupleList> forbiddenTupleLists, Collection<TupleList> errorTupleLists) {
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
    
    private static void checkForbiddenTupleIdentifier(int[] parameterSizes, Collection<TupleList> forbiddenTupleLists) {
        for (TupleList forbiddenTuples : forbiddenTupleLists) {
            for (int parameter : forbiddenTuples.getInvolvedParameters()) {
                Preconditions.check(parameter >= 0);
                Preconditions.check(parameter < parameterSizes.length);
            }
        }
    }
    
    /**
     * @return the testing strength
     */
    @Override
    public int getStrength() {
        return strength;
    }
    
    /**
     * @return the number of values for each parameter
     */
    @Override
    public int[] getParameterSizes() {
        return Arrays.copyOf(parameterSizes, parameterSizes.length);
    }
    
    /**
     * @return all forbidden combinations. Tests containing these combinations cannot be executed
     */
    public List<TupleList> getForbiddenTupleLists() {
        return Collections.unmodifiableList(forbiddenTupleLists);
    }
    
    /**
     * @return all error combinations. Tests containing these combinations can be executed but should cause an error
     */
    public List<TupleList> getErrorTupleLists() {
        return Collections.unmodifiableList(errorTupleLists);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(strength, parameterSizes, forbiddenTupleLists, errorTupleLists);
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final CombinatorialTestModel other = (CombinatorialTestModel) object;
        return strength == other.strength && Arrays.equals(parameterSizes, other.parameterSizes) && Objects.equals(forbiddenTupleLists, other.forbiddenTupleLists) && Objects.equals(errorTupleLists, other.errorTupleLists);
    }
    
    @Override
    public String toString() {
        return "CombinatorialTestModel{" + "strength=" + strength + ", parameterSizes=" + Arrays.toString(parameterSizes) + ", forbiddenTupleLists=" + forbiddenTupleLists + ", errorTupleLists=" + errorTupleLists + '}';
    }
    
}
