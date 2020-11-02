package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;

import java.lang.reflect.Method;

import static de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedExtractionUtil.extractTypedObjectFromMethod;

/**
 * A provider loading a {@link SequentialExecutionConfiguration} from a method as described in {@link ConfigurationFromMethod}.
 * <p>
 * This is a more or less direct copy of org.junit.jupiter.params.provider.MethodArgumentsProvider from the
 * junit-jupiter-params project.
 */
class MethodBasedConfigurationProvider implements ConfigurationProvider, AnnotationConsumer<ConfigurationFromMethod> {

    private String methodName;

    @Override
    public void accept(ConfigurationFromMethod configurationFromMethod) {
        methodName = configurationFromMethod.value();
    }

    @Override
    public SequentialExecutionConfiguration provide(Method method) {
        final Method configurationMethod = ReflectionUtils.findQualifiedMethod(method.getDeclaringClass(), methodName);
        return extractConfigurationFromMethod(configurationMethod);
    }

    private SequentialExecutionConfiguration extractConfigurationFromMethod(Method method) {
        return extractTypedObjectFromMethod(method,
                SequentialExecutionConfiguration.class,
                SequentialExecutionConfiguration.Builder.class);
    }
}
