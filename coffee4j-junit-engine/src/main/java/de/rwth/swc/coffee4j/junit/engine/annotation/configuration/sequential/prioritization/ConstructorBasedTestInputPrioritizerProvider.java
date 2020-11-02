package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ConstructorBasedTestInputPrioritizerProvider
        implements TestInputPrioritizerProvider, AnnotationConsumer<EnableTestInputPrioritization> {
    
    private Class<? extends TestInputPrioritizer> prioritizerClass;
    
    @Override
    public void accept(EnableTestInputPrioritization configuration) {
        prioritizerClass = configuration.value();
    }
    
    @Override
    public TestInputPrioritizer provide(Method method) {
        try {
            final Constructor<? extends TestInputPrioritizer> constructor = prioritizerClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new Coffee4JException("Could not create a " + TestInputPrioritizer.class.getCanonicalName()
                    + " for class " + prioritizerClass.getCanonicalName(), e);
        }
    }
    
}
