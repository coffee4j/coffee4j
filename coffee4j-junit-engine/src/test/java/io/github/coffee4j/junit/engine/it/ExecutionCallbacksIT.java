package io.github.coffee4j.junit.engine.it;

import io.github.coffee4j.junit.engine.CombinatorialTestEngine;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import io.github.coffee4j.junit.engine.annotation.test.lifecycle.combination.AfterCombination;
import io.github.coffee4j.junit.engine.annotation.test.lifecycle.combination.BeforeCombination;
import io.github.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import io.github.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import io.github.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Tests functional-requirement F7 from the thesis "Development of an Automated Combinatorial Testing Framework"
 */
class ExecutionCallbacksIT {

    private static final Set<CallbackTypes> invokedCallbacks = new HashSet<>();

    private enum CallbackTypes {
        BEFORE_TEST,
        BEFORE_COMBINATION,
        AFTER_COMBINATION,
        AFTER_TEST
    }

    @Test
    void executesExecutionCallbacks() {
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
        void unixLike(@InputParameter("OS") String os,
                      @InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("Browser") String browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }

        @BeforeMethod
        void beforeTest() {
            invokedCallbacks.add(CallbackTypes.BEFORE_TEST);
        }

        @AfterMethod
        void afterTest() {
            invokedCallbacks.add(CallbackTypes.AFTER_TEST);
        }

        @BeforeCombination
        void beforeCombination(@InputParameter("OS") String os,
                               @InputParameter("Language") RequirementsModel.Language language,
                               @InputParameter("Browser") String browser) {
            invokedCallbacks.add(CallbackTypes.BEFORE_COMBINATION);
        }

        @AfterCombination
        void afterCombination(@InputParameter("OS") String os,
                              @InputParameter("Language") RequirementsModel.Language language,
                              @InputParameter("Browser") String browser) {
            invokedCallbacks.add(CallbackTypes.AFTER_COMBINATION);
        }
        
    }

}
