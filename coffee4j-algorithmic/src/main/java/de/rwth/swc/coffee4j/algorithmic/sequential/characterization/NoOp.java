package de.rwth.swc.coffee4j.algorithmic.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The NoOp, short for No Operation, algorithm is the simplest possible fault characterization algorithm.
 * It just translates every failure in the initial covering array to a failure inducing consisting out
 * of the whole test case; i.e. the failing test case (0, 0, 1, 1) is directly returned as a result
 * of fault characterization. No additional test cases will be generated.
 * <p>
 * The inclusion of this algorithm in the evaluation provides a measure of the overhead
 * that other fault characterization algorithms introduce.
 */
public class NoOp implements FaultCharacterizationAlgorithm {

    private Map<int[], TestResult> testResults;

    /**
     * Empty constructor.
     *
     * This constructor is only present so that it matches the signature of {@link FaultCharacterizationAlgorithmFactory}.
     *
     * @param configuration the configuration will be ignored.
     */
    public NoOp(FaultCharacterizationConfiguration configuration) {
    }


    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        this.testResults = testResults;
        return Collections.emptyList();
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return testResults.entrySet().stream().filter(entry -> entry.getValue().isUnsuccessful())
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
