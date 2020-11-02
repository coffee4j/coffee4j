package de.rwth.swc.coffee4j.algorithmic.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;

import java.util.Collection;
import java.util.List;

/**
 * An algorithm which orders test inputs according to some internal prioritization criterion.
 *
 * <p>This can be used together with a {@link TestInputGroupGenerator} is used which does not take any prioritization
 * information into account. The generator is then only responsible for building test suites which satisfy the defined
 * combinatorial coverage criterion, and a {@link TestInputPrioritizer} is used in a following phase to order the
 * generated test inputs.
 *
 * <p>No implementation should use information which is not given through the {@link TestModel}. If further information
 * are required they should be included into the {@link TestModel} so that all algorithms can profit from them.
 *
 * <p>Implementations must at least be immutable but should also be stateless (no fields) with the possible exception
 * of configuration parameters.
 */
public interface TestInputPrioritizer {
    
    /**
     * Prioritizes the given test cases according to some internal prioritization criterion.
     * This criterion should be explained in the Javadoc of the implementation.
     *
     * @param testCases the test cases to prioritize. Must not be {@code null}
     * @param model the model which includes additional prioritization information. Must not be {@code null}
     * @return the same test cases as in the input, but ordered according to the prioritization criteria. This must
     *     NEVER include additional/remove existing test cases! Otherwise the correct behaviour of any calling class
     *     can not be guaranteed. Consequently, {@code null} is also never a valid return value
     */
    List<int[]> prioritize(Collection<int[]> testCases, TestModel model);
    
}
