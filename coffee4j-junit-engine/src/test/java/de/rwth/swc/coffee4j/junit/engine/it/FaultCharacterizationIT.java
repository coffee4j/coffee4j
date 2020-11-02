package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.ben.Ben;
import de.rwth.swc.coffee4j.engine.process.report.sequential.LoggingSequentialExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.EnableFaultCharacterization;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter.EnableReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class FaultCharacterizationIT {

    @Test
    void characterizeFault() {
        assertThatCode(() -> EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(FaultCharacterizationTestCase.class))
                .execute())
        .doesNotThrowAnyException();
    }

    /**
     * An example {@link CombinatorialTest} demonstrating complete fault characterization capability. It also shows that
     * constraints are respected by BEN, since one fault is not found as it is hidden behind a constraint.
     * When executing this test, three test inputs should fail, and "Combination{param1=2, param6=1.1, param5=3}" should
     * be discovered as the only failure-inducing combination.
     */
    static class FaultCharacterizationTestCase {
        
        private static final Logger LOG = LoggerFactory.getLogger(FaultCharacterizationTestCase.class);

        @CombinatorialTest
        @EnableFaultCharacterization(algorithm = Ben.class)
        @EnableReporter(LoggingSequentialExecutionReporter.class)
        void combinatorialTest(@InputParameter("param1") int param1, @InputParameter("param2") String param2,
                               @InputParameter("param3") int param3, @InputParameter("param4") boolean param4,
                               @InputParameter("param5") int param5, @InputParameter("param6") float param6) {
            LOG.debug("{}\t{}\t{}\t{}\t{}\t{}", param1, param2, param3, param4, param5, param6);
            assertFalse((param1 == 1 && "one  ".equals(param2) && param4) || (param1 == 2 && param5 == 3 && param6 == 1.1f));
        }

        private static InputParameterModel.Builder model() {
            return inputParameterModel("test testModel")
                    .positiveTestingStrength(3)
                    .parameters(
                            parameter("param1")
                                    .values(1, 2, 3),
                            parameter("param2")
                                    .values("one  ", "two  ", "three"),
                            parameter("param3")
                                    .values(1, 2, 3),
                            parameter("param4")
                                    .values(true, false),
                            parameter("param5")
                                    .values(1, 2, 3),
                            parameter("param6")
                                    .values(1.1f, 2.2f, 3.3f)
                    )
                    .exclusionConstraint(
                            constrain("param1", "param2", "param4")
                                    .by(
                                            (Integer firstValue, String secondValue, Boolean fourthValue) ->
                                                    !(firstValue == 1 && "one  ".equals(secondValue) && fourthValue)
                                    )
                    );
        }

    }

}
