package de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterContext;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterValueProvider;

import java.util.Map;
import java.util.Objects;

class InputParameterValueProvider implements ParameterValueProvider, AnnotationConsumer<InputParameter> {
    
    private static final Map<Class<?>, Class<?>> BOXED = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            double.class, Double.class,
            float.class, Float.class,
            int.class, Integer.class,
            long.class, Long.class,
            short.class, Short.class,
            void.class, Void.class);
    
    private String parameterName;
    
    @Override
    public void accept(InputParameter inputParameter) {
        parameterName = Objects.requireNonNull(inputParameter).value();
    }
    
    @Override
    public Object provideValue(ParameterContext parameterContext) {
        final Object combinationValue = parameterContext.getCombination().getRawValue(parameterName);

        if(combinationValue == null) {
            return null;
        }

        final Class<?> methodParameterType = parameterContext.getParameter().getType();
        final Class<?> valueType = combinationValue.getClass();
    
        if (!isAssignableType(methodParameterType, valueType)) {
            throw new Coffee4JException("Parameter \"" + parameterName
                    + "\" not assignable. Expected type " + methodParameterType.getCanonicalName() + " but was "
                    + valueType.getCanonicalName());
        }
        
        return combinationValue;
    }
    
    private boolean isAssignableType(Class<?> parameterType, Class<?> valueType) {
        final Class<?> boxedParameterType = parameterType.isPrimitive() ? BOXED.get(parameterType) : parameterType;
        final Class<?> boxedValueType = valueType.isPrimitive() ? BOXED.get(valueType) : valueType;
        
        return boxedParameterType.isAssignableFrom(boxedValueType);
    }
    
}
