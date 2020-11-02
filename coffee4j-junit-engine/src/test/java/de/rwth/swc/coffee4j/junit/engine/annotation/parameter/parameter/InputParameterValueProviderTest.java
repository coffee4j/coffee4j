package de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static java.lang.Integer.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputParameterValueProviderTest {
    
    @SuppressWarnings("UnnecessaryBoxing")
    private static final Combination COMBINATION = Combination.of(Map.of(
            parameter("first").values(valueOf(0), valueOf(1)).build(), value(1, valueOf(1)),
            parameter("second").values("value1", "value2").build(), value(0, "value1"),
            parameter("third").values(1.1, 2.2).build(), value(1, 2.2)));
    
    @Test
    @SuppressWarnings("ConstantConditions")
    void doesNotAcceptNullValues() {
        final InputParameterValueProvider provider = new InputParameterValueProvider();
        
        assertThrows(NullPointerException.class, () -> provider.accept(null));
        assertThrows(NullPointerException.class, () -> provider.provideValue(null));
    }
    
    @Test
    void providesValueForUnboxedType() throws NoSuchMethodException {
        final InputParameterValueProvider provider = new InputParameterValueProvider();
        
        final InputParameter annotation = getAnnotationForParameterAtIndex(0);
        provider.accept(annotation);
        
        final ParameterContext context = getContextForParameterAtIndex(0);
        final Object providedValue = provider.provideValue(context);
        
        assertEquals(1, providedValue);
    }
    
    @Test
    void providesValueForReferenceType() throws NoSuchMethodException {
        final InputParameterValueProvider provider = new InputParameterValueProvider();
        
        final InputParameter annotation = getAnnotationForParameterAtIndex(1);
        provider.accept(annotation);
        
        final ParameterContext context = getContextForParameterAtIndex(1);
        final Object providedValue = provider.provideValue(context);
        
        assertEquals("value1", providedValue);
    }
    
    @Test
    void throwsExceptionForUnassignableType() throws NoSuchMethodException {
        final InputParameterValueProvider provider = new InputParameterValueProvider();
        
        final InputParameter annotation = getAnnotationForParameterAtIndex(2);
        provider.accept(annotation);
        
        final ParameterContext context = getContextForParameterAtIndex(2);
        
        final Coffee4JException exception = assertThrows(Coffee4JException.class, () -> provider.provideValue(context));
        assertThat(exception.getMessage())
                .contains("not assignable");
    }
    
    @Test
    void throwsExceptionForNonExistingParameter() throws NoSuchMethodException {
        final InputParameterValueProvider provider = new InputParameterValueProvider();
        
        final InputParameter annotation = getAnnotationForParameterAtIndex(3);
        provider.accept(annotation);
        
        final ParameterContext context = getContextForParameterAtIndex(3);
        
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> provider.provideValue(context));
        assertThat(exception.getMessage())
                .contains("There is no parameter with name \"fourth\"");
    }
    
    private InputParameter getAnnotationForParameterAtIndex(int index) throws NoSuchMethodException {
        final Parameter parameter = getParameterAtIndex(index);
        return parameter.getAnnotation(InputParameter.class);
    }
    
    private ParameterContext getContextForParameterAtIndex(int index) throws NoSuchMethodException {
        final Parameter parameter = getParameterAtIndex(index);
        return ParameterContext.of(parameter, COMBINATION);
    }
    
    private Parameter getParameterAtIndex(int index) throws NoSuchMethodException {
        return getClass().getMethod("testMethod", int.class, Object.class, String.class, String.class)
                .getParameters()[index];
    }
    
    @SuppressWarnings("unused")
    public void testMethod(@InputParameter("first") int parameterWithUnboxing,
            @InputParameter("second") Object parameterWithReferenceType,
            @InputParameter("third") String parameterWithWrongType,
            @InputParameter("fourth") String parameterWithNonExistingName) {
        
    }
    
}
