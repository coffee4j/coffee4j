package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.report.CombinationArgument;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CombinationArgumentConverterTest {
    
    @Test
    void canResolveCombinationArgument() {
        final CombinationArgumentConverter resolver = new CombinationArgumentConverter();
        assertTrue(resolver.canConvert(new CombinationArgument(new int[0])));
    }
    
    @Test
    void cannotResolveNull() {
        final CombinationArgumentConverter resolver = new CombinationArgumentConverter();
        assertFalse(resolver.canConvert(null));
    }
    
    @Test
    void cannotResolveOtherClass() {
        final CombinationArgumentConverter resolver = new CombinationArgumentConverter();
        assertFalse(resolver.canConvert("test"));
    }
    
    @Test
    void resolvesCombination() {
        final ModelConverter converter = Mockito.mock(ModelConverter.class);
        when(converter.convertCombination(any(int[].class))).thenReturn(Combination.empty());
        final int[] combination = new int[0];
        final CombinationArgumentConverter resolver = new CombinationArgumentConverter();
        
        resolver.initialize(converter);
        assertEquals(Combination.empty(), resolver.convert(new CombinationArgument(combination)));
    }
    
}
