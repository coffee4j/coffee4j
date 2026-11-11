package io.github.coffee4j.engine.converter.constraints.methodbased;

import io.github.coffee4j.engine.configuration.model.Parameter;
import io.github.coffee4j.engine.converter.constraints.ConstraintConverterFactory;
import io.github.coffee4j.engine.converter.constraints.IndexBasedConstraintConverter;

import java.util.List;

/**
 * Factory for creating {@link SimpleCartesianProductConstraintConverter}s.
 */
public class SimpleCartesianProductConstraintConverterFactory implements ConstraintConverterFactory {
    @Override
    public IndexBasedConstraintConverter create(List<Parameter> parameters) {
        return new SimpleCartesianProductConstraintConverter(parameters);
    }
}
