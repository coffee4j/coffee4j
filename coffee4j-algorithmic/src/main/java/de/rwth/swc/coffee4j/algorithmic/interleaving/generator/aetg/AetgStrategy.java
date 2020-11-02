package de.rwth.swc.coffee4j.algorithmic.interleaving.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.ForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.OptimalValue;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Implementation of AETGSat ("Constructing Interaction Test Suites for Highly-Configurable Systems in the Presence
 * of Constraints: A Greedy Approach").
 */
public class AetgStrategy implements TestInputGenerationStrategy {
    
    private final CoverageMap coverageMap;
    private final ConstraintChecker checker;
    private final CompleteTestModel testModel;

    // number of different test input candidates to generate and to select the next test input from
    // according to "The AETG system: An Approach to Testing Based on Combinatorial Design", 50 is a reasonable value
    private static final int NUMBER_OF_DIFFERENT_TEST_INPUTS = 25;

    private final int numberOfParameters;
    private final int numberOfParameterValuePairs;

    // List of all parameters
    private final IntList parameters;
    private final Random seed = ThreadLocalRandom.current();

    private AetgStrategy(TestInputGenerationConfiguration configuration) {
        this.coverageMap = configuration.getCoverageMap();
        this.checker = configuration.getConstraintChecker();
        this.testModel = configuration.getTestModel();

        numberOfParameters = testModel.getNumberOfParameters();
        numberOfParameterValuePairs = Arrays.stream(testModel.getParameterSizes()).reduce(Integer::sum).orElse(0);

        parameters = new IntArrayList(numberOfParameters);
        IntStream.range(0, numberOfParameters).forEach(parameters::add);
    }

    /**
     * @return Factory creating AETG-Strategy
     */
    public static TestInputGenerationStrategyFactory aetgStrategy() {
        return AetgStrategy::new;
    }

    @Override
    public Optional<int[]> generateNextTestInput() {
        // if all tuples are covered, return an empty Optional
        if (coverageMap.allCombinationsCovered()) {
            return Optional.empty();
        }

        List<int[]> candidateTestInputs = new ArrayList<>(NUMBER_OF_DIFFERENT_TEST_INPUTS);
        ConstraintChecker tempChecker = new MinimalForbiddenTuplesChecker((ForbiddenTuplesChecker) this.checker);
        CoverageMap tempCoverageMap = new CoverageMap(this.coverageMap, tempChecker);

        ExecutorService candidateBuilderService = Executors.newCachedThreadPool();

        // create numberOfDifferentTestInputs candidate test inputs
        // afterwards, choose test input that covers the most uncovered t-tuples (if any exists)
        for (int iteration = 0; iteration < NUMBER_OF_DIFFERENT_TEST_INPUTS; iteration++) {
            Runnable task = () -> {
                Optional<int[]> possibleTestInput = generatePossibleTestInput();
                possibleTestInput.ifPresent(candidateTestInputs::add);
            };

            candidateBuilderService.execute(task);
        }

        candidateBuilderService.shutdown();
        try {
            candidateBuilderService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Coffee4JException(e, "Creating candidate test inputs took too long!");
        }

        if (candidateTestInputs.isEmpty()) {
            return Optional.empty();
        } else if (candidateTestInputs.size() == 1) {
            return Optional.of(candidateTestInputs.get(0));
        } else {
            return Optional.of(Collections.max(candidateTestInputs, Comparator.comparing(tempCoverageMap::getNumberOfCoveredCombinationsByTestInput)));
        }
    }

    private Optional<int[]> generatePossibleTestInput() {
        final IntList tempParameters = new IntArrayList(this.parameters);

        int[] testInput = CombinationUtil.emptyCombination(numberOfParameters);
        boolean isSatisfying = false;

        Set<ParameterValuePair> forbiddenPairs = new HashSet<>();
        ParameterValuePair firstParameterValuePair = null;

        while (!isSatisfying) {
            if (forbiddenPairs.size() >= numberOfParameterValuePairs) {
                return Optional.empty();
            }

            // select first valid parameter-value-pair contained in most uncovered t-tuples
            firstParameterValuePair = getFirstParameterAndValue(forbiddenPairs);
            isSatisfying = checker.isExtensionValid(testInput, firstParameterValuePair.getParameter(), firstParameterValuePair.getValue());

            if (!isSatisfying) {
                forbiddenPairs.add(firstParameterValuePair);
            }
        }

        testInput[firstParameterValuePair.getParameter()] = firstParameterValuePair.getValue();

        Collections.shuffle(tempParameters, seed);

        OptimalValue optimalValueFinder = new OptimalValue();

        // iterate randomly over all remaining tempParameters
        for (int parameter : tempParameters) {
            if (testInput[parameter] == -1) {
                // search for a value for the given parameter and partial test input to maximize number of covered t-tuples
                Optional<ParameterValuePair>  optimalValue = optimalValueFinder.forParameter(parameter, testModel.getParameterSize(parameter), new IntArraySet(), testInput, coverageMap, checker);

                // no valid value for given parameter and partial test input available
                if (!optimalValue.isPresent()) {
                    return Optional.empty();
                }

                testInput[optimalValue.get().getParameter()] = optimalValue.get().getValue();
            }
        }

        return Optional.of(testInput);
    }

    private ParameterValuePair getFirstParameterAndValue(Set<ParameterValuePair> forbiddenPairs) {
        return coverageMap.getParameterValuePairCoveringMostCombinations(forbiddenPairs);
    }

    @Override
    public String toString() {
        return "AetgStrategy";
    }
}
