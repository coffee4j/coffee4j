package de.rwth.swc.coffee4j.junit.engine.annotation.parameter;

import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.combination.InputCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that a parameter of a combinatorial test method should be resolved by the given provider.
 *
 * <p>This may be used as a meta-annotation e.g. see {@link InputParameter} or {@link InputCombination}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterValueSource {
    
    /**
     * Gets the class of the {@link ParameterValueProvider} to use
     *
     * @return the class of the {@link ParameterValueProvider} to use
     */
    Class<? extends ParameterValueProvider> value();
    
}
