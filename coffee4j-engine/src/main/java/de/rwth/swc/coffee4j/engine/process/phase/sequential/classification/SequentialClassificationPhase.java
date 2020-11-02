package de.rwth.swc.coffee4j.engine.process.phase.sequential.classification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.CachingDelegatingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.GeneratingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;
import org.junit.platform.commons.function.Try;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract Class for Classification Phase providing general functionality in the sequential part.
 */
public class SequentialClassificationPhase extends AbstractPhase<SequentialGenerationContext, Map<Combination, TestResult>, Combination> {
    
    protected final GeneratingSequentialCombinatorialTestManager testManager;
    protected final ModelConverter modelConverter;

    public SequentialClassificationPhase(SequentialGenerationContext context) {
        super(context);

        if (context.getGenerator() instanceof CachingDelegatingSequentialCombinatorialTestManager) {
            Preconditions.check(((CachingDelegatingSequentialCombinatorialTestManager)context.getGenerator()).getGenerator() instanceof GeneratingSequentialCombinatorialTestManager);
            testManager = (GeneratingSequentialCombinatorialTestManager) ((CachingDelegatingSequentialCombinatorialTestManager) context.getGenerator()).getGenerator();
        } else {
            Preconditions.check(context.getGenerator() instanceof GeneratingSequentialCombinatorialTestManager);
            testManager = (GeneratingSequentialCombinatorialTestManager) context.getGenerator();
        }

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
        Optional<int[]> optNextTestInput = testManager.generateNextTestInputForClassification(
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
