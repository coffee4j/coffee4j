package de.rwth.swc.coffee4j.junit.engine.annotation.parameter.combination;

import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterValueSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ParameterValueSource(InputCombinationValueProvider.class)
public @interface InputCombination {
}
