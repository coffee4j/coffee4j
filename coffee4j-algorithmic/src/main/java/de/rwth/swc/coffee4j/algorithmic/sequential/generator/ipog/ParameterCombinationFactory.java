package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;

/**
 * An interface for a factory defining which combinations of parameters need to be tested. In every iteration of the
 * IPOG algorithm, the test suite is expanded to the next parameter defined by a {@link ParameterOrder}. Since all
 * combinations between the last iterations' parameters are already covered, this only needs to return the combinations
 * between the old parameters, which, together with the current parameter, should be covered. This means, that if all
 * 2-value-combinations should be covered for an IPM having 4 parameter numbered 0 through 3, the following combinations
 * would be returned after iterations for parameter 0 and 1 have already been done and the strength is 2:
 * {0}, {1}. Internally, IPOG will append the current parameter to all of these sets. This means that it knows the
 * parameter combinations {0, 2} and {1, 2} should be covered. The combination {0, 1} is not relevant since it is
 * already covered in the last iteration.
 */
public interface ParameterCombinationFactory {
    
    /**
     * Calculates which parameter combinations should be covered in the next step.
     *
     * @param oldParameters the parameters already set by IPOG in horizontal expansion
     * @param nextParameter the next parameter for which the parameter combination between it and the oldParameters
     *     needs to be calculated
     * @param model the model including the strength configuration
     * @return all combinations of old parameter which should be covered together with the next parameter.
     *     If all only all values of the next parameter should be covered (testing strength of one), a
     *     list with a single empty set must be returned. If no values in the next parameters need
     *     to be covered (testing strength zero), an empty list must be returned.
     */
    List<IntSet> create(int[] oldParameters, int nextParameter, TestModel model);
    
}
