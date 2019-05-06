package de.rwth.swc.coffee4j.junit.provider.configuration.diagnosis;

import de.rwth.swc.coffee4j.junit.provider.Loader;
import de.rwth.swc.coffee4j.model.diagnosis.ConstraintDiagnosisConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class ConstraintDiagnosisLoader implements Loader<ConstraintDiagnosisConfiguration> {

    @Override
    public ConstraintDiagnosisConfiguration load(ExtensionContext extensionContext) {
        final Method testMethod = extensionContext.getRequiredTestMethod();

        final Optional<EnableConstraintDiagnosis> optional =
                findAnnotation(testMethod, EnableConstraintDiagnosis.class);

        return optional
                .map(a -> new ConstraintDiagnosisConfiguration(true, a.skip()))
                .orElseGet(ConstraintDiagnosisConfiguration::disable);
    }
}