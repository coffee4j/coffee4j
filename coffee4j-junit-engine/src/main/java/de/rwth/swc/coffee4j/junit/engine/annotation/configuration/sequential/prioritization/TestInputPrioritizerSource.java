package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a {@link TestInputPrioritizerProvider} to a combinatorial test method.
 *
 * <p>May be used as a meta-annotation as demonstrated by {@link EnableTestInputPrioritization}.
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestInputPrioritizerSource {
    
    /**
     * Gets the class of the {@link TestInputPrioritizerProvider}
     *
     * @return the class which provides a {@link TestInputPrioritizer}. Must have a no-args constructor
     */
    Class<? extends TestInputPrioritizerProvider> value();
    
}
