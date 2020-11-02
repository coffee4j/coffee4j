package de.rwth.swc.coffee4j.engine.model.constraints.methodbased;

import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.BooleanFunction2;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.ConstraintFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BooleanFunction2Test implements ConstraintFunctionTest {

    @Override
    public ConstraintFunction getFunction() {
        return (BooleanFunction2<?, ?>) (String first, String second) -> first.equals("test");
    }

    @Override
    public List<?> getTooFewValues() {
        return Collections.singletonList("test");
    }

    @Override
    public List<?> getTooManyValues() {
        return Arrays.asList("one", "two", "three");
    }

    @Override
    public List<?> getValuesEvaluatingToTrue() {
        return Arrays.asList("test", "test");
    }

    @Override
    public List<?> getValuesEvaluatingToFalse() {
        return Arrays.asList("one", "two");
    }

    @Override
    public List<?> getValuesOfWrongType() {
        return Arrays.asList("test", 2);
    }

}
