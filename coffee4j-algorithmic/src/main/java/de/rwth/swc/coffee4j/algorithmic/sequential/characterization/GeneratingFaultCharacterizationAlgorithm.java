package de.rwth.swc.coffee4j.algorithmic.sequential.characterization;

import java.util.Set;

/**
 * Interface each sequential FCA used for the identification of exception-inducing combinations must implement.
 */
public interface GeneratingFaultCharacterizationAlgorithm extends FaultCharacterizationAlgorithm {
    
    Set<int[]> computeExceptionInducingCombinations();
    
}
