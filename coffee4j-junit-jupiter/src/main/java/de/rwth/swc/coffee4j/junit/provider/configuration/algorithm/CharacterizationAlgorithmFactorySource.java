package de.rwth.swc.coffee4j.junit.provider.configuration.algorithm;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ConfigurationSource} is an annotation used to register
 * {@linkplain CharacterizationAlgorithmFactoryProvider characterization algorithm providers} for the annotated test method.
 * <p>
 * This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code CharacterizationAlgorithmFactorySource} (demonstrated by {@link CharacterizationAlgorithm}).
 * <p>
 * This is more or less a copy of {@link org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CharacterizationAlgorithmFactorySource {
    
    /**
     * @return the class which provides a{@link FaultCharacterizationAlgorithmFactory}
     * . Must have a no-args constructor
     */
    Class<? extends CharacterizationAlgorithmFactoryProvider> value();
    
}
