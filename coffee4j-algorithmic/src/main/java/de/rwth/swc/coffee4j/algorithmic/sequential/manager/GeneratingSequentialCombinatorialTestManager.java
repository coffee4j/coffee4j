package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.Map;
import java.util.Optional;

/**
 * Interface each test manager used for the sequential generation of error-constraints must implement.
 */
public interface GeneratingSequentialCombinatorialTestManager extends SequentialCombinatorialTestManager {

    /**
     * @param errorConstraintExceptionCausingTestInputs all test inputs that triggered an
     *                                                  {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException} during testing.
     * @return next test input to execute or an empty Optional if no further tests are needed for classification.
     */
    Optional<int[]> initializeClassification(Map<int[], TestResult> errorConstraintExceptionCausingTestInputs);

    /**
     * @param testInput  previously executed test input.
     * @param testResult result of previously executed test input.
     * @return next test input to execute or an empty Optional if no further tests are needed for classification.
     */
    Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult testResult);

}
