package de.rwth.swc.coffee4j.engine.process.report;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.report.util.NoOpFormatter;
import de.rwth.swc.coffee4j.engine.process.report.util.ReportUtility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportUtilityTest {
    
    final Combination combination1 = Mockito.mock(Combination.class);
    final Combination combination2 = Mockito.mock(Combination.class);

    @Test
    void testFormattedExceptionInducingCombinations() {
        when(combination1.toString()).thenReturn("[0,0]");
        when(combination2.toString()).thenReturn("[1,1]");

        Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations = new HashMap<>();
        exceptionInducingCombinations.put(combination1, ErrorConstraintException.class);
        exceptionInducingCombinations.put(combination2,  NullPointerException.class);

        String output = ReportUtility.getFormattedExceptionInducingCombinations(exceptionInducingCombinations, new NoOpFormatter());
        assertTrue(output.contains("[0,0]"));
        assertTrue(output.contains("[1,1]"));
        assertTrue(output.contains("Not classified"));
        assertTrue(output.contains("Type " + NullPointerException.class.getSimpleName()));

        output = ReportUtility.getFormattedExceptionInducingCombinations(new HashMap<>(), new NoOpFormatter());
        assertEquals("No exception-inducing combinations were found!", output);

        output = ReportUtility.getFormattedExceptionInducingCombinations(exceptionInducingCombinations.keySet());
        assertTrue(output.contains("[0,0]"));
        assertTrue(output.contains("[1,1]"));

        output = ReportUtility.getFormattedExceptionInducingCombinations(Collections.emptySet());
        assertEquals("No exception-inducing combinations were found!", output);
    }

    @Test
    void testFormattedFailureInducingCombinations() {
        when(combination1.toString()).thenReturn("[0,0]");
        when(combination2.toString()).thenReturn("[1,1]");

        String output = ReportUtility.getFormattedFailureInducingCombinations(Set.of(combination1, combination2));
        assertTrue(output.contains("[0,0]"));
        assertTrue(output.contains("[1,1]"));

        output = ReportUtility.getFormattedFailureInducingCombinations(Collections.emptySet());
        assertEquals("No failure-inducing combinations were found!", output);
    }
}
