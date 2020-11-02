package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ConfigurationSource} is an annotation used to register
 * {@linkplain FaultCharacterizationAlgorithmFactoryProvider characterization characterization providers}
 * for the annotated test class.
 * <p>
 * This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code FaultCharacterizationAlgorithmFactorySource} (demonstrated by {@link EnableFaultCharacterization}).
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FaultCharacterizationAlgorithmFactorySource {

    /**
     * Gets the class of the {@link FaultCharacterizationAlgorithmFactoryProvider}
     *
     * @return the class which provides a
     * {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory}.
     * Must have a no-args constructor
     */
    Class<? extends FaultCharacterizationAlgorithmFactoryProvider> value();
    
}
