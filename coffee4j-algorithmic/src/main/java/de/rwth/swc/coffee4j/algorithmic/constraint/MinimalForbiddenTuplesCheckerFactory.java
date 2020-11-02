package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.checkValidIdentifier;

/**
 * Factory for creating a {@link MinimalForbiddenTuplesChecker}.
 */
public class MinimalForbiddenTuplesCheckerFactory implements ConstraintCheckerFactory {
    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        return new MinimalForbiddenTuplesChecker(testModel);
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        Preconditions.check(checkValidIdentifier(testModel, toBeNegated.getId()));

        final TupleList negatedErrorConstraint = buildNegatedErrorConstraint(testModel, toBeNegated);

        final List<TupleList> errorConstraints = new ArrayList<>(testModel.getErrorTupleLists());
        errorConstraints.remove(toBeNegated);
        errorConstraints.add(negatedErrorConstraint);

        final CompleteTestModel newTestModel = CompleteTestModel.builder(testModel)
                .errorTupleLists(errorConstraints)
                .build();

        return new MinimalForbiddenTuplesChecker(newTestModel);
    }

    private TupleList buildNegatedErrorConstraint(CompleteTestModel testModel, TupleList toBeNegated) {
        final Int2IntMap relevantParameterSizes = new Int2IntOpenHashMap();
        for (int parameter : toBeNegated.getInvolvedParameters()) {
            relevantParameterSizes.put(parameter, testModel.getParameterSize(parameter));
        }

        final MinimalForbiddenTuplesChecker tempChecker = new MinimalForbiddenTuplesChecker(
                CompleteTestModel.builder(testModel)
                        .exclusionTupleLists(List.of())
                        .errorTupleLists(List.of(toBeNegated))
                        .build());

        // collect all tuples that are valid concerning the constraint to be negated
        final Collection<int[]> complementTuples = Combinator
                .computeCartesianProduct(relevantParameterSizes, testModel.getNumberOfParameters())
                .stream()
                .filter(tempChecker::isValid)
                .collect(Collectors.toList());

        final List<int[]> forbiddenValueCombinations = new ArrayList<>(complementTuples.size());

        // format tuples
        for (int[] combination : complementTuples) {
            int[] forbiddenCombination = new int[toBeNegated.getInvolvedParameters().length];
            int param = 0;

            for (int parameter : toBeNegated.getInvolvedParameters()) {
                forbiddenCombination[param] = combination[parameter];
                param++;
            }

            forbiddenValueCombinations.add(forbiddenCombination);
        }

        return new TupleList(toBeNegated.getId(), toBeNegated.getInvolvedParameters(), forbiddenValueCombinations);
    }
}
