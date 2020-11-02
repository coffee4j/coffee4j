package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationSource;

import java.lang.annotation.*;

/**
 * {@code InterleavingConfigurationSource} is an annotation used to register
 * {@linkplain InterleavingConfigurationProvider configuration providers} for the annotated test method.
 * <p>
 * This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code InterleavingConfigurationSource} (demonstrated by {@link EnableInterleavingGeneration}).
 * <p>
 * Copy of {@link ConfigurationSource} for Interleaving Combinatorial Testing.
 * </p>
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InterleavingConfigurationSource {

    /**
     * The type of {@link InterleavingConfigurationProvider} used to provide an
     * {@link InterleavingExecutionConfiguration} for a
     * {@link CombinatorialTest}.
     *
     * @return the {@link InterleavingConfigurationProvider} class
     */
    Class<? extends InterleavingConfigurationProvider> value();
    
}
