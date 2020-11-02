package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalExplanation;
import de.rwth.swc.coffee4j.algorithmic.conflict.choco.ChocoModel;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.ConflictExplainer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflictDetectionConfigurationLoaderTest {

    @Test
    void loadConflictDetectionConfiguration() throws NoSuchMethodException {
        final ConflictDetectionConfigurationLoader loader = new ConflictDetectionConfigurationLoader();

        final Method method = this.getClass().getMethod("annotatedMethod");
        final ConflictDetectionConfiguration loadedConfig = loader.load(method);

        assertThat(loadedConfig.shouldAbort()).isFalse();
        assertThat(loadedConfig.isConflictExplanationEnabled()).isTrue();
        assertEquals(loadedConfig.createConflictExplainer().getClass(), SomeConflictExplainer.class);
        assertThat(loadedConfig.isConflictDiagnosisEnabled()).isTrue();
        assertEquals(loadedConfig.createConflictDiagnostician().getClass(), SomeConflictDiagnostician.class);
    }

    @Test
    void noAnnotatedConfiguration() throws NoSuchMethodException {
        final ConflictDetectionConfigurationLoader loader = new ConflictDetectionConfigurationLoader();

        final Method method = this.getClass().getMethod("notAnnotatedMethod");
        final ConflictDetectionConfiguration loadedConfig = loader.load(method);

        assertThat(loadedConfig)
                .isEqualTo(ConflictDetectionConfiguration.disable());
    }
    
    @EnableConflictDetection(
            shouldAbort = false,
            conflictExplanationAlgorithm = SomeConflictExplainer.class,
            diagnoseConflicts = true,
            conflictDiagnosisAlgorithm = SomeConflictDiagnostician.class)
    public void annotatedMethod() {
    }
    
    public void notAnnotatedMethod() {
    }

    static class SomeConflictExplainer implements ConflictExplainer {
    
        @Override
        public Optional<InternalExplanation> getMinimalConflict(ChocoModel model, int[] background, int[] relaxable) {
            return Optional.empty();
        }
    
    }
    
    static class SomeConflictDiagnostician implements ConflictDiagnostician {
    
        @Override
        public int[][] getMinimalDiagnoses(InternalConflictSet conflict) {
            return new int[0][];
        }
    
    }
    
}
