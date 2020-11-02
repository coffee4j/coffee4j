package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConstructorBasedFaultCharacterizationProviderTest implements MockingTest {

    private static FaultCharacterizationConfiguration currentConfig;

    @Test
    void providesFaultCharacterizationAlgorithmFactory() {
        final ConstructorBasedFaultCharacterizationProvider provider = new ConstructorBasedFaultCharacterizationProvider();
        final EnableFaultCharacterization annotation = new EnableFaultCharacterization() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return EnableFaultCharacterization.class;
            }

            @Override
            public Class<? extends FaultCharacterizationAlgorithm> algorithm() {
                return SomeFaultCharacterizationAlgorithm.class;
            }
        };
        provider.accept(annotation);

        final Method someMethod = Mockito.mock(Method.class);
        final FaultCharacterizationConfiguration mockedConfig = mock(FaultCharacterizationConfiguration.class);

        final FaultCharacterizationAlgorithmFactory providedFactory = provider.provide(someMethod);
        assertThat(providedFactory).isInstanceOf(FaultCharacterizationAlgorithmFactory.class);

        providedFactory.create(mockedConfig);
        assertThat(currentConfig).isEqualTo(mockedConfig);
    }

    private static class SomeFaultCharacterizationAlgorithm implements FaultCharacterizationAlgorithm {

        public SomeFaultCharacterizationAlgorithm(FaultCharacterizationConfiguration config) {
            currentConfig = config;
        }

        @Override
        public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
            return null;
        }

        @Override
        public List<int[]> computeFailureInducingCombinations() {
            return null;
        }
    }
}
