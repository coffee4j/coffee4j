package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving;

import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationSource;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.InterleavingConfigurationProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.MethodBasedGeneratingInterleavingConfigurationProvider;

import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableInterleavingConstraintGeneration {
    
    /**
     * @return the name of the method providing an {@link InterleavingExecutionConfiguration}
     */
    String value() default "";

    /**
     * @return {@link InterleavingConfigurationProvider} providing a constraint-generation configuration. Not needed
     * for sequential combinatorial testing as it currently does not support the dynamic approach. The configuration
     * is provided via the annotation
     * {@link ConfigurationSource}
     */
    Class<? extends InterleavingConfigurationProvider> getProvider() default MethodBasedGeneratingInterleavingConfigurationProvider.class;
    
}
