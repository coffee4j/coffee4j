package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.ConflictExplainer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Loads a {@link ConflictDetectionConfiguration} for a {@link CombinatorialTest}
 * using the {@link EnableConflictDetection} annotation.
 */
public class ConflictDetectionConfigurationLoader implements Loader<ConflictDetectionConfiguration> {
    
    @Override
    public ConflictDetectionConfiguration load(Method method) {
        return AnnotationSupport.findAnnotation(method, EnableConflictDetection.class)
            .map(annotation -> new ConflictDetectionConfiguration(
                    true,
                    annotation.shouldAbort(),
                    annotation.explainConflicts(),
                    () -> (ConflictExplainer) createInstance(annotation.conflictExplanationAlgorithm()),
                    annotation.diagnoseConflicts(),
                    () -> (ConflictDiagnostician) createInstance(annotation.conflictDiagnosisAlgorithm())))
            .orElseGet(ConflictDetectionConfiguration::disable);
    }
    
    private static Object createInstance(Class<?> clazz) {
        try {
            return MethodHandles.lookup()
                    .findConstructor(clazz, MethodType.methodType(void.class))
                    .invoke();
        } catch (Throwable throwable) {
            throw new Coffee4JException("Could not create new instance of class " + clazz, throwable);
        }
    }
    
}
