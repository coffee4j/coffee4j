package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Phase for Interleaving Combinatorial Testing.
 */
public class InterleavingGenerationPhase
        extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    
    private final ExtensionExecutor extensionExecutor;
    private final InterleavingCombinatorialTestManager testManager;
    private final ModelConverter converter;
    
    public InterleavingGenerationPhase(InterleavingGenerationContext context) {
        super(context);
    
        extensionExecutor = context.getExtensionExecutor();
        testManager = context.getTestManager();
        converter = context.getModelConverter();
    }

    @Override
    public Combination execute(Map<Combination, TestResult> previouslyExecutedTests) {
        // if previously executed test is passing -> update coverage
        // if first test is to be generated, previouslyExecutedTests should be empty,
        // otherwise it should contain exactly one element
        for (Map.Entry<Combination, TestResult> test : previouslyExecutedTests.entrySet()) {
            if (test.getValue().isSuccessful()) {
                testManager.updateCoverage(converter.convertCombination(test.getKey()));
            }
        }

        // Before Generation
        extensionExecutor.executeBeforeGeneration();

        Optional<int[]> optNextTestInput;
        Combination nextTestInput = null;
        List<Combination> nextTestInputList = new ArrayList<>();

        Optional<Map.Entry<Combination, TestResult>> optEntry = previouslyExecutedTests.entrySet().stream().findFirst();

        // assumption: previouslyExecutedTests contains exactly one element
        if (optEntry.isPresent()) {
            Map.Entry<Combination, TestResult> previouslyExecutedTest = optEntry.get();

            // Generation of next test input
            optNextTestInput = testManager.generateNextTestInput(
                    converter.convertCombination(previouslyExecutedTest.getKey()),
                    previouslyExecutedTest.getValue());
        } else {
            optNextTestInput = testManager.generateNextTestInput(null, null);
        }

        if (optNextTestInput.isPresent()) {
            nextTestInput = converter.convertCombination(optNextTestInput.get());
            nextTestInputList.add(nextTestInput);
        }

        // After Generation
        extensionExecutor.executeAfterGeneration(nextTestInputList);

        return nextTestInput;
    }
}
