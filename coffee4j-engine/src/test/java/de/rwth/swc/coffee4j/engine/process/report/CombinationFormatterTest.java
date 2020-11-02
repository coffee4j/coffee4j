package de.rwth.swc.coffee4j.engine.process.report;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.process.report.util.CombinationFormatter;
import de.rwth.swc.coffee4j.engine.process.report.util.JavaFormatter;
import de.rwth.swc.coffee4j.engine.process.report.util.NoOpFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombinationFormatterTest {
    private Combination combination;

    @BeforeEach
    void createCombination() {
        List<Value> values = new ArrayList<>();

        values.add(new Value(0, 0));
        values.add(new Value(1, 1));

        combination = Combination.of(Map.of(new Parameter("param", values), values.get(1)));
    }

    @Test
    void testNoOpFormatter() {
        CombinationFormatter formatter = new NoOpFormatter();
        assertEquals(combination.toString(), formatter.format(combination));
    }

    @Test
    void testJavaFormatter() {
        CombinationFormatter formatter = new JavaFormatter();
        assertEquals("constrain(\"param\").by((Object param) -> !(param.equals(1)))", formatter.format(combination));
    }
}
