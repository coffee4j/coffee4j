package de.rwth.swc.coffee4j.engine.converter.tuplebased;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.tuplebased.TupleBasedConstraint;
import de.rwth.swc.coffee4j.engine.converter.constraints.IndexBasedConstraintConverter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TupleBasedConstraintConverterTest {
    @Test
    void tupleBasedConstraintConverterTest() {
        List<Value> list1 = new ArrayList<>();
        Value value1 = new Value(0, "eins");
        Value value2 = new Value(1, "zwei");
        Value value3 = new Value(2, "drei");
        list1.add(value1);
        list1.add(value2);
        list1.add(value3);
        Parameter parameter1 = new Parameter("Parameter 1", list1);

        List<Value> list2 = new ArrayList<>();
        Value value4 = new Value(3, 3);
        Value value5 = new Value(4, 4);
        Value value6 = new Value(5, 5);
        list2.add(value4);
        list2.add(value5);
        list2.add(value6);
        Parameter parameter2 = new Parameter("Parameter 2", list2);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);

        Map<Parameter, Value> parameterValueMap = new LinkedHashMap<>();
        parameterValueMap.put(parameter1, value2);
        parameterValueMap.put(parameter2, value6);

        Combination combination = Combination.of(parameterValueMap);
        List<String> parameterNames = new ArrayList<>();
        parameterNames.add(parameter1.getName());
        parameterNames.add(parameter2.getName());

        Constraint constraint = new TupleBasedConstraint("constraint", parameterNames, combination);

        IndexBasedConstraintConverter converter = constraint.getConverterFactory().create(parameters);

        TupleList tupleList = converter.convert(constraint, 0);

        assertArrayEquals(new int[]{0,1}, tupleList.getInvolvedParameters());
        assertArrayEquals(new int[]{1,2}, tupleList.getTuples().get(0));
    }
}
