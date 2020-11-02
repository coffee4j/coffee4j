package de.rwth.swc.coffee4j.algorithmic.sequential.characterization.mixtgte;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmTest;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;


public class MixtgteTest implements FaultCharacterizationAlgorithmTest {
    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new Mixtgte(configuration);
    }
}