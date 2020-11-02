package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.ArrayUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.Arrays;
import java.util.stream.IntStream;

final class NegativeStrengthBasedParameterOrder implements ParameterOrder {

    private final int[] negativeParameters;
    private final int strengthA;

    NegativeStrengthBasedParameterOrder(TupleList forbiddenTuples, int strengthA) {
        Preconditions.notNull(forbiddenTuples);
        Preconditions.check(strengthA > 0
                && strengthA <= forbiddenTuples.getInvolvedParameters().length);

        this.negativeParameters = forbiddenTuples.getInvolvedParameters();
        this.strengthA = strengthA;
    }

    @Override
    public int[] getInitialParameters(Int2IntMap parameters, int strength) {
        return Arrays.stream(negativeParameters)
                .boxed()
                .sorted((first, second) -> Integer.compare(parameters.get((int) second), parameters.get((int) first)))
                .limit(strengthA)
                .mapToInt(i -> i)
                .toArray();
    }

    @Override
    public int[] getRemainingParameters(Int2IntMap parameters, int strength) {
        final int[] remainingParametersOfTupleList = Arrays.stream(negativeParameters)
                .boxed()
                .sorted((first, second) -> Integer.compare(parameters.get((int) second), parameters.get((int) first)))
                .skip(strengthA)
                .mapToInt(i -> i)
                .toArray();

        final int [] allOtherParameters = Arrays.stream(ArrayUtil.exclude(parameters.keySet().toIntArray(), negativeParameters))
                .boxed()
                .sorted((first, second) -> Integer.compare(parameters.get((int) second), parameters.get((int) first)))
                .mapToInt(i -> i)
                .toArray();

        final int[] remainingParameters = new int[remainingParametersOfTupleList.length + allOtherParameters.length];
        System.arraycopy(remainingParametersOfTupleList, 0, remainingParameters, 0, remainingParametersOfTupleList.length);
        System.arraycopy(allOtherParameters, 0, remainingParameters, remainingParametersOfTupleList.length, allOtherParameters.length);

        return remainingParameters;
    }
}
