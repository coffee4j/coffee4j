package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.IpogNegConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm.IpogNeg;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationSource;

import java.lang.annotation.*;

/**
 * This is an annotation to configure the {@link IpogNeg} TestInputGroupGenerator
 * via {@link IpogNegConfiguration}.
 *
 * It defines a {@link ConfigurationSource} with the {@link IpogNegConfigurationProvider}
 * to create new instances of a {@link IpogNegConfiguration}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationSource(
        provider = IpogNegConfigurationProvider.class,
        configurable = IpogNeg.class,
        configuration = IpogNegConfiguration.class
)
public @interface ConfigureIpogNeg {

    /**
     * Gets the class of the {@link ConstraintCheckerFactory ConstraintCheckerFactory}
     *
     * @return the class used to create a {@link de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker}
     * for a {@link IpogNeg}.
     * Need to have a no-args constructor
     */
    Class<? extends ConstraintCheckerFactory> constraintCheckerFactory()
            default MinimalForbiddenTuplesCheckerFactory.class;

    int strengthA() default 0;
}
