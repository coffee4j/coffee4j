package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A parameter order based on the normal parameter order and the testing strength. The initial parameters are the first
 * t parameters if t is the testing strength, and all other parameters are returned separately.
 * This means parameters 1, 2, 3, 4, 5 get split into initial 1, 2 and remaining 3, 4, 5 if testing strength = 2.
 */
final  class StrengthBasedParameterOrder implements ParameterOrder {

    @Override
    public int[] getInitialParameters(Int2IntMap parameters, int strength) {
        Preconditions.notNull(parameters);
        Preconditions.check(strength >= 0);

        if (strength == 0) {
            return new int[0];
        }

        return IntStream.range(0, parameters.size())
                .boxed()
                .sorted((first, second) -> Integer.compare(parameters.get((int) second), parameters.get((int) first)))
                .limit(strength)
                .mapToInt(i -> i)
                .toArray();
    }

    @Override
    public int[] getRemainingParameters(Int2IntMap parameters, int strength) {
        return IntStream.range(0, parameters.size())
                .boxed()
                .sorted((first, second) -> Integer.compare(parameters.get((int) second), parameters.get((int) first)))
                .skip(strength)
                .mapToInt(i -> i)
                .toArray();
    }
}

