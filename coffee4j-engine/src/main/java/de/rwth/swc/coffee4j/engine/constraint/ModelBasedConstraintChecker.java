package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ModelBasedConstraintChecker implements ConstraintChecker {
    
    private final Model model;
    
    ModelBasedConstraintChecker(Model model) {
        this.model = Preconditions.notNull(model);
    }
    
    @Override
    public boolean isSatisfiable() {
        model.getSolver().reset();
        
        return model.getSolver().solve();
    }
    
    @Override
    public boolean isValid(final int[] combination) {
        final List<Constraint> constraintsList = createAssignmentConstraints(combination, model);
        
        return ChocoSolverUtil.runChocoSolver(model, constraintsList);
    }
    
    private List<Constraint> createAssignmentConstraints(final int[] combination, final Model model) {
        final List<Constraint> constraints = new ArrayList<>();
        
        for (int i = 0; i < combination.length; i++) {
            if (combination[i] != -1) {
                addAssignmentConstraint(i, combination[i], model, constraints);
            }
        }
        
        return constraints;
    }

    public static void addAssignmentConstraint(int parameter, int value, Model model, List<Constraint> constraints) {
        if (value != -1) {
            final Optional<Variable> candidate = ChocoSolverUtil.findVariable(model, parameter);
            
            if (candidate.isPresent() && candidate.get() instanceof IntVar) {
                final IntVar variable = (IntVar) candidate.get();
                
                final Constraint constraint = model.arithm(variable, "=", value);
                constraints.add(constraint);
            } else {
                // If you reach this branch, there's a programming error somewhere else"
                throw new IllegalStateException("INTERNAL-ERROR: " + value + " belongs to unknown parameter " + parameter);
            }
        }
    }
    
    @Override
    public boolean isExtensionValid(int[] combination, int... parameterValues) {
        Preconditions.check(parameterValues.length % 2 == 0);
        
        final List<Constraint> constraintsList = createAssignmentConstraints(combination, model);
        
        for (int i = 0; i < parameterValues.length; i += 2) {
            addAssignmentConstraint(parameterValues[i], parameterValues[i + 1], model, constraintsList);
        }
        
        return ChocoSolverUtil.runChocoSolver(model, constraintsList);
    }
    
    @Override
    public boolean isDualValid(int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);
        
        final List<Constraint> constraintsList = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            addAssignmentConstraint(parameters[i], values[i], model, constraintsList);
        }
        
        return ChocoSolverUtil.runChocoSolver(model, constraintsList);
    }
}
