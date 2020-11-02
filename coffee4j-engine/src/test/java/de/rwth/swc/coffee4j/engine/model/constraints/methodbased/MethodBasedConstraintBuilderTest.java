package de.rwth.swc.coffee4j.engine.model.constraints.methodbased;

import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodBasedConstraintBuilderTest {
    
    @Test
    void parameterNamesCannotContainNull() {
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder.constrain(null));
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder.constrain("first", (String) null));
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder.constrain("first", "second", (String) null));
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder.constrain("first", "second", "third", (String) null));
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder
                .constrain("first", "second", "third", "fourth", (String) null));
        assertThrows(IllegalArgumentException.class, () -> ConstraintBuilder
                .constrain("first", "second", "third", "fourth", "fifth", (String) null));
    }
    
    @Test
    void constraintByReturnsConstraintWithCorrectParametersAndFunction() {
        final BooleanFunction1<?> firstFunction = Mockito.mock(BooleanFunction1.class);
        final BooleanFunction2<?, ?> secondFunction = Mockito.mock(BooleanFunction2.class);
        final BooleanFunction3<?, ?, ?> thirdFunction = Mockito.mock(BooleanFunction3.class);
        final BooleanFunction4<?, ?, ?, ?> fourthFunction = Mockito.mock(BooleanFunction4.class);
        final BooleanFunction5<?, ?, ?, ?, ?> fifthFunction = Mockito.mock(BooleanFunction5.class);
        final BooleanFunction6<?, ?, ?, ?, ?, ?> sixthFunction = Mockito.mock(BooleanFunction6.class);
        
        final MethodBasedConstraint firstConstraint = ConstraintBuilder.constrain("first").by(firstFunction);
        final MethodBasedConstraint secondConstraint = ConstraintBuilder.constrain("first", "second").by(secondFunction);
        final MethodBasedConstraint thirdConstraint = ConstraintBuilder.constrain("first", "second", "third").by(thirdFunction);
        final MethodBasedConstraint fourthConstraint = ConstraintBuilder.constrain("first", "second", "third", "fourth").by(fourthFunction);
        final MethodBasedConstraint fifthConstraint = ConstraintBuilder
                .constrain("first", "second", "third", "fourth", "fifth").by(fifthFunction);
        final MethodBasedConstraint sixthConstraint = ConstraintBuilder
                .constrain("first", "second", "third", "fourth", "fifth", "sixth").by(sixthFunction);
        
        Assertions.assertEquals(Collections.singletonList("first"), firstConstraint.getParameterNames());
        Assertions.assertEquals(Arrays.asList("first", "second"), secondConstraint.getParameterNames());
        Assertions.assertEquals(Arrays.asList("first", "second", "third"), thirdConstraint.getParameterNames());
        Assertions.assertEquals(Arrays.asList("first", "second", "third", "fourth"), fourthConstraint.getParameterNames());
        Assertions.assertEquals(Arrays.asList("first", "second", "third", "fourth", "fifth"), fifthConstraint.getParameterNames());
        Assertions.assertEquals(Arrays.asList("first", "second", "third", "fourth", "fifth", "sixth"), sixthConstraint.getParameterNames());

        assertEquals(firstFunction, firstConstraint.getConstraintFunction());
        assertEquals(secondFunction, secondConstraint.getConstraintFunction());
        assertEquals(thirdFunction, thirdConstraint.getConstraintFunction());
        assertEquals(fourthFunction, fourthConstraint.getConstraintFunction());
        assertEquals(fifthFunction, fifthConstraint.getConstraintFunction());
        assertEquals(sixthFunction, sixthConstraint.getConstraintFunction());
    }
    
}
