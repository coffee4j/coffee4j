package de.rwth.swc.coffee4j.engine.model.constraints.methodbased;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.ConstraintFunction;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.MethodBasedConstraint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodBasedConstraintTest {

    @Test
    void preconditions() {
        Assertions.assertThrows(NullPointerException.class, () -> new MethodBasedConstraint("", null, Mockito.mock(
                ConstraintFunction.class)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MethodBasedConstraint("", Collections.emptyList(), null));
    }

    @Test
    void canCreateConstraint() {
        final List<String> parameterNames = Arrays.asList("first", "second");
        final ConstraintFunction function = list -> true;

        final MethodBasedConstraint constraint = new MethodBasedConstraint("", parameterNames, function);

        assertEquals(parameterNames, constraint.getParameterNames());

        assertEquals(function, constraint.getConstraintFunction());
    }
    
    @Test
    void shouldThrowExceptionIfValueHasWrongType() {
        final MethodBasedConstraint constraint = new MethodBasedConstraint(
                "", List.of("second"), value -> true);
        final Combination combination = Combination.of(Map.of(parameter("first").values(1, 2).build(), value(0, 1)));
        
        assertThrows(IllegalArgumentException.class, () -> constraint.checkIfValid(combination));
    }

}
