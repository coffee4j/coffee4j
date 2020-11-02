package de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for creating instances based on {@link ConfigurationProvider} registered via
 * {@link ConfigurationSource}.
 */
public final class ConfigurationBasedInstanceCreator {
    
    private ConfigurationBasedInstanceCreator() {
        // private constructor for utility class
    }
    
    /**
     * Creates a new instance of a class, possibly with a supplied {@link ConfigurationProvider}.
     *
     * <p>Searches for a {@link ConfigurationSource} of with the correct {@link ConfigurationSource#configurable()}
     * on the {@code method}. If such a source is found, the instance is created using the supplied
     * configuration, otherwise a no-args constructor is used. Consequently, the {@code configurableClass} should
     * always have a no-args constructor.
     *
     * @param configurableClass the class for which an instance should be created, possibly with a configuration
     *     supplied by a {@link ConfigurationProvider} on the {@code method}. Must not be {@code null}
     * @param method the method possibly annotated with {@link ConfigurationSource}. Must not be {@code null}
     * @param <T> the type of the instance created by the method
     * @return the instance created with a configuration if one was provided. Never {@code null}
     */
    public static <T> T create(Class<T> configurableClass, Method method) {
        Preconditions.notNull(configurableClass, "configurableClass required");
        Preconditions.notNull(method, "method required");
        
        return findMatchingConfigurationSource(configurableClass, method)
                .map(source -> createInstanceWithConfiguration(configurableClass, method, source))
                .orElseGet(() -> createInstanceWithoutConfiguration(configurableClass));
    }
    
    private static Optional<ConfigurationSource> findMatchingConfigurationSource(
            Class<?> configurableClass, Method method) {
        
        final List<ConfigurationSource> matchingConfigurationSources
                = AnnotationSupport.findRepeatableAnnotations(method, ConfigurationSource.class).stream()
                        .filter(source -> source.configurable().equals(configurableClass))
                        .collect(Collectors.toList());
        
        if (matchingConfigurationSources.isEmpty()) {
            return Optional.empty();
        } else if (matchingConfigurationSources.size() == 1) {
            return Optional.of(matchingConfigurationSources.get(0));
        } else {
            throw new Coffee4JException("Expected exactly one " + ConfigurationSource.class.getCanonicalName()
                    + " for class " + configurableClass.getCanonicalName() + " but got "
                    + matchingConfigurationSources.size());
        }
    }
    
    private static <T> T createInstanceWithConfiguration(
            Class<T> configurableClass, Method method, ConfigurationSource configurationSource) {
        
        final Object configuration = createConfiguration(method, configurationSource);
        final Object[] constructorArguments = new Object[] { configuration };
        final Constructor<T> constructor = findConstructor(configurableClass, configurationSource.configuration());
    
        try {
            return constructor.newInstance(constructorArguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new Coffee4JException(exception, "Could not create a new instance of %s the given constructor %s",
                    configurableClass.getCanonicalName(), constructor.getName());
        }
    }
    
    private static Object createConfiguration(Method method, ConfigurationSource configurationSource) {
        return AnnotationConsumerInitializer
                .initialize(method, ReflectionSupport.newInstance(configurationSource.provider()))
                .provide(method);
    }
    
    private static <T> Constructor<T> findConstructor(Class<T> configurableClass, Class<?> configurationClass) {
        try {
            return configurableClass.getConstructor(configurationClass);
        } catch (NoSuchMethodException exception) {
            throw new Coffee4JException(
                    exception,
                    "The class %s must have a public constructor which accepts a %s",
                    configurableClass.getName(),
                    configurationClass.getSimpleName()
            );
        }
    }
    
    private static <T> T createInstanceWithoutConfiguration(Class<T> configurableClass) {
        return ReflectionUtils.createNewInstance(configurableClass);
    }
    
}
