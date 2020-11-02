package de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.GeneratingInterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import org.junit.platform.commons.function.Try;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract Class for Classification Phase providing general functionality.
 */
public class InterleavingClassificationPhase extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    
    protected final GeneratingInterleavingCombinatorialTestManager testManager;
    protected final ModelConverter modelConverter;

    public InterleavingClassificationPhase(InterleavingGenerationContext context) {
        super(context);

        Preconditions.check(context.getTestManager() instanceof GeneratingInterleavingCombinatorialTestManager);

        testManager = (GeneratingInterleavingCombinatorialTestManager) context.getTestManager();
        modelConverter = context.getModelConverter();
    }

    /**
     * @param errorConstraintExceptionCausingTestInputs all test inputs that triggered an
     * {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException} during the identification.
     *
     * @return first test input needed for classification if needed.
     */
    public Combination initialize(Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs) {
        Optional<int[]> optNextTestInput = testManager.initializeClassification(
                errorConstraintExceptionCausingTestInputs
                        .entrySet()
                        .stream()
                        .map(testInput -> Map.entry(modelConverter.convertCombination(testInput.getKey()), testInput.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        Combination nextTestInput = null;

        if (optNextTestInput.isPresent()) {
            nextTestInput = modelConverter.convertCombination(optNextTestInput.get());
        }

        return nextTestInput;
    }

    @Override
    public Combination execute(Map<Combination, TestResult> input) {
        Preconditions.notNull(input);
        Preconditions.check(Try.call(() -> !input.containsKey(null)).toOptional().orElse(true));
        Preconditions.check(Try.call(() -> !input.containsValue(null)).toOptional().orElse(true));

        // assumption: input contains exactly one element
        Optional<Map.Entry<Combination, TestResult>> optEntry = input.entrySet().stream().findFirst();

        Map.Entry<Combination, TestResult> previouslyExecutedTest = null;

        if (optEntry.isPresent()) {
            previouslyExecutedTest = optEntry.get();
        }

        // Generation of next test input
        assert previouslyExecutedTest != null;
        Optional<int[]> optNextTestInput = testManager.generateNextTestInput(
                modelConverter.convertCombination(previouslyExecutedTest.getKey()),
                previouslyExecutedTest.getValue());

        Combination nextTestInput = null;

        // empty, if minimal failure-inducing combination is not failure-inducing
        // or maximal number of iterations is reached
        if (optNextTestInput.isPresent()) {
            nextTestInput = modelConverter.convertCombination(optNextTestInput.get());
        }

        return nextTestInput;
    }
    
}
