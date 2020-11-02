package de.rwth.swc.coffee4j.engine.model.constraints.methodbased;

import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.BooleanFunction3;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.ConstraintFunction;

import java.util.Arrays;
import java.util.List;

class BooleanFunction3Test implements ConstraintFunctionTest {

    @Override
    public ConstraintFunction getFunction() {
        return (BooleanFunction3<?, ?, ?>) (String first, String second, String third) -> first.equals("test");
    }

    @Override
    public List<?> getTooFewValues() {
        return Arrays.asList("one", "two");
    }

    @Override
    public List<?> getTooManyValues() {
        return Arrays.asList("one", "two", "three", "four");
    }

    @Override
    public List<?> getValuesEvaluatingToTrue() {
        return Arrays.asList("test", "test", "test");
    }

    @Override
    public List<?> getValuesEvaluatingToFalse() {
        return Arrays.asList("onw", "two", "three");
    }

    @Override
    public List<?> getValuesOfWrongType() {
        return Arrays.asList("one", "two", 3);
    }

}
