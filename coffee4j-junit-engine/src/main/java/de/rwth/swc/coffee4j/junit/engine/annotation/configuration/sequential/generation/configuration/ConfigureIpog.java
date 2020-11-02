package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.IpogConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationSource;

import java.lang.annotation.*;

/**
 * This is an annotation to configure the {@link Ipog} TestInputGroupGenerator via {@link IpogConfiguration}.
 *
 * It defines a {@link ConfigurationSource} with the {@link IpogConfigurationProvider}
 * to create new instances of a {@link IpogConfiguration}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationSource(
        provider = IpogConfigurationProvider.class,
        configurable = Ipog.class,
        configuration = IpogConfiguration.class)
public @interface ConfigureIpog {

    /**
     * Gets the class of the {@link ConstraintCheckerFactory ConstraintCheckerFactory}
     *
     * @return the class used to create a {@link de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker}
     * for a {@link Ipog}.
     * Need to have a no-args constructor
     */
    Class<? extends ConstraintCheckerFactory> constraintCheckerFactory()
            default MinimalForbiddenTuplesCheckerFactory.class;
    
}
