package de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification;

import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import org.junit.platform.commons.function.Try;

import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link IdentificationPhase} for Interleaving Combinatorial Testing.
 */
public class IdentificationPhase
        extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    
    private final InterleavingCombinatorialTestManager testManager;
    private final ModelConverter converter;
    
    public IdentificationPhase(InterleavingGenerationContext context) {
        super(context);
    
        testManager = context.getTestManager();
        converter = context.getModelConverter();
    }
    
    /**
     * initialize identification phase with previously executed failing test input
     *
     * @param testInput failing test input
     * @param result {@link TestResult} of the failing test input.
     * @return first test input to test for identification. Null, if no input can be generated.
     */
    public Combination initialize(Combination testInput, TestResult result) {
        Optional<int[]> nextTestInputOptional = testManager.initializeIdentification(converter.convertCombination(testInput), result);
        return nextTestInputOptional.map(converter::convertCombination).orElse(null);
    }
    
    /**
     * reinitialize identification phase
     *
     * @return first test input to test for identification. Null, if no input can be generated.
     */
    public Combination reinitialize() {
        Optional<int[]> nextTestInputOptional = testManager.reinitializeIdentification();
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

        // empty, if minimal failure-inducing combination is identified
        if (optNextTestInput.isPresent()) {
            nextTestInput = converter.convertCombination(optNextTestInput.get());
        }

        return nextTestInput;
    }
}
