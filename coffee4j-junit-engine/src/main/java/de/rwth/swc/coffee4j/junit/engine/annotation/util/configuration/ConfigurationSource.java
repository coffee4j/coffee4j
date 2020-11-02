package de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration;

import java.lang.annotation.*;

/**
 * Source of a {@link ConfigurationProvider} for a specific configurable class and configuration type.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigurationSources.class)
public @interface ConfigurationSource {
    
    /**
     * The provider which creates the configuration instance.
     * Must implement the {@link ConfigurationProvider} interface for the given
     * {@link #configuration()} class.
     *
     * @return the provider to use
     */
    Class<? extends ConfigurationProvider<?>> provider();
    
    /**
     * The configurable class which can be instantiated using the configuration of type
     * {@link #configuration()} provided by the {@link #provider()}.
     *
     * @return the configurable class
     */
    Class<?> configurable();
    
    /**
     * The configuration class which is used to configure a new instance of {@link #configurable()}.
     *
     * @return the type of the configuration
     */
    Class<?> configuration();
    
}
