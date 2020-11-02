package de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking;

import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import org.junit.platform.commons.function.Try;

import java.util.Map;
import java.util.Optional;

public class CheckingPhase
        extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    
    private final InterleavingCombinatorialTestManager testManager;
    private final ModelConverter converter;
    
    public CheckingPhase(InterleavingGenerationContext context) {
        super(context);
    
        testManager = context.getTestManager();
        converter = context.getModelConverter();
    }
    
    /**
     * @return true iff checked combination is failure-inducing.
     */
    public boolean failureInducingCombinationsFound() {
        return testManager.combinationIdentified();
    }
    
    /**
     * initialize checking phase with possibly failure-inducing combination
     *
     * @return first test input to test for feedback checking. Null, if no input can be generated.
     */
    public Combination initialize() {
        Optional<int[]> nextTestInputOptional = testManager.initializeFeedbackChecking();
        return nextTestInputOptional.map(converter::convertCombination).orElse(null);
    }

    @Override
    public Combination execute(Map<Combination, TestResult> previouslyExecutedTests) {
        Preconditions.notNull(previouslyExecutedTests);
        Preconditions.check(Try.call(() -> !previouslyExecutedTests.containsKey(null)).toOptional().orElse(true));
        Preconditions.check(Try.call(() -> !previouslyExecutedTests.containsValue(null)).toOptional().orElse(true));

        // if previously executed test is passing -> update coverage
        for (Map.Entry<Combination, TestResult> test : previouslyExecutedTests.entrySet()) {
            if (test.getValue().isSuccessful()) {
                testManager.updateCoverage(converter.convertCombination(test.getKey()));
            }
        }

        // assumption: previouslyExecutedTests contains exactly one element
        Optional<Map.Entry<Combination, TestResult>> optEntry = previouslyExecutedTests.entrySet().stream().findFirst();

        Map.Entry<Combination, TestResult> previouslyExecutedTest = null;

        if (optEntry.isPresent()) {
            previouslyExecutedTest = optEntry.get();
        }

        // Generation of next test input
        assert previouslyExecutedTest != null;
        Optional<int[]> optNextTestInput = testManager.generateNextTestInput(
                converter.convertCombination(previouslyExecutedTest.getKey()),
                previouslyExecutedTest.getValue());

        Combination nextTestInput = null;

        // empty, if minimal failure-inducing combination is not failure-inducing
        // or maximal number of iterations is reached
        if (optNextTestInput.isPresent()) {
            nextTestInput = converter.convertCombination(optNextTestInput.get());
        }

        return nextTestInput;
    }
    
}
