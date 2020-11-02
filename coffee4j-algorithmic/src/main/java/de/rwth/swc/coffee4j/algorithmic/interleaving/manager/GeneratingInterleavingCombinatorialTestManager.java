package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.Map;
import java.util.Optional;

/**
 * Interface each test manager used for the interleaving generation of error-constraints must implement.
 */
public interface GeneratingInterleavingCombinatorialTestManager extends InterleavingCombinatorialTestManager {
    /**
     * @return set of all found (most-likely) error-combinations
     */
    Map<int[], Class<? extends Throwable>> getMinimalExceptionInducingCombinations();

    /**
     * @param errorConstraintExceptionCausingTestInputs all test inputs that triggered an
     * {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException} during testing.
     *
     * @return next test input to execute or an empty Optional if no further tests are needed for classification
     */
    Optional<int[]> initializeClassification(Map<int[], TestResult> errorConstraintExceptionCausingTestInputs);
}
