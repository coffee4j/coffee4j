package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;

/**
 * Factory for creating an {@link InterleavingGenerationContext}.
 */
public interface InterleavingGenerationContextFactory {
    InterleavingGenerationContext create(ExtensionExecutor extensionExecutor, InputParameterModel model);
}
