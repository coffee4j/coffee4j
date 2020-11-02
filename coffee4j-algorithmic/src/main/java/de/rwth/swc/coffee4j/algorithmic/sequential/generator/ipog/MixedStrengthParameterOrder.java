package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * A parameter order based on the normal parameter order and the testing strength. The initial parameters are the first
 * t parameters if t is the testing strength, and all other parameters are returned separately.
 * This means parameters 1, 2, 3, 4, 5 get split into initial 1, 2 and remaining 3, 4, 5 if testing strength = 2.
 */
public class MixedStrengthParameterOrder implements ParameterOrder {
    
    @Override
    public int[] getInitialParameters(TestModel model) {
        return getBiggestCartesianProductSet(model);
    }
    
    @Override
    public int[] getRemainingParameters(TestModel model) {
        final IntSet biggestCartesianProductSet = new IntOpenHashSet(getBiggestCartesianProductSet(model));
        return IntStream.range(0, model.getNumberOfParameters())
                .filter(parameter -> !biggestCartesianProductSet.contains(parameter))
                .toArray();
    }
    
    private int[] getBiggestCartesianProductSet(TestModel model) {
        if (model.getMixedStrengthGroups().isEmpty()) {
            if (model.getDefaultTestingStrength() == 0) {
                return new int[0];
            } else {
                return IntStream.range(0, model.getDefaultTestingStrength()).toArray();
            }
        } else {
            final IntSet biggestGroup = model.getMixedStrengthGroups().stream()
                    .map(PrimitiveStrengthGroup::getParameters)
                    .max(Comparator.comparingInt(IntSet::size))
                    .orElseThrow(() -> new IllegalStateException("There should be a group as asserted before"));
            return biggestGroup.toIntArray();
        }
    }
    
}
