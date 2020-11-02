package de.rwth.swc.coffee4j.junit.engine.annotation.parameter.combination;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.ParameterContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputCombinationValueProviderTest {
    
    private final Combination COMBINATION = Combination.of(Map.of(
            parameter("first").values(0, 1).build(), value(0, 0),
            parameter("second").values(2, 3).build(), value(1, 3)));
    
    @Test
    @SuppressWarnings("ConstantConditions")
    void doesNotAcceptNullValue() {
        final InputCombinationValueProvider provider = new InputCombinationValueProvider();
        
        assertThrows(NullPointerException.class, () -> provider.provideValue(null));
    }
    
    @Test
    void throwsExceptionIfParameterHasWrongType() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(0);
        final InputCombinationValueProvider provider = new InputCombinationValueProvider();
        
        final Coffee4JException exception = assertThrows(Coffee4JException.class,
                () -> provider.provideValue(context));
        
        assertThat(exception.getMessage())
                .contains("must be assignable from type");
    }
    
    @Test
    void returnsGivenCombinationWithCorrectType() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(1);
        final InputCombinationValueProvider provider = new InputCombinationValueProvider();
        
        final Object providedValue = provider.provideValue(context);
        
        assertEquals(COMBINATION, providedValue);
    }
    
    private ParameterContext getContextForParameterAtIndex(int index) throws NoSuchMethodException {
        final Parameter parameter = getClass().getMethod("testMethod", String.class, Combination.class)
                .getParameters()[index];
        return ParameterContext.of(parameter, COMBINATION);
    }
    
    @SuppressWarnings("unused")
    public void testMethod(@InputCombination String parameterWithWrongType,
            @InputCombination Combination parameter) {
        // only used as target for reflection
    }
    
}
