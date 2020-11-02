package de.rwth.swc.coffee4j.engine.process.phase.model;

import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.PhaseContext;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.util.Objects;

/**
 * The context needed for a {@link ModelModificationPhase}.
 *
 * <p>This is just a wrapper for the {@link ExtensionExecutor} since a context must implement {@link PhaseContext}.
 */
public class ModelModificationContext implements PhaseContext {
    
    private final ExtensionExecutor extensionExecutor;
    private final ExecutionReporter reporter;
    
    public ModelModificationContext(ExtensionExecutor extensionExecutor, ExecutionReporter reporter) {
        this.extensionExecutor = Objects.requireNonNull(extensionExecutor);
        this.reporter = Objects.requireNonNull(reporter);
    }
    
    public ExtensionExecutor getExtensionExecutor() {
        return extensionExecutor;
    }
    
    public ExecutionReporter getReporter() {
        return reporter;
    }
    
}
