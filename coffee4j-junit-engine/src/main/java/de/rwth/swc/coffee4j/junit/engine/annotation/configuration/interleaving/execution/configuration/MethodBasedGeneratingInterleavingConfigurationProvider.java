package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;

import static de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedExtractionUtil.extractTypedObjectFromMethod;

/**
 * {@link InterleavingConfigurationProvider} that processes a method providing a configuration.
 */
public class MethodBasedGeneratingInterleavingConfigurationProvider implements AnnotationConsumer<EnableInterleavingConstraintGeneration>, InterleavingConfigurationProvider {

    private String methodName;

    @Override
    public void accept(EnableInterleavingConstraintGeneration enableInterleavingConstraintGeneration) {
        methodName = enableInterleavingConstraintGeneration.value();
    }

    @Override
    public InterleavingExecutionConfiguration provide(Method method) {
        if (AnnotationSupport.isAnnotated(method, EnableInterleavingConstraintGeneration.class)) {
            final Method configurationMethod = ReflectionUtils.findQualifiedMethod(method.getDeclaringClass(), methodName);
            InterleavingExecutionConfiguration configuration;

            try {
                configuration = extractConfigurationFromMethod(configurationMethod);
            } catch (Exception e) {
                throw new Coffee4JException("An GeneratingInterleavingCombinatorialTestExecutionConfiguration is expected!", e);
            }

            return configuration;
        } else if (AnnotationSupport.isAnnotated(method, EnableInterleavingGeneration.class)) {
            return extractConfigurationFromMethod(method);
        } else {
            throw new Coffee4JException("Deviating number of configuration methods. Method %s must configure exactly " +
                    "one method for an interleaving generation configuration");
        }
    }

    private InterleavingExecutionConfiguration extractConfigurationFromMethod(Method method) {
        return extractTypedObjectFromMethod(method,
                InterleavingExecutionConfiguration.class,
                InterleavingExecutionConfiguration.Builder.class);
    }
}
