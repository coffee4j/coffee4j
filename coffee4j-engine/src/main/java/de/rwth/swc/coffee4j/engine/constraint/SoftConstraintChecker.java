package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.Collection;

class SoftConstraintChecker extends ModelBasedConstraintChecker {
    
    SoftConstraintChecker(final InputParameterModel inputParameterModel, Collection<InternalConstraint> hardConstraints, Collection<InternalConstraint> softConstraints, int threshold) {
        super(createModel(inputParameterModel, hardConstraints, softConstraints, threshold));
    }
    
    private static Model createModel(InputParameterModel inputParameterModel, Collection<InternalConstraint> hardConstraints, Collection<InternalConstraint> softConstraints, int threshold) {
        final Model model = new Model();
        createVariables(inputParameterModel, model);
        createHardConstraints(inputParameterModel, hardConstraints, model);
        createSoftConstraints(inputParameterModel, softConstraints, threshold, model);
        
        return model;
    }
    
    private static void createVariables(InputParameterModel inputParameterModel, Model model) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private static void createHardConstraints(InputParameterModel inputParameterModel, Collection<InternalConstraint> hardConstraints, Model model) {
        for (InternalConstraint constraint : hardConstraints) {
            constraint.post(inputParameterModel, model);
        }
    }
    
    private static void createSoftConstraints(InputParameterModel inputParameterModel, Collection<InternalConstraint> softConstraints, int threshold, Model model) {
        IntVar[] reifiedVars = new IntVar[softConstraints.size()];
        int index = 0;
        
        for (InternalConstraint constraint : softConstraints) {
            reifiedVars[index++] = constraint.apply(inputParameterModel, model).reify().intVar();
        }
        
        int[] weights = new int[softConstraints.size()];
        Arrays.fill(weights, 1);
        IntVar sum = model.intVar("sum", 0, softConstraints.size());
        
        model.arithm(sum, ">=", threshold).post();
        model.scalar(reifiedVars, weights, "=", sum).post();
    }
    
}
