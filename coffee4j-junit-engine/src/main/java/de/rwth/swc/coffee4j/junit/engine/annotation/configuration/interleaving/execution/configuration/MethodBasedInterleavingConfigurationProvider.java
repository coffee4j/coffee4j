package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;

import java.lang.reflect.Method;

import static de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedExtractionUtil.extractTypedObjectFromMethod;

/**
 * A provider loading an {@link InterleavingExecutionConfiguration} from a method as described in
 * {@link EnableInterleavingGeneration}.
 * <p>
 * This is a more or less direct copy of org.junit.jupiter.params.provider.MethodArgumentsProvider from the
 * junit-jupiter-params project.
 */
public class MethodBasedInterleavingConfigurationProvider implements InterleavingConfigurationProvider, AnnotationConsumer<EnableInterleavingGeneration> {

    private String methodName;

    @Override
    public void accept(EnableInterleavingGeneration configurationFromMethod) {
        methodName = configurationFromMethod.value();
    }

    @Override
    public InterleavingExecutionConfiguration provide(Method method) {
        final Method configurationMethod = ReflectionUtils.findQualifiedMethod(method.getDeclaringClass(), methodName);
        InterleavingExecutionConfiguration configuration;

        try {
            configuration = extractConfigurationFromMethod(configurationMethod);
        } catch (Exception e) {
            throw new Coffee4JException("An InterleavingCombinatorialTestExecutionConfiguration is expected!", e);
        }

        return configuration;
    }

    private InterleavingExecutionConfiguration extractConfigurationFromMethod(Method method) {
        return extractTypedObjectFromMethod(method,
                InterleavingExecutionConfiguration.class,
                InterleavingExecutionConfiguration.Builder.class);
    }
}