package de.rwth.swc.coffee4j.engine.converter.tuplebased;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import de.rwth.swc.coffee4j.engine.converter.constraints.AbstractIndexBasedConstraintConverter;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.tuplebased.TupleBasedConstraint;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Converter for {@link TupleBasedConstraint}s. Maps each parameter-value to the internal index-based representation.
 */
public class TupleBasedConstraintConverter extends AbstractIndexBasedConstraintConverter {
    /**
     * @param parameters parameters containing the values for the conversion. Must not be {@code null}.
     */
    public TupleBasedConstraintConverter(List<Parameter> parameters) {
        super(parameters);
    }

    @Override
    protected TupleList convertedConstraint(Constraint constraint, Object2IntMap<String> parameterIdMap, Int2ObjectMap<Parameter> idToParameterMap, int id) {
        Preconditions.check(constraint instanceof TupleBasedConstraint);
        Combination combination = ((TupleBasedConstraint) constraint).getCombination();

        IntList parameters = new IntArrayList();
        IntList values = new IntArrayList();

        for (Map.Entry<Parameter, Value> entry : combination.getParameterValueMap().entrySet()) {
            parameters.add(parameterIdMap.getInt(entry.getKey().getName()));

            List<Value> valueList = entry.getKey().getValues();
            Value value = entry.getValue();

            values.add(valueList.indexOf(value));
        }

        return new TupleList(id, parameters.toIntArray(), Collections.singletonList(values.toIntArray()), constraint.getConstraintStatus().equals(ConstraintStatus.CORRECT));
    }
}
