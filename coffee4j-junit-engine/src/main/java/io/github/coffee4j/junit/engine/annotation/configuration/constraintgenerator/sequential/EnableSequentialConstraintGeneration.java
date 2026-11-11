package io.github.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential;

import io.github.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;
import io.github.coffee4j.algorithmic.sequential.characterization.mixtgte.GeneratingMixtgte;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.characterization.FaultCharacterizationAlgorithmFactoryProvider;

import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSequentialConstraintGeneration {
    /**
     * @return returns the {@link GeneratingFaultCharacterizationAlgorithm} provided by this annotation.
     */
    Class<? extends GeneratingFaultCharacterizationAlgorithm> value() default GeneratingMixtgte.class;

    /**
     * @return returns a provider that can be used by the factory loader.
     */
    Class<? extends FaultCharacterizationAlgorithmFactoryProvider> getProvider() default ConstructorBasedSequentialConstraintGenerationProvider.class;
}
