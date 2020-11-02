package de.rwth.swc.coffee4j.algorithmic.constraint;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class to provide general functionality for both approaches depicted in
 * "Constraint Handling In Combinatorial Test Generation Using Forbidden Tuples" by Yu et. al.
 */
public abstract class ForbiddenTuplesChecker implements ConstraintChecker {
    
    private final CompleteTestModel model;
    private Set<IntList> initialForbiddenTuples;
    Set<IntList> minimalForbiddenTuples;
    final int[] parameterSizes;
    final int numberOfParameters;
    
    final Multimap<IntList, IntList> parameterValuesToTupleMap = MultimapBuilder.hashKeys().hashSetValues().build();
    final Multimap<Integer, IntList> parameterToUsedValuesMap;

    /**
     * @param model test model to create a {@link ConstraintChecker} for.
     */
    ForbiddenTuplesChecker(CompleteTestModel model) {
        this.model = model;
        parameterSizes = model.getParameterSizes();
        numberOfParameters = model.getNumberOfParameters();

        IntList parameters = new IntArrayList(parameterSizes);
        parameters.sort(null);
        parameterToUsedValuesMap = MultimapBuilder
                .hashKeys(numberOfParameters)
                .hashSetValues(parameters.getInt(numberOfParameters - 1))
                .build();

        generateInitialForbiddenTupleSet();
        generateMinimalForbiddenTupleSet();
    }

    /**
     * Copy constructor.
     * @param checker {@link ForbiddenTuplesChecker} to copy.
     */
    ForbiddenTuplesChecker(ForbiddenTuplesChecker checker) {
        this.model = checker.model;
        this.initialForbiddenTuples = new HashSet<>(checker.initialForbiddenTuples);
        this.minimalForbiddenTuples = new HashSet<>(checker.minimalForbiddenTuples);
        this.parameterSizes = Arrays.copyOf(checker.parameterSizes, checker.parameterSizes.length);
        this.numberOfParameters = checker.numberOfParameters;

        IntList parameters = new IntArrayList(parameterSizes);
        parameters.sort(null);

        parameterToUsedValuesMap = MultimapBuilder
                .hashKeys(numberOfParameters)
                .hashSetValues(parameters.getInt(numberOfParameters - 1))
                .build();
    }

    /**
     * template method that can be implemented to provide the general approach depicted in
     * "Constraint Handling In Combinatorial Test Generation Using Forbidden Tuples".
     */
    protected abstract void generateMinimalForbiddenTupleSet();

    /**
     * template method that can be used to provide the on-demand approach depicted in
     * "Constraint Handling In Combinatorial Test Generation Using Forbidden Tuples".
     *
     * @param combination combination for which the set of minimal forbidden tuples is created.
     */
    protected abstract void generateNecessaryForbiddenTupleSet(int[] combination);

    private void generateInitialForbiddenTupleSet() {
        int[] emptyCombination = CombinationUtil.emptyCombination(numberOfParameters);

        Set<IntList> parameterValuePairs;

        Set<TupleList> forbiddenTupleLists = new HashSet<>(model.getExclusionTupleLists());
        forbiddenTupleLists.addAll(model.getErrorTupleLists());

        for (TupleList list : forbiddenTupleLists) {
            int[] involvedParameters = list.getInvolvedParameters();

            for (int[] values : list.getTuples()) {
                IntList combination = new IntArrayList(emptyCombination);
                parameterValuePairs = new HashSet<>();

                for (int index = 0; index < involvedParameters.length; index++) {
                    IntList pair = new IntArrayList(new int[]{involvedParameters[index], values[index]});

                    parameterToUsedValuesMap.put(involvedParameters[index], pair);
                    parameterValuePairs.add(pair);
                    combination.set(involvedParameters[index], values[index]);
                }

                parameterValuePairs.forEach(pair -> parameterValuesToTupleMap.put(pair, combination));
            }
        }

        initialForbiddenTuples = new HashSet<>(parameterValuesToTupleMap.values());
        minimalForbiddenTuples = new HashSet<>(initialForbiddenTuples);
    }

    void updateMultiMaps(Set<IntList> newTuples) {
        for (IntList tuple : newTuples) {
            for (int param = 0; param < numberOfParameters; param++) {
                if (tuple.getInt(param) != -1) {
                    IntList key = new IntArrayList(new int[]{param, tuple.getInt(param)});

                    parameterValuesToTupleMap.put(key, tuple);
                    parameterToUsedValuesMap.put(param, key);
                }
            }
        }

        minimalForbiddenTuples = new HashSet<>(parameterValuesToTupleMap.values());
    }

    public Set<IntList> getMinimalForbiddenTuples() {
        return minimalForbiddenTuples;
    }

    public Set<IntList> getInitialForbiddenTuples() {
        return initialForbiddenTuples;
    }

    @Override
    public boolean isValid(int[] combination) {
        generateNecessaryForbiddenTupleSet(combination);

        for (IntList forbiddenTuple : minimalForbiddenTuples) {
            if (CombinationUtil.contains(combination, forbiddenTuple.toIntArray())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isExtensionValid(int[] combination, int... parameterValues) {
        Preconditions.check(parameterValues.length % 2 == 0);

        int[] newCombination = CombinationUtil.emptyCombination(numberOfParameters);

        System.arraycopy(combination, 0, newCombination, 0, combination.length);

        for (int i = 0; i < parameterValues.length - 1; i += 2) {
            newCombination[parameterValues[i]] = parameterValues[i + 1];
        }

        return isValid(newCombination);
    }

    @Override
    public boolean isDualValid(int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);

        int[] newCombination = CombinationUtil.emptyCombination(numberOfParameters);

        for (int i = 0; i < parameters.length; i++) {
            newCombination[parameters[i]] = values[i];
        }

        return isValid(newCombination);
    }
}
