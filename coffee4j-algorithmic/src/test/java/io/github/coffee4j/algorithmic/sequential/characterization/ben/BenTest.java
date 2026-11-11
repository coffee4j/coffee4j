package io.github.coffee4j.algorithmic.sequential.characterization.ben;

import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmTest;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;

class BenTest implements FaultCharacterizationAlgorithmTest {
    
    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new Ben(configuration, 10, 50);
    }
    
}
