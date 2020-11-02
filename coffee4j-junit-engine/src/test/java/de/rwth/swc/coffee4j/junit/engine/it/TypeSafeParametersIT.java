package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Tests functional-requirement F5 from the thesis "Development of an Automated Combinatorial Testing Framework"
 */
class TypeSafeParametersIT {

    @Test
    void checksTypeSafety() {
        final EngineTestKit.Builder builder = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ArbitraryParameterOrderTestCase.class));
        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(builder::execute)
                .withMessageContaining("Parameter \"Browser\" not assignable. " +
                        "Expected type java.lang.Integer but was java.lang.String");
    }

    static class ArbitraryParameterOrderTestCase {

        private InputParameterModel modelMethod() {
            return RequirementsModel.BROWSER_IPM;
        }

        @CombinatorialTest(inputParameterModel = "modelMethod")
        @EnableGeneration
        void unixLike(@InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("OS") String os,
                      @InputParameter("Browser") Integer browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }
    }
}
