package de.rwth.swc.coffee4j.engine.converter.constraints;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract class providing functionality and fields commonly used by all different types of converters.
 */
public abstract class AbstractIndexBasedConstraintConverter implements IndexBasedConstraintConverter {
    private final Int2ObjectMap<Parameter> idToParameterMap;
    private final Object2IntMap<String> parameterNameIdMap;

    /**
     * @param parameters parameters containing the values for the conversion. Must not be {@code null}.
     */
    public AbstractIndexBasedConstraintConverter(List<Parameter> parameters) {
        Preconditions.notNull(parameters);
        idToParameterMap = constructIdToParameterMap(parameters);
        parameterNameIdMap = constructParameterNameMap(parameters);
    }

    @Override
    public List<TupleList> convert(List<Constraint> constraints, int lastId) {
        Preconditions.notNull(constraints);
        assertContainsOnlyValidParameters(constraints, parameterNameIdMap.keySet());

        final List<TupleList> convertedConstraints = new ArrayList<>();

        for (Constraint constraint : constraints) {
            Preconditions.notNull(constraint);
            convertedConstraints.add(convertedConstraint(constraint, parameterNameIdMap, idToParameterMap, ++lastId));
        }

        return convertedConstraints;
    }

    @Override
    public TupleList convert(Constraint constraint, int lastId) {
        Preconditions.notNull(constraint);
        return convertedConstraint(constraint, parameterNameIdMap, idToParameterMap, ++lastId);
    }

    private Int2ObjectMap<Parameter> constructIdToParameterMap(List<Parameter> parameters) {
        final Int2ObjectMap<Parameter> newIdToParameterMap = new Int2ObjectOpenHashMap<>();

        for (int i = 0; i < parameters.size(); i++) {
            newIdToParameterMap.put(i, parameters.get(i));
        }

        return newIdToParameterMap;
    }

    private Object2IntMap<String> constructParameterNameMap(List<Parameter> parameters) {
        final Object2IntMap<String> parameterNameMap = new Object2IntOpenHashMap<>();

        for (int i = 0; i < parameters.size(); i++) {
            parameterNameMap.put(parameters.get(i).getName(), i);
        }

        return parameterNameMap;
    }

    private void assertContainsOnlyValidParameters(List<Constraint> constraints, Collection<String> parameterNames) {
        for (Constraint constraint : constraints) {
            Preconditions.check(parameterNames.containsAll(constraint.getParameterNames()));
        }
    }

    /**
     * template method implemented by sub-classes to convert the given {@link Constraint} into a {@link TupleList}.
     *
     * @param constraint constraint to be converted.
     * @param parameterIdMap map from parameter name to Id.
     * @param idToParameterMap map from Id to {@link Parameter}.
     * @param id id to assign.
     *
     * @return converted constraint.
     */
    protected abstract TupleList convertedConstraint(Constraint constraint, Object2IntMap<String> parameterIdMap, Int2ObjectMap<Parameter> idToParameterMap, int id);
}
