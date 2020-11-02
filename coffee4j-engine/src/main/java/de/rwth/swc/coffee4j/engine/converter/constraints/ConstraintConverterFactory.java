package de.rwth.swc.coffee4j.engine.converter.constraints;

import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;

import java.util.List;

@FunctionalInterface
public interface ConstraintConverterFactory {
    /**
     * @param parameters list of {@link Parameter} for which a constraint converter is created.
     * @return a new {@link IndexBasedConstraintConverter}
     */
    IndexBasedConstraintConverter create(List<Parameter> parameters);
}
