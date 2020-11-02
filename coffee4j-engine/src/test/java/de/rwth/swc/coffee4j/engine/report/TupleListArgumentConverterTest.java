package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.MethodBasedConstraint;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TupleListArgumentConverterTest {
    
    @Test
    void canConvertTuplesListArgument() {
        final TupleListArgumentConverter converter = new TupleListArgumentConverter();
        assertTrue(converter.canConvert(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{0}))));
    }
    
    @Test
    void cannotConvertNull() {
        final TupleListArgumentConverter converter = new TupleListArgumentConverter();
        assertFalse(converter.canConvert(null));
    }
    
    @Test
    void cannotConvertOtherClass() {
        final TupleListArgumentConverter converter = new TupleListArgumentConverter();
        assertFalse(converter.canConvert("test"));
    }
    
    @Test
    void convertTuplesList() {
        final MethodBasedConstraint
                resolvedConstraint = new MethodBasedConstraint("", Collections.singletonList("test"), list -> true, ConstraintStatus.UNKNOWN);
        final ModelConverter modelConverter = Mockito.mock(ModelConverter.class);
        when(modelConverter.convertConstraint(any(TupleList.class))).thenReturn(resolvedConstraint);
        final TupleListArgumentConverter converter = new TupleListArgumentConverter();
        
        converter.initialize(modelConverter);
        assertEquals(resolvedConstraint, converter.convert(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{0}))));
    }
    
}
