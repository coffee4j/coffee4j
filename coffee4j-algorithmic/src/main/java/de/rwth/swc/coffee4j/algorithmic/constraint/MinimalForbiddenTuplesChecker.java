package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.TupleBuilderUtil;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Subclass of {@link ForbiddenTuplesChecker} implementing the general approach introduced in
 * "Constraint Handling In Combinatorial Test Generation Using Forbidden Tuples" by Yu et. al.
 */
public class MinimalForbiddenTuplesChecker extends ForbiddenTuplesChecker {
    /**
     * @param model test model to create a {@link ConstraintChecker} for.
     */
    public MinimalForbiddenTuplesChecker(CompleteTestModel model) {
        super(model);
    }

    /**
     * Copy constructor.
     * @param checker {@link ForbiddenTuplesChecker} to copy.
     */
    public MinimalForbiddenTuplesChecker(ForbiddenTuplesChecker checker) {
        super(checker);
    }

    /**
     * @return returns factory for creating {@link MinimalForbiddenTuplesChecker}.
     */
    public static MinimalForbiddenTuplesCheckerFactory minimalForbiddenTuplesChecker() {
        return new MinimalForbiddenTuplesCheckerFactory();
    }

    @Override
    protected void generateMinimalForbiddenTupleSet() {
        boolean newTuplesDerived;
        Set<IntList> newDerivedTuples;

        do {
            newDerivedTuples = deriveNewTuples();
            newTuplesDerived = !simplifyTupleSet().containsAll(newDerivedTuples);
        } while (newTuplesDerived);
    }

    private Set<IntList> deriveNewTuples() {
        Set<IntList> newTuples = new HashSet<>();

        // try to derive new tuples for each parameter individually
        for (int currentParam = 0; currentParam < numberOfParameters; currentParam++) {
            newTuples.addAll(deriveNewTuplesUsingParameter(currentParam));
        }

        newTuples = removeAlreadyIdentifiedTuples(newTuples);

        if (!newTuples.isEmpty()) {
            updateMultiMaps(newTuples);
        }

        return newTuples;
    }

    private Collection<IntList> deriveNewTuplesUsingParameter(int param) {
        Collection<IntList> usedValuesForParameter = parameterToUsedValuesMap.get(param);

        // not all possible values for current parameter are set -> no deriving possible
        if (usedValuesForParameter.size() < parameterSizes[param]) {
            return Collections.emptySet();
        }

        // stores for each value of the current parameter all forbidden tuples containing this
        // parameter-value combination
        Set<Collection<IntList>> forbiddenTuplesForParameter = usedValuesForParameter
                .stream()
                .map(parameterValuesToTupleMap::get)
                .collect(Collectors.toSet());

        // for every forbidden tuple, a new tuple without the value of the parameter must be created
        // (copies reference the same object)
        Set<Collection<IntList>> forbiddenSet = new HashSet<>();

        for (Collection<IntList> set : forbiddenTuplesForParameter) {
            Set<IntList> newSet = new HashSet<>();
            for (IntList tuple : set) {
                IntList newTuple = new IntArrayList(tuple);
                newTuple.set(param, -1);
                newSet.add(newTuple);
            }

            forbiddenSet.add(newSet);
        }

        return TupleBuilderUtil.buildCartesianProduct(forbiddenSet, numberOfParameters);
    }

    private Set<IntList> removeAlreadyIdentifiedTuples(Set<IntList> newTuples) {
        Collection<IntList> oldTuples = parameterValuesToTupleMap.values();
        return newTuples
                .stream()
                .filter(tuple -> !oldTuples.contains(tuple))
                .collect(Collectors.toSet());
    }

    private Set<IntList> simplifyTupleSet() {
        Set<IntList> removedTuples = new HashSet<>();

        for (IntList combination : minimalForbiddenTuples) {

            List<IntList> subCombinations = new ArrayList<>(minimalForbiddenTuples);
            subCombinations.remove(combination);
            subCombinations = subCombinations
                    .stream()
                    .filter(subCombination -> CombinationUtil.contains(combination.toIntArray(), subCombination.toIntArray()))
                    .collect(Collectors.toList());

            if (!subCombinations.isEmpty()) {
                removedTuples.add(combination);

                for (int param = 0; param < numberOfParameters; param++) {
                    int value = combination.getInt(param);

                    if (value != -1) {
                        IntList key = new IntArrayList(new int[]{param, value});

                        parameterValuesToTupleMap.remove(key, combination);

                        if (!parameterValuesToTupleMap.containsKey(key)) {
                            parameterToUsedValuesMap.remove(param, key);
                        }
                    }
                }
            }
        }

        minimalForbiddenTuples = new HashSet<>(parameterValuesToTupleMap.values());
        return removedTuples;
    }

    @Override
    protected void generateNecessaryForbiddenTupleSet(int[] combination) {
        // No action needed
    }

    @Override
    public void addConstraint(int[] forbiddenTuple) {
        if (Arrays.equals(forbiddenTuple, CombinationUtil.emptyCombination(forbiddenTuple.length))) {
            for (int param = 0; param < numberOfParameters; param++) {
                IntList key = new IntArrayList(new int[]{param, forbiddenTuple[param]});

                parameterValuesToTupleMap.put(key, new IntArrayList(forbiddenTuple));
                parameterToUsedValuesMap.put(param, key);
            }
        }

        for (int param = 0; param < numberOfParameters; param++) {
            if (forbiddenTuple[param]!= -1) {
                IntList key = new IntArrayList(new int[]{param, forbiddenTuple[param]});

                parameterValuesToTupleMap.put(key, new IntArrayList(forbiddenTuple));
                parameterToUsedValuesMap.put(param, key);
            }
        }

        // re-generate minimal tuple set
        generateMinimalForbiddenTupleSet();
    }
}