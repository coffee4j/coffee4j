package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.TestModel;
import org.chocosolver.solver.Model;

import java.util.Collection;
import java.util.List;

class HardConstraintChecker extends ModelBasedConstraintChecker {
    
    HardConstraintChecker(final TestModel testModel,
                          List<InternalConstraint> exclusionConstraints,
                          List<InternalConstraint> errorConstraints) {
        super(createModel(testModel, exclusionConstraints, errorConstraints));
    }
    
    private static Model createModel(TestModel testModel,
                                     List<InternalConstraint> exclusionConstraints,
                                     List<InternalConstraint> errorConstraints) {
        final Model model = new Model();
        createVariables(testModel, model);
        createConstraints(exclusionConstraints, errorConstraints, model);
        
        return model;
    }
    
    private static void createVariables(TestModel testModel, Model model) {
        for (int i = 0; i < testModel.getNumberOfParameters(); i++) {
            int parameterSize = testModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private static void createConstraints(Collection<InternalConstraint> exclusionConstraints,
                                          Collection<InternalConstraint> errorConstraints,
                                          Model model) {
        for (InternalConstraint constraint : exclusionConstraints) {
            constraint.apply(model).post();
        }
        
        for (InternalConstraint errorConstraint : errorConstraints) {
            errorConstraint.apply(model).post();
        }
    }
}
