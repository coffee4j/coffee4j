package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a {@link TestInputPrioritizerSource} which uses the {@link ConstructorBasedTestInputPrioritizerProvider}
 * to create new instances of a {@link TestInputPrioritizer} by instantiation the respective algorithm via a
 * no-args constructor.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@TestInputPrioritizerSource(ConstructorBasedTestInputPrioritizerProvider.class)
public @interface EnableTestInputPrioritization {
    
    /**
     * Specified which {@link TestInputPrioritizer} should be used for the combinatorial test.
     *
     * @return the class of a {@link TestInputPrioritizer} which has a no-args constructor
     */
    Class<? extends TestInputPrioritizer> value();
    
}
