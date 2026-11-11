package io.github.coffee4j.engine.process.phase.interleaving.generation;

import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import io.github.coffee4j.engine.process.extension.ExtensionExecutor;

/**
 * Factory for creating an {@link InterleavingGenerationContext}.
 */
public interface InterleavingGenerationContextFactory {
    InterleavingGenerationContext create(ExtensionExecutor extensionExecutor, InputParameterModel model);
}
