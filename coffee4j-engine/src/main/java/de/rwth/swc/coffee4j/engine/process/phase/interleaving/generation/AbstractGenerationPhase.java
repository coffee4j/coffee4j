package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;

import java.util.Map;

/**
 * Abstract class which each GenerationPhase must extend.
 */
public abstract class AbstractGenerationPhase extends AbstractPhase<InterleavingGenerationContext, Map<Combination, TestResult>, Combination> {
    protected final ExtensionExecutor extensionExecutor;
    protected final InterleavingCombinatorialTestManager testManager;
    protected final ModelConverter converter;

    protected AbstractGenerationPhase(InterleavingGenerationContext context) {
        super(context);

        extensionExecutor = context.getExtensionExecutor();
        testManager = context.getTestManager();
        converter = context.getModelConverter();
    }
}
