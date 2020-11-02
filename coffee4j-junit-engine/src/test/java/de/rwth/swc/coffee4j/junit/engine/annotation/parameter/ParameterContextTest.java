package de.rwth.swc.coffee4j.junit.engine.annotation.parameter;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterContextTest {
    
    @Test
    void doesNotAllowNullValues() throws NoSuchMethodException {
        final Parameter parameter = getClass().getMethod("testMethod", String.class).getParameters()[0];
        
        assertThrows(NullPointerException.class, () -> ParameterContext.of(null, Combination.empty()));
        assertThrows(NullPointerException.class, () -> ParameterContext.of(parameter, null));
    }
    
    public void testMethod(String parameter) {
        // only used as target for reflection
    }
    
}
