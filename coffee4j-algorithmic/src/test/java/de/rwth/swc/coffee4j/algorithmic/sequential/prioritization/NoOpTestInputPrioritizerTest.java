package de.rwth.swc.coffee4j.algorithmic.sequential.prioritization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoOpTestInputPrioritizerTest {
    
    @SuppressWarnings("SimplifiableJUnitAssertion")
    @Test
    void allInstancesAreEqual() {
        final NoOpTestInputPrioritizer first = new NoOpTestInputPrioritizer();
        final NoOpTestInputPrioritizer second = new NoOpTestInputPrioritizer();
        
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.toString(), second.toString());
    }
    
    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    void notEqualToOtherTestInputPrioritizers() {
        final NoOpTestInputPrioritizer noOpPrioritizer = new NoOpTestInputPrioritizer();
        final TestInputPrioritizer otherPrioritizer = (testInputs, model)  -> List.of();
        
        assertFalse(noOpPrioritizer.equals(otherPrioritizer));
        assertFalse(noOpPrioritizer.equals(null));
    }
    
}
