package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import java.util.Optional;

interface CoverageMap {
    boolean mayHaveUncoveredCombinations();

    void markAsCovered(int[] combination);

    int[] computeGainsOfFixedParameter(int[] combination);

    Optional<int[]> getUncoveredCombination();
}

