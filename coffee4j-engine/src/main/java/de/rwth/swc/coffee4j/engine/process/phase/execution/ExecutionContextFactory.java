package de.rwth.swc.coffee4j.engine.process.phase.execution;

import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;

/**
 * A factory for an {@link ExecutionContext}
 */
@FunctionalInterface
public interface ExecutionContextFactory {

    /**
     * Creates a new {@link ExecutionContext} configured with the supplied {@link ExtensionExecutor}
     *
     * @param extensionExecutor the {@link ExtensionExecutor} which to configure the {@link ExecutionContext}
     * @return the created {@link ExecutionContext}
     */
    ExecutionContext create(ExtensionExecutor extensionExecutor);
}
