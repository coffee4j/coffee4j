package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.chocosolver.solver.Model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DynamicHardConstraintChecker extends ModelBasedConstraintChecker {

    private final IntSet involvedParameters = new IntArraySet();
    private final ConstraintConverter converter = new ConstraintConverter();

    public DynamicHardConstraintChecker(final CompleteTestModel inputParameterModel,
                                        List<Constraint> exclusionConstraints,
                                        List<Constraint> errorConstraints) {
        super(createModel(inputParameterModel, exclusionConstraints, errorConstraints));
        inputParameterModel.getErrorTupleLists().stream().map(TupleList::getInvolvedParameters).flatMapToInt(Arrays::stream).forEach(involvedParameters::add);
        inputParameterModel.getExclusionTupleLists().stream().map(TupleList::getInvolvedParameters).flatMapToInt(Arrays::stream).forEach(involvedParameters::add);
    }

    private static Model createModel(CompleteTestModel inputParameterModel,
                                     Collection<Constraint> exclusionConstraints,
                                     Collection<Constraint> errorConstraints) {
        final Model model = new Model();
        model.getSettings().setCheckDeclaredConstraints(false);

        createVariables(inputParameterModel, model);
        createConstraints(exclusionConstraints, errorConstraints, model);

        return model;
    }

    private static void createVariables(CompleteTestModel inputParameterModel, Model model) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);

            model.intVar(key, 0, parameterSize - 1);
        }
    }

    private static void createConstraints(Collection<Constraint> exclusionConstraints,
                                          Collection<Constraint> errorConstraints,
                                          Model model) {
        for (Constraint constraint : exclusionConstraints) {
            constraint.apply(model).post();
        }

        for (Constraint errorConstraint : errorConstraints) {
            errorConstraint.apply(model).post();
        }
    }

    @Override
    public void addConstraint(int[] forbiddenCombination) {
        int numberOfSetParameters = CombinationUtil.numberOfSetParameters(forbiddenCombination);
        if (numberOfSetParameters == 0) {
            model.post(model.falseConstraint());
        } else {
            int[] parameters = new int[numberOfSetParameters];
            int[] values = new int[numberOfSetParameters];
            int current = 0;

            for (int parameter = 0; parameter < forbiddenCombination.length; parameter++) {
                if (forbiddenCombination[parameter] != CombinationUtil.NO_VALUE) {
                    involvedParameters.add(parameter);
                    parameters[current] = parameter;
                    values[current] = forbiddenCombination[parameter];
                    current++;
                }
            }

            model.post(converter.createConstraints(parameters, values, model).getOpposite());
        }
    }

    public IntSet getInvolvedParameters() {
        return involvedParameters;
    }
}
