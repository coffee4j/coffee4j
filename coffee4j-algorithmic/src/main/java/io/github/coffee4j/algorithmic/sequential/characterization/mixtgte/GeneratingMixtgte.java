package io.github.coffee4j.algorithmic.sequential.characterization.mixtgte;

import io.github.coffee4j.algorithmic.interleaving.identification.CombinationType;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import io.github.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sub-class extending {@link Mixtgte} to enable the generation of error-constraints.
 */
public class GeneratingMixtgte extends Mixtgte implements GeneratingFaultCharacterizationAlgorithm {
    /**
     * @param config configuration containing the test model this algorithm needs.
     */
    public GeneratingMixtgte(FaultCharacterizationConfiguration config) {
        super(config.model());
    }

    /**
     * @return a factory always returning new instances of the MixTgTe algorithm
     */
    public static FaultCharacterizationAlgorithmFactory generatingMixtgte() {
        return GeneratingMixtgte::new;
    }

    @Override
    public Set<int[]> computeExceptionInducingCombinations() {
        return isolatedMinInducingCombinations
                .entrySet()
                .stream()
                .filter(inducingCombination -> inducingCombination.getValue() == CombinationType.EXCEPTION_INDUCING)
                .map(inducingCombination -> inducingCombination.getKey().toIntArray())
                .collect(Collectors.toSet());
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return isolatedMinInducingCombinations
                .entrySet()
                .stream()
                .filter(inducingCombination -> inducingCombination.getValue() == CombinationType.FAILURE_INDUCING)
                .map(inducingCombination -> inducingCombination.getKey().toIntArray())
                .collect(Collectors.toList());
    }
}
