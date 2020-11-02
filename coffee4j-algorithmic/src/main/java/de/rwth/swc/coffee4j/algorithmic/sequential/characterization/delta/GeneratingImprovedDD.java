package de.rwth.swc.coffee4j.algorithmic.sequential.characterization.delta;

import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneratingImprovedDD extends ImprovedDeltaDebugging implements GeneratingFaultCharacterizationAlgorithm {
    /**
     * Creates a new Improved Delta Debugging algorithm for the given configuration. The ConstraintsChecker is ignored.
     *
     * @param configuration the configuration for the algorithm
     */
    public GeneratingImprovedDD(FaultCharacterizationConfiguration configuration) {
        super(configuration);
    }

    /**
     * @return returns a factory for this class.
     */
    public static FaultCharacterizationAlgorithmFactory generatingImprovedDD() { return GeneratingImprovedDD::new; }

    @Override
    public Set<int[]> computeExceptionInducingCombinations() {
        return inducingCombinations
                .entrySet()
                .stream()
                .filter(inducingCombination -> inducingCombination.getValue() == CombinationType.EXCEPTION_INDUCING)
                .map(inducingCombination -> inducingCombination.getKey().toIntArray())
                .collect(Collectors.toSet());
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return inducingCombinations
                .entrySet()
                .stream()
                .filter(inducingCombination -> inducingCombination.getValue() == CombinationType.FAILURE_INDUCING)
                .map(inducingCombination -> inducingCombination.getKey().toIntArray())
                .collect(Collectors.toList());
    }
}
