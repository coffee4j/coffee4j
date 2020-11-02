package de.rwth.swc.coffee4j.engine.process.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;

import java.util.List;

/**
 * A factory for an {@link ExtensionExecutor}
 */
@FunctionalInterface
public interface ExtensionExecutorFactory {

    /**
     * Creates an {@link ExtensionExecutor} configured with the defined extensions
     *
     * @param extensions the extension to configure the executor with
     * @return the created extension executor
     */
    ExtensionExecutor create(List<Extension> extensions);

}
