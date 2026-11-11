package io.github.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.util.ArrayUtil;
import io.github.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;
import java.util.Optional;

import static io.github.coffee4j.algorithmic.util.Combinator.computeNegativeParameterCombinations;
import static io.github.coffee4j.algorithmic.util.Combinator.computeParameterCombinations;

final class NegativeStrengthBasedParameterCombinationFactory implements ParameterCombinationFactory {

    private final int[] negativeParameters;
    private final int strengthA;

    NegativeStrengthBasedParameterCombinationFactory(TupleList forbiddenTuples, int strengthA) {
        Preconditions.notNull(forbiddenTuples);
        Preconditions.check(strengthA > 0
                && strengthA <= forbiddenTuples.getInvolvedParameters().length);

        this.negativeParameters = Preconditions.notNull(forbiddenTuples).getInvolvedParameters();
        this.strengthA = strengthA;
    }

    @Override
    public Optional<List<IntSet>> create(int[] oldParameters, int nextParameter, int strengthB) {
        if(ArrayUtil.contains(negativeParameters, nextParameter)) {
            return Optional.of(computeParameterCombinations(oldParameters, strengthA - 1));
        } else if(strengthB > 0) {
            return Optional.of(computeNegativeParameterCombinations(oldParameters, negativeParameters, strengthA, strengthB - 1));
        } else {
            return Optional.empty();
        }
    }
}

