package de.rwth.swc.coffee4j.engine.configuration;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestInputGroupContextTest {
    
    private static final TestInputGroupGenerator GENERATOR = Mockito.mock(TestInputGroupGenerator.class);
    
    @Test
    void preconditions() {
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroupContext(null, GENERATOR));
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroupContext("test", null));
    }
    
    @Test
    void testValues() {
        final TestInputGroupContext context = new TestInputGroupContext("test", GENERATOR);
        assertEquals("test", context.getIdentifier());
        assertEquals(GENERATOR, context.getGenerator());
    }
}
