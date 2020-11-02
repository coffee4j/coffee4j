package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.AfterCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.BeforeCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class LifecycleIT {

    private static final List<String> methodsInvoked = new ArrayList<>();

    @Test
    void allBeforeAndAfter() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(TestCase.class))
                .execute();

        final Events tests = executionResults.tests();

        tests.assertStatistics(
                stats-> stats
                        .started(2)
                        .finished(2)
        );

        final Condition<String> testMethod = new Condition<>(
                methodName -> methodName.equals("testMethod"),
                "testMethod"
        );
        assertThat(methodsInvoked)
                .hasSize(8)
                .containsOnly("beforeTest", "afterTest", "beforeCombination", "afterCombination", "testMethod")
                .containsOnlyOnce("beforeTest", "afterTest")
                .startsWith("beforeTest")
                .endsWith("afterTest")
                .haveExactly(2, testMethod)
                .containsSubsequence("beforeTest",
                        "beforeCombination",
                        "afterCombination",
                        "beforeCombination",
                        "afterCombination",
                        "afterTest"
                );
    }

    static class TestCase {

        private static InputParameterModel model() {
            return InputParameterModel.inputParameterModel("dummy_model")
                    .parameter(
                            Parameter.parameter("parameter_uno")
                                    .values("eins", "zwei").build()
                    ).build();
        }

        @BeforeMethod
        static void beforeTest() {
            methodsInvoked.add("beforeTest");
        }

        @BeforeCombination
        static void beforeCombination() {
            methodsInvoked.add("beforeCombination");
        }

        @CombinatorialTest
        @EnableGeneration
        void testMethod(@InputParameter("parameter_uno") String parameter) {
            methodsInvoked.add("testMethod");
        }

        @AfterCombination
        static void afterCombination() {
            methodsInvoked.add("afterCombination");
        }

        @AfterMethod
        static void afterTest() {
            methodsInvoked.add("afterTest");
        }
    }
}
