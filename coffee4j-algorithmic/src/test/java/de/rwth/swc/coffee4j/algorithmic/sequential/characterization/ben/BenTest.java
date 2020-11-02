package de.rwth.swc.coffee4j.algorithmic.sequential.characterization.ben;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmTest;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;

class BenTest implements FaultCharacterizationAlgorithmTest {
    
    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new Ben(configuration, 10, 50);
    }
    
}
