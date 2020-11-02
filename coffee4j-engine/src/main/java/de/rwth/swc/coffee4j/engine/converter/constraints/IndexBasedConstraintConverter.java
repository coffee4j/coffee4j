package de.rwth.swc.coffee4j.engine.converter.constraints;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.engine.converter.model.IndexBasedModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;

import java.util.List;

/**
 * Used by {@link IndexBasedModelConverter} to convert a list of {@link Constraint} to a list of {@link TupleList}.
 */
public interface IndexBasedConstraintConverter {
    
    /**
     * Converts all constraints to {@link TupleList}s by using the index based schema explained in {@link ModelConverter}.
     * The constraints need to be converted in order.
     *
     * @param constraints all {@link Constraint}s which need to be converted. Must not be {@code null} but can be empty
     * @param lastId last id that has been assigned.
     * @return the converted constraints in the same order as the given constraints
     */
    List<TupleList> convert(List<Constraint> constraints, int lastId);

    /**
     * Converts the constraint to a {@link TupleList} by using the index based schema explained in {@link ModelConverter}.
     *
     * @param constraint {@link Constraint} that needs to be converted. Must not be {@code null}.
     * @param lastId last id that has been assigned.
     * @return the converted {@link Constraint}.
     */
    TupleList convert(Constraint constraint, int lastId);
}
