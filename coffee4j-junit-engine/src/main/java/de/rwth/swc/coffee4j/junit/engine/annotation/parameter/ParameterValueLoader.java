package de.rwth.swc.coffee4j.junit.engine.annotation.parameter;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Parameter;

/**
 * Loads the object which is supposed to injected for a method parameter for a given {@link ParameterContext}.
 *
 * <p>Assumes that there is exactly one {@link ParameterValueSource} (meta-)annotation present on the parameter
 * which may be initialized with a {@link AnnotationConsumerInitializer}.
 */
public class ParameterValueLoader {
    
    public Object load(ParameterContext context) {
        final Parameter parameter = context.getParameter();
        
        final ParameterValueProvider provider = AnnotationSupport.findAnnotation(parameter, ParameterValueSource.class)
                .map(ParameterValueSource::value)
                .map(ReflectionSupport::newInstance)
                .map(instance -> AnnotationConsumerInitializer.initialize(parameter, instance))
                .orElseThrow(() -> new Coffee4JException(
                        "Could not find " + ParameterValueProvider.class + " for parameter " + parameter  + "."));
    
        return provider.provideValue(context);
    }
    
}
