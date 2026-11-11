package io.github.coffee4j.junit.engine.it;

import io.github.coffee4j.algorithmic.model.TestResult;
import io.github.coffee4j.junit.engine.CombinatorialTestEngine;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import io.github.coffee4j.junit.engine.annotation.extension.EnableExtension;
import io.github.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import io.github.coffee4j.engine.configuration.model.Combination;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import io.github.coffee4j.engine.configuration.extension.characterization.AfterFaultCharacterizationCallback;
import io.github.coffee4j.engine.configuration.extension.characterization.BeforeFaultCharacterizationCallback;
import io.github.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import io.github.coffee4j.engine.configuration.extension.execution.BeforeExecutionCallback;
import io.github.coffee4j.engine.configuration.extension.generation.AfterGenerationCallback;
import io.github.coffee4j.engine.configuration.extension.generation.BeforeGenerationCallback;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Tests functional-requirement F10 from the thesis "Development of an Automated Combinatorial Testing Framework"
 */
class PhaseCallbacksIT {

    private static final Set<CallbackTypes> invokedCallbacks = new HashSet<>();

    private enum CallbackTypes {
        BEFORE_GENERATION,
        AFTER_GENERATION,
        BEFORE_EXECUTION,
        AFTER_EXECUTION,
        BEFORE_FAULT_CHARACTERIZATION,
        AFTER_FAULT_CHARACTERIZATION
    }

    @Test
    void executesPhaseCallbacks() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ExecutionCallbacksTestCase.class))
                .execute();

        assertThat(invokedCallbacks)
                .containsExactlyInAnyOrder(CallbackTypes.values());
    }

    static class ExecutionCallbacksTestCase {

        private InputParameterModel modelMethod() {
            return RequirementsModel.BROWSER_IPM;
        }

        @CombinatorialTest(inputParameterModel = "modelMethod")
        @EnableGeneration
        @EnableExtension({
                BeforeGeneration.class, AfterGeneration.class,
                BeforeExecution.class, AfterExecution.class,
                BeforeFaultCharacterization.class, AfterFaultCharacterization.class
        })
        void unixLike(@InputParameter("OS") String os,
                      @InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("Browser") String browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }
    }

    static class BeforeGeneration implements BeforeGenerationCallback {

        @Override
        public void beforeGeneration() {
            invokedCallbacks.add(CallbackTypes.BEFORE_GENERATION);
        }
    }

    static class AfterGeneration implements AfterGenerationCallback {

        @Override
        public List<Combination> afterGeneration(List<Combination> inputCombinations) {
            invokedCallbacks.add(CallbackTypes.AFTER_GENERATION);
            return inputCombinations;
        }
    }

    static class BeforeExecution implements BeforeExecutionCallback {

        @Override
        public void beforeExecution(List<Combination> combination) {
            invokedCallbacks.add(CallbackTypes.BEFORE_EXECUTION);
        }
    }

    static class AfterExecution implements AfterExecutionCallback {

        @Override
        public Map<Combination, TestResult> afterExecution(Map<Combination, TestResult> combinationTestResultMap) {
            invokedCallbacks.add(CallbackTypes.AFTER_EXECUTION);
            return combinationTestResultMap;
        }
    }

    static class BeforeFaultCharacterization implements BeforeFaultCharacterizationCallback {

        @Override
        public void beforeFaultCharacterization(Map<Combination, TestResult> combinationTestResultMap) {
            invokedCallbacks.add(CallbackTypes.BEFORE_FAULT_CHARACTERIZATION);
        }
    }

    static class AfterFaultCharacterization implements AfterFaultCharacterizationCallback {

        @Override
        public void afterFaultCharacterization(List<Combination> additionalTestInput) {
            invokedCallbacks.add(CallbackTypes.AFTER_FAULT_CHARACTERIZATION);
        }
    }
}
