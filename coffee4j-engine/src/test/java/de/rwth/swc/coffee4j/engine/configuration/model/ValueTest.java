package de.rwth.swc.coffee4j.engine.configuration.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueTest {
    
    @Test
    void dataCanBeNull() {
        final Value value = new Value(0, null);
        
        assertNull(value.get());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void weightCanBeNull() {
        final Value value = new Value(0, "test", null);
        
        assertFalse(value.getWeight().isPresent());
        assertFalse(value.hasWeight());
        assertThrows(IllegalStateException.class, value::getRequiredWeight);
    }
    
    @Test
    void weightCanBeSet() {
        final Value value = new Value(0, "test", 2.0);
        
        assertTrue(value.getWeight().isPresent());
        assertEquals(2.0, value.getWeight().getAsDouble());
        assertTrue(value.hasWeight());
        assertEquals(2.0, value.getRequiredWeight());
    }
}
