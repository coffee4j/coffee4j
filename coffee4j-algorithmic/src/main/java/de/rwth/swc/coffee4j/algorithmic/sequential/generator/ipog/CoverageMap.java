package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import java.util.Optional;

public interface CoverageMap {
    boolean mayHaveUncoveredCombinations();

    void markAsCovered(int[] combination);

    int[] computeGainsOfFixedParameter(int[] combination);

    Optional<int[]> getUncoveredCombination();
}
