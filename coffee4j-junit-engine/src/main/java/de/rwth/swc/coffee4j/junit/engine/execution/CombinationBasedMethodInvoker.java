package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterContext;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterValueLoader;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.commons.support.ModifierSupport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class CombinationBasedMethodInvoker {

    private final Object instance;
    private final Method method;
    
    public CombinationBasedMethodInvoker(Object instance, Method method) {
        Preconditions.check(instance != null || ModifierSupport.isStatic(method),
                "Method needs to be static if the instance is null.");
        
        this.instance = instance;
        this.method = Objects.requireNonNull(method);
    }
    
    public void execute(Combination combination) throws Throwable {
        if (method.getParameters().length == 0) {
            invokeMethod(new Object[0]);
        } else {
            final ParameterValueLoader valueLoader = new ParameterValueLoader();
            final Object[] methodArguments = Arrays.stream(method.getParameters())
                    .map(parameter -> ParameterContext.of(parameter, combination))
                    .map(valueLoader::load)
                    .toArray();
            
            invokeMethod(methodArguments);
        }
    }
    
    private void invokeMethod(Object[] arguments) throws Throwable {
        boolean isAccessible = ModifierSupport.isStatic(method) ? method.canAccess(null)
                : method.canAccess(instance);
        method.setAccessible(true);
        
        try {
            method.invoke(instance, arguments);
        } catch (InvocationTargetException e) {
            method.setAccessible(isAccessible);
            throw e.getCause();
        }
        
        method.setAccessible(isAccessible);
    }

}
