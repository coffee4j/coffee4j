package de.rwth.swc.coffee4j.engine.process.phase.execution;

import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.PhaseContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Supplies contextual information, that is needed during an execution phase
 */
public class ExecutionContext implements PhaseContext {

    private final ExtensionExecutor extensionExecutor;
    private final TestMethodConfiguration testMethodConfiguration;
    private final List<ExecutionReporter> executionReporters;

    private ExecutionContext(ExtensionExecutor extensionExecutor,
            TestMethodConfiguration testMethodConfiguration,
            List<? extends ExecutionReporter> executionReporters) {
        
        this.extensionExecutor = extensionExecutor;
        this.testMethodConfiguration = testMethodConfiguration;
        this.executionReporters = new ArrayList<>(executionReporters);
    }

    /**
     * Creates a new {@link ExecutionContext} using the supplied information
     *
     * @param extensionExecutor the {@link ExtensionExecutor} to use
     * @param testMethodConfiguration the {@link TestMethodConfiguration} to use
     * @param executionReporters the {@link ExecutionReporter execution reporters} to use
     * @return the created {@link ExecutionContext}
     */
    public static ExecutionContext createExecutionContext(ExtensionExecutor extensionExecutor,
            TestMethodConfiguration testMethodConfiguration,
            List<? extends ExecutionReporter> executionReporters) {
        return new ExecutionContext(extensionExecutor, testMethodConfiguration, executionReporters);
    }

    public ExtensionExecutor getExtensionExecutor() {
        return extensionExecutor;
    }

    public TestMethodConfiguration getTestMethodConfiguration() {
        return testMethodConfiguration;
    }

    public List<ExecutionReporter> getExecutionReporters() {
        return executionReporters;
    }

}
