package de.rwth.swc.coffee4j.engine.process.phase.model.constraints.tuplebased;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.tuplebased.TupleBasedConstraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TupleBasedConstraintTest {
    @Test
    void preconditions() {
        Assertions.assertThrows(NullPointerException.class, () -> new TupleBasedConstraint(null, null, Mockito.mock(Combination.class)));
        Assertions.assertThrows(NullPointerException.class, () -> new TupleBasedConstraint("", null, Mockito.mock(Combination.class)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TupleBasedConstraint("", Collections.emptyList(), null));
        Assertions.assertThrows(NullPointerException.class, () -> new TupleBasedConstraint("", Collections.emptyList(), null, Mockito.mock(Combination.class)));
    }

    @Test
    void canCreateConstraint() {
        final List<String> parameterNames = Arrays.asList("first", "second");
        List<Value> values = new ArrayList<>();
        values.add(new Value(0, 0));
        values.add(new Value(1, 1));
        values.add(new Value(2, 2));
    
        final Combination combination = Combination.of(Map.of(new Parameter("first", values), values.get(0)));

        final TupleBasedConstraint constraint = new TupleBasedConstraint("", parameterNames, combination);

        assertEquals(parameterNames, constraint.getParameterNames());
        assertEquals(combination, constraint.getCombination());
        assertTrue(constraint.checkIfValid(Combination.of(Map.of(new Parameter("first", values), values.get(1)))));
        assertTrue(constraint.checkIfValid(Combination.of(Map.of(new Parameter("first", values), values.get(2)))));
        assertFalse(constraint.checkIfValid(combination));
        assertEquals(ConstraintStatus.UNKNOWN, constraint.getConstraintStatus() );
    }
}
