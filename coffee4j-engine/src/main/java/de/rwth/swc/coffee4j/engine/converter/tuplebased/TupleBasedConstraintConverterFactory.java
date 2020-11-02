package de.rwth.swc.coffee4j.engine.converter.tuplebased;

import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.converter.constraints.ConstraintConverterFactory;
import de.rwth.swc.coffee4j.engine.converter.constraints.IndexBasedConstraintConverter;

import java.util.List;

/**
 * Factory for creating a {@link TupleBasedConstraintConverter}.
 */
public class TupleBasedConstraintConverterFactory implements ConstraintConverterFactory {
    @Override
    public IndexBasedConstraintConverter create(List<Parameter> parameters) {
        return new TupleBasedConstraintConverter(parameters);
    }
}
