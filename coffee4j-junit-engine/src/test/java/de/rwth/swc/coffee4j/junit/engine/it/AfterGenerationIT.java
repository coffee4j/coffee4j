package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.AfterGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.extension.EnableExtension;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import java.util.List;

import static java.util.Comparator.comparing;
import static org.assertj.core.data.Index.atIndex;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.uniqueIdSubstring;

class AfterGenerationIT {

    @Test
    void afterGeneration() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(TestCase.class))
                .execute();

        final Events tests = executionResults.tests();

        tests.assertStatistics(
                stats-> stats
                        .started(3)
                        .finished(3));

        executionResults.tests()
                .started()
                .assertThatEvents()
                .has(uniqueIdSubstring("1_drei"), atIndex(0))
                .has(uniqueIdSubstring("2_eins"), atIndex(1))
                .has(uniqueIdSubstring("3_zwei"), atIndex(2));

    }

    static class AfterGeneration implements AfterGenerationCallback {

        @Override
        public List<Combination> afterGeneration(List<Combination> inputCombinations) {
            inputCombinations.sort(comparing(
                    combination -> (String) combination.getRawValue("parameter_uno")));
            return inputCombinations;
        }
    }

    static class TestCase {

        private static InputParameterModel model() {
            return InputParameterModel.inputParameterModel("dummy_model")
                    .parameter(
                            Parameter.parameter("parameter_uno")
                                    .values("2_eins", "3_zwei", "1_drei").build()
                    ).build();
        }

        @CombinatorialTest
        @EnableGeneration
        @EnableExtension(AfterGeneration.class)
        void testMethod(@InputParameter("parameter_uno") String parameter) { }

    }
}
