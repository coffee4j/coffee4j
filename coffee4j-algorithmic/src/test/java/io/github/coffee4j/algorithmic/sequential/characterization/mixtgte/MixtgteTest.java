package io.github.coffee4j.algorithmic.sequential.characterization.mixtgte;

import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmTest;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;


public class MixtgteTest implements FaultCharacterizationAlgorithmTest {
    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new Mixtgte(configuration);
    }
}