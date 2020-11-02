package de.rwth.swc.coffee4j.engine.converter.constraints.methodbased;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.ConstraintFunction;
import de.rwth.swc.coffee4j.engine.converter.constraints.AbstractIndexBasedConstraintConverter;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.MethodBasedConstraint;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintStatus;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.*;

/**
 * Converts {@link MethodBasedConstraint} objects to their {@link TupleList} representation by executing their
 * {@link ConstraintFunction} with every possible value combination of its
 * parameters. This means the cartesian product of its parameters values is used.
 * For example, if a constraint uses parameters "param1" with value "1" and "2" and "param2" with value "5" and "6",
 * then the constraint it tested with all these combinations: {"1", "5"}, {"1", "6"}, {"2", "5"} ,{"2", "6"}.
 * For all combinations for which the {@link ConstraintFunction}
 * returns {@code false}, a corresponding tuple is added to the {@link TupleList} representation.
 * Naturally, this is a very expensive conversion process, especially if there are many large parameters involved.
 */
public class SimpleCartesianProductConstraintConverter extends AbstractIndexBasedConstraintConverter {
    /**
     * @param parameters parameters containing the values for the conversion. Must not be {@code null}.
     */
    public SimpleCartesianProductConstraintConverter(List<Parameter> parameters) {
        super(parameters);
    }

    @Override
    protected TupleList convertedConstraint(Constraint constraint, Object2IntMap<String> parameterIdMap, Int2ObjectMap<Parameter> idToParameterMap, int id) {
        Preconditions.check(constraint instanceof MethodBasedConstraint);

        int[] relevantParameters = constraint.getParameterNames().stream().mapToInt(parameterIdMap::getInt).toArray();
        final Int2IntMap relevantParameterSizes = computeSizeMap(idToParameterMap, relevantParameters);
        
        final Collection<int[]> cartesianProduct = Combinator.computeCartesianProduct(relevantParameterSizes, relevantParameters.length);
        final List<int[]> tuples = new ArrayList<>();
        
        for (int[] combination : cartesianProduct) {
            final Combination convertedCombination = mapToCombination(combination, relevantParameters, idToParameterMap);
            if (!constraint.checkIfValid(convertedCombination)) {
                int[] tuple = new int[combination.length];
                System.arraycopy(combination, 0, tuple, 0, combination.length);
                tuples.add(tuple);
            }
        }
        
        if (tuples.isEmpty()) {
            throw new IllegalArgumentException("Constraint \"" + constraint.getName()
                    + "\" on parameters \"" + constraint.getParameterNames() + "\" allows all values. "
                    + "This is most likely an error in the constraint function.");
        } else if (tuples.size() == cartesianProduct.size()) {
            throw new IllegalArgumentException("Constraint \"" + constraint.getName()
                    + "\" on parameters \"" + constraint.getParameterNames() + "\" does not permit any values. "
                    + "This is most likely an error in the constraint function.");
        }
        
        return new TupleList(id, relevantParameters, tuples, constraint.getConstraintStatus().equals(ConstraintStatus.CORRECT));
    }
    
    private Int2IntMap computeSizeMap(Int2ObjectMap<Parameter> idToParameterMap, int[] relevantKeys) {
        final Int2IntMap subMap = new Int2IntOpenHashMap();
        
        for (int i = 0; i < relevantKeys.length; i++) {
            final Parameter relevantParameter = idToParameterMap.get(relevantKeys[i]);
            subMap.put(i, relevantParameter.size());
        }
        
        return subMap;
    }

    private Combination mapToCombination(int[] combination, int[] relevantParameters, Int2ObjectMap<Parameter> idToParameterMap) {
        Map<Parameter, Value> parameterValueMap = new HashMap<>();

        for (int i = 0; i < relevantParameters.length; i++) {
            final int valueId = combination[i];

            Parameter parameter = idToParameterMap.get(relevantParameters[i]);
            Value value = parameter.getValues().get(valueId);

            parameterValueMap.put(parameter, value);
        }

        return Combination.of(parameterValueMap);
    }
}
