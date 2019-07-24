package de.rwth.swc.coffee4j.engine.constraint;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.Variable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChocoSolverUtil {

    private ChocoSolverUtil() {
    }
    
    static public Optional<Variable> findVariable(final Model model, int parameter) {
        final String key = String.valueOf(parameter);
        
        return Arrays.stream(model.getVars())
                .filter(variable -> variable.getName().equals(key))
                .findFirst();
    }
    
    static boolean runChocoSolver(Model model, List<Constraint> temporaryConstraints) {
        final Constraint[] constraints = temporaryConstraints.toArray(new Constraint[0]);
        
        model.post(constraints);
        
        final boolean result = model.getSolver().solve();
        
        model.unpost(constraints);
        model.getSolver().reset();
        
        return result;
    }
}
