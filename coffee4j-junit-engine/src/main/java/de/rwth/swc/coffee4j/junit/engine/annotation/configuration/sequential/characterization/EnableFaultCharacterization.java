package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.ben.Ben;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a {@link FaultCharacterizationAlgorithmFactorySource} which uses the
 * {@link ConstructorBasedFaultCharacterizationProvider} to create
 * new instances of a {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory} by instantiation
 * the respective {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory} class
 * via constructor accepting exactly one
 * {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration}.
 * This means that not a {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory} is needed
 * in the {@link #algorithm()} method, but instead a normal {@link FaultCharacterizationAlgorithm} which can be instantiated
 * multiple times, thus creating a {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@FaultCharacterizationAlgorithmFactorySource(ConstructorBasedFaultCharacterizationProvider.class)
public @interface EnableFaultCharacterization {

    /**
     * Gets the {@link FaultCharacterizationAlgorithm} specified by this annotation
     *
     * @return the class of a {@link FaultCharacterizationAlgorithm} which has a constructor with just one
     * {@link de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration} parameter
     */
    Class<? extends FaultCharacterizationAlgorithm> algorithm() default Ben.class;
}
