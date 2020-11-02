package de.rwth.swc.coffee4j.engine.process.extension;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.extension.model.ModelModifier;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.util.List;
import java.util.Map;

/**
 * Manages the execution of defined extensions.
 * The currently known extension are callbacks before the three phases:
 * initial generation, execution, and fault characterization
 */
public interface ExtensionExecutor {
    
    /**
     * Executes all registered {@link ModelModifier} exactly in the order they were registered in.
     *
     * @param original the original {@link InputParameterModel} as given by the user
     * @param reporter for reporting any changes to the original model
     * @return an {@link InputParameterModel} modified by all registered processors
     */
    InputParameterModel executeModelModifiers(InputParameterModel original, ExecutionReporter reporter);

    /**
     * Executes the callback before the initial generation
     */
    void executeBeforeGeneration();

    /**
     * Executes the callback after the generation phase
     *
     * @param combinations the list of combinations generated in the generation phase
     * @return the possibly sorted combinations
     */
    List<Combination> executeAfterGeneration(List<Combination> combinations);

    /**
     * Executes the callback before the execution phase
     *
     * @param combinations the sorted combinations from the generation phase
     */
    void executeBeforeExecution(List<Combination> combinations);

    /**
     * Executes the callback after the execution phase
     *
     * @param testResultMap the preliminary test results from the execution phase
     * @return the possibly altered execution results
     */
    Map<Combination, TestResult> executeAfterExecution(Map<Combination, TestResult> testResultMap);

    /**
     * Executes the callback before the fault characterization phase
     *
     * @param combinationTestResultMap the summarized test results of the combinations
     */
    void executeBeforeFaultCharacterization(Map<Combination, TestResult> combinationTestResultMap);

    /**
     * Executes the callback after the fault characterization phase
     *
     * @param combinations the combinations generated in the fault characterization phase
     */
    void executeAfterFaultCharacterization(List<Combination> combinations);
    
}
