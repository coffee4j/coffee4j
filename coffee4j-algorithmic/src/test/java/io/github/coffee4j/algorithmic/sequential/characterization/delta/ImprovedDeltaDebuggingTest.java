package io.github.coffee4j.algorithmic.sequential.characterization.delta;

import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmTest;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;

class ImprovedDeltaDebuggingTest implements FaultCharacterizationAlgorithmTest {
    
    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new ImprovedDeltaDebugging(configuration);
    }
}
