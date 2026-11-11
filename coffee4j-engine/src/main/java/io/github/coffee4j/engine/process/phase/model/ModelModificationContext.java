package io.github.coffee4j.engine.process.phase.model;

import io.github.coffee4j.engine.process.extension.ExtensionExecutor;
import io.github.coffee4j.engine.process.phase.PhaseContext;
import io.github.coffee4j.engine.report.ExecutionReporter;

import java.util.Objects;

/**
 * The context needed for a {@link ModelModificationPhase}.
 *
 * <p>This is just a wrapper for the {@link ExtensionExecutor} since a context must implement {@link PhaseContext}.
 */
public record ModelModificationContext(ExtensionExecutor extensionExecutor,
                                       ExecutionReporter reporter) implements PhaseContext {

    public ModelModificationContext {
        Objects.requireNonNull(extensionExecutor);
        Objects.requireNonNull(reporter);
    }

}
