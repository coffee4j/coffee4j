package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;
import java.util.Optional;

import static de.rwth.swc.coffee4j.algorithmic.util.Combinator.computeParameterCombinations;

/**
 * The "normal" strategy for covering all t-value-combinations for combinatorial test with testing strength t.
 * This means that all combinations of previous parameters with strength t - 1 are returned, as the current parameter
 * is added to every combination as described in {@link ParameterCombinationFactory}.
 */
final class StrengthBasedParameterCombinationFactory implements ParameterCombinationFactory {

    @Override
    public Optional<List<IntSet>> create(int[] oldParameters, int nextParameter, int strength) {
        Preconditions.notNull(oldParameters);

        if(strength > 0) {
            return Optional.of(computeParameterCombinations(oldParameters, strength - 1));
        } else {
            return Optional.empty();
        }
    }
}

