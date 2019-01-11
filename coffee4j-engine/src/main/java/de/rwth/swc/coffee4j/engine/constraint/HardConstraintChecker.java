package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import org.chocosolver.solver.Model;

import java.util.Collection;

class HardConstraintChecker extends ModelBasedConstraintChecker {
    
    HardConstraintChecker(final InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        super(createModel(inputParameterModel, exclusionConstraints, errorConstraints));
    }
    
    private static Model createModel(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        final Model model = new Model();
        createVariables(inputParameterModel, model);
        createConstraints(inputParameterModel, exclusionConstraints, errorConstraints, model);
        
        return model;
    }
    
    private static void createVariables(InputParameterModel inputParameterModel, Model model) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private static void createConstraints(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints, Model model) {
        for (InternalConstraint constraint : exclusionConstraints) {
            constraint.post(inputParameterModel, model);
        }
        
        for (InternalConstraint errorConstraint : errorConstraints) {
            errorConstraint.post(inputParameterModel, model);
        }
    }
}
