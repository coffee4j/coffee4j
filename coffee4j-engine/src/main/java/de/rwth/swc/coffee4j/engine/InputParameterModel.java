package de.rwth.swc.coffee4j.engine;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;

/**
 * An interface for restricting the information supplied by an {@link CombinatorialTestModel}. This is important
 * as a {@link FaultCharacterizationAlgorithm}, for example, should only use the
 * constraint checker supplied via the {@link FaultCharacterizationConfiguration}
 * and not construct a new one out of forbidden or error tuples. It therefore must not no these fields exist.
 */
public interface InputParameterModel {
    
    /**
     * @return the desired testing strength
     */
    int getStrength();
    
    /**
     * @return the number of values for each parameter
     */
    int[] getParameterSizes();
    
    /**
     * @return the number of parameters
     */
    default int getNumberOfParameters() {
        return getParameterSizes().length;
    }
    
    /**
     * @param parameterId the id of a parameter in the model
     * @return the number of values this parameter has
     */
    default int getSizeOfParameter(int parameterId) {
        return getParameterSizes()[parameterId];
    }
    
}
