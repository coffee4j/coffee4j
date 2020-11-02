package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;

/**
 * Defines the order in which parameters should be covered in IPOG. The order can be relevant for performance and test
 * suite size. Additionally, a correct combination of order and {@link ParameterCombinationFactory} can be important.
 */
public interface ParameterOrder {
    
    /**
     * All combinations which should be used in the first initial step of IPOG. In this step the cartesian product of
     * all returned parameters is calculated. This means that the strength may need to be considered.
     *
     * @param model the test configuration which includes the strength configuration
     * @return all parameters which should be constructed using the cartesian product. This explicitly means that
     * these parameters are always in oldParameters in a {@link ParameterCombinationFactory}
     */
    int[] getInitialParameters(TestModel model);
    
    /**
     * The order of all remaining parameters. The parameter which should be expanded in the first horizontal expansion
     * should be at the first place in the array (index 0).
     *
     * @param model the test configuration which includes the strength configuration
     * @return all parameters which were not already returned by {@link #getInitialParameters(TestModel)} in any order
     */
    int[] getRemainingParameters(TestModel model);
    
}
