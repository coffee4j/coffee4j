package de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter;

import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterValueSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signalizes that the value corresponding to the given parameter name should be injected
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ParameterValueSource(InputParameterValueProvider.class)
public @interface InputParameter {

    /**
     * Gets the name of the {@link Parameter}
     *
     * @return the name of the {@link Parameter}
     */
    String value();
    
}
