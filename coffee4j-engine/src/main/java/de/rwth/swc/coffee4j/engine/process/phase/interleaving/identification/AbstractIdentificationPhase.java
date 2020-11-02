package de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract class which each IdentificationPhase must extend.
 */
public abstract class AbstractIdentificationPhase extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    protected final InterleavingCombinatorialTestManager testManager;
    protected final ModelConverter converter;

    protected AbstractIdentificationPhase(InterleavingGenerationContext context) {
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
}