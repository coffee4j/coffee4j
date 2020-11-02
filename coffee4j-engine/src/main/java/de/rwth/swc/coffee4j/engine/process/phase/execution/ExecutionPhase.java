package de.rwth.swc.coffee4j.engine.process.phase.execution;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Manages an execution Phase with an enforces execution strategy.
 *
 * <p>Enforces in this case means, that for a combination all {@link TestInputExecutor}
 * supplied by the {@link ExecutionContext} are executed.
 * It does not matter if some executors fails
 */
public class ExecutionPhase extends AbstractPhase<ExecutionContext, List<Combination>, Map<Combination, TestResult>> {
    
    private ExecutionMode executionMode = ExecutionMode.EXECUTE_ALL;
    
    public ExecutionPhase(ExecutionContext executionContext) {
        super(executionContext);
    }
    
    /**
     * Sets the execution mode used by this execution phase.
     *
     * <p>This needs be be cleaned up. Currently the problem is that the same execution phase instance is used
     * for initial and fault characterization execution, so the mode has to be changed if the fault
     * characterization test inputs should be executed even if one of them fails.
     *
     * @param executionMode the mode in which this execution phase should work
     */
    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = Objects.requireNonNull(executionMode);
    }
    
    @Override
    public Map<Combination, TestResult> execute(List<Combination> input) {
        context.getExtensionExecutor().executeBeforeExecution(Collections.unmodifiableList(input));
        
        final Map<Combination, TestResult> results = executeAllCombinations(input);
        
        return context.getExtensionExecutor().executeAfterExecution(new LinkedHashMap<>(results));
    }
    
    private Map<Combination, TestResult> executeAllCombinations(List<Combination> combinations) {
        final Map<Combination, TestResult> results = new LinkedHashMap<>();
        final boolean isFailFastMode = executionMode == ExecutionMode.FAIL_FAST;
        
        for (Combination combination : combinations) {
            context.getExecutionReporters().forEach(reporter -> reporter.testInputExecutionStarted(combination));
            final TestResult testResult = executeCombinationDescriptor(combination);
            results.put(combination, testResult);
            context.getExecutionReporters()
                    .forEach(reporter -> reporter.testInputExecutionFinished(combination, testResult));
            
            if (testResult.isUnsuccessful() && isFailFastMode) {
                break;
            }
        }

        return results;
    }
    
    private TestResult executeCombinationDescriptor(Combination combination) {
        final TestMethodConfiguration testMethodConfiguration = context.getTestMethodConfiguration();
        return testMethodConfiguration.getTestInputExecutor().execute(combination);
    }
    
}
