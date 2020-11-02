package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.ben.Ben;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis.EnableConflictDetection;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter.EnableReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.EnableFaultCharacterization;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.extension.EnableExtension;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.AfterCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.BeforeCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.AfterFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.BeforeFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.BeforeExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.AfterGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.BeforeGenerationCallback;
import de.rwth.swc.coffee4j.engine.process.report.sequential.LoggingSequentialExecutionReporter;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class CompleteCombinatorialTestExampleIT {

    private static final Set<ExecutionCallbackTypes> invokedExecutionCallbacks = new HashSet<>();
    private static final Set<PhaseCallbackTypes> invokedPhasedCallbacks = new HashSet<>();

    enum ExecutionCallbackTypes {
        BEFORE_TEST,
        BEFORE_COMBINATION,
        AFTER_COMBINATION,
        AFTER_TEST

    }

    enum PhaseCallbackTypes {
        BEFORE_GENERATION,
        AFTER_GENERATION,
        BEFORE_EXECUTION,
        AFTER_EXECUTION,
        BEFORE_FAULT_CHARACTERIZATION,
        AFTER_FAULT_CHARACTERIZATION
    }

    enum Language {
        de_DE,
        en_US,
        fr_BE
    }

    @Test
    void executesExecutionCallbacks() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(CompleteCombinatorialTestCase.class))
                .execute();

        assertThat(invokedExecutionCallbacks)
                .containsExactlyInAnyOrder(ExecutionCallbackTypes.values());
        assertThat(invokedPhasedCallbacks)
                .containsExactlyInAnyOrder(PhaseCallbackTypes.values());
    }

    static class CompleteCombinatorialTestCase {

        public static InputParameterModel.Builder model() {
            return inputParameterModel("exampleModel")
                    .parameters(
                            parameter("OS").values("Windows", "Linux", "MacOS"),
                            parameter("Language").values(Language.de_DE, Language.en_US, Language.fr_BE),
                            parameter("Browser").values("Safari", "Chrome", "Edge"))
                    .exclusionConstraints(
                            constrain("OS", "Browser")
                                    .by((String os, String browser) -> !os.equals("MacOS") || browser.equals("Safari")),
                            constrain("OS", "Browser")
                                    .by((String os, String browser) -> !os.equals("Linux") || browser.equals("Chrome")),
                            constrain("OS", "Browser")
                                    .by((String os, String browser) ->
                                            os.equals("MacOS") || os.equals("Linux") || browser.equals("Edge")));
        }

        @CombinatorialTest
        @EnableExtension({
                BeforeGeneration.class, AfterGeneration.class,
                BeforeExecution.class, AfterExecution.class,
                BeforeFaultCharacterization.class, AfterFaultCharacterization.class
        })
        @EnableFaultCharacterization(algorithm = Ben.class)
        @EnableConflictDetection
        @EnableGeneration(algorithms = Ipog.class)
        @EnableReporter(LoggingSequentialExecutionReporter.class)
        void unixLike(@InputParameter("OS") String os,
                      @InputParameter("Language") Language language,
                      @InputParameter("Browser") String browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }

        @BeforeMethod
        void beforeTest() {
            invokedExecutionCallbacks.add(ExecutionCallbackTypes.BEFORE_TEST);
        }

        @AfterMethod
        void afterTest() {
            invokedExecutionCallbacks.add(ExecutionCallbackTypes.AFTER_TEST);
        }

        @BeforeCombination
        void beforeCombination(@InputParameter("OS") String os,
                               @InputParameter("Language") Language language,
                               @InputParameter("Browser") String browser) {
            invokedExecutionCallbacks.add(ExecutionCallbackTypes.BEFORE_COMBINATION);
        }

        @AfterCombination
        void afterCombination(@InputParameter("OS") String os,
                              @InputParameter("Language") Language language,
                              @InputParameter("Browser") String browser) {
            invokedExecutionCallbacks.add(ExecutionCallbackTypes.AFTER_COMBINATION);
        }
        
    }

    static class BeforeGeneration implements BeforeGenerationCallback {
        @Override
        public void beforeGeneration() {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.BEFORE_GENERATION);
        }
    }

    static class AfterGeneration implements AfterGenerationCallback {
        @Override
        public List<Combination> afterGeneration(List<Combination> inputCombinations) {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.AFTER_GENERATION);
            return inputCombinations;
        }
    }

    static class BeforeExecution implements BeforeExecutionCallback {
        @Override
        public void beforeExecution(List<Combination> combination) {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.BEFORE_EXECUTION);
        }
    }

    static class AfterExecution implements AfterExecutionCallback {
        @Override
        public Map<Combination, TestResult> afterExecution(Map<Combination, TestResult> combinationTestResultMap) {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.AFTER_EXECUTION);
            return combinationTestResultMap;
        }
    }

    static class BeforeFaultCharacterization implements BeforeFaultCharacterizationCallback {
        @Override
        public void beforeFaultCharacterization(Map<Combination, TestResult> combinationTestResultMap) {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.BEFORE_FAULT_CHARACTERIZATION);
        }
    }

    static class AfterFaultCharacterization implements AfterFaultCharacterizationCallback {
        @Override
        public void afterFaultCharacterization(List<Combination> additionalTestInput) {
            invokedPhasedCallbacks.add(PhaseCallbackTypes.AFTER_FAULT_CHARACTERIZATION);
        }
    }
}
