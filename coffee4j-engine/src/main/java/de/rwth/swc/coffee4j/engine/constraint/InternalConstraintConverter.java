package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class InternalConstraintConverter {

    public List<InternalConstraint> convertErrorTuples(final CombinatorialTestModel model) {
        return convertAll(model.getErrorTupleLists());
    }
    
    public List<InternalConstraint> convertForbiddenTuples(final CombinatorialTestModel model) {
        return convertAll(model.getForbiddenTupleLists());
    }
    
    private List<InternalConstraint> convertAll(final List<TupleList> tupleLists) {
        final List<InternalConstraint> constraints = new ArrayList<>(tupleLists.size());
        
        for (TupleList forbiddenTuples : tupleLists) {
            final InternalConstraint constraint = convertTuplesList(forbiddenTuples);
            
            constraints.add(constraint);
        }
        
        return constraints;
    }
    
    //
    // For list of forbidden-tuples { {0, 0}, {1, 1} } of identifiers { 0, 2 };
    // a constraint equivalent of !( ("0"=0 /\ "2"=0) \/ ("0"=1 /\ "2"=1) ) is created.
    //
    private InternalConstraint convertTuplesList(final TupleList tupleList) {
        final BiFunction<InputParameterModel, Model, Constraint> constraint = (InputParameterModel ipm, Model model) -> or(model, createConstraints(tupleList, model)).getOpposite();
        
        return new InternalConstraint(tupleList.getId(), constraint, tupleList.isMarkedAsCorrect());
    }
    
    private Constraint or(Model model, Constraint[] constraints) {
        if (constraints.length == 1) {
            return constraints[0];
        } else {
            return model.or(constraints);
        }
    }
    
    private Constraint and(Model model, Constraint[] constraints) {
        if (constraints.length == 1) {
            return constraints[0];
        } else {
            return model.and(constraints);
        }
    }
    
    private Constraint[] createConstraints(final TupleList tupleList, Model model) {
        final Constraint[] propositions = new Constraint[tupleList.getTuples().size()];
        int index = 0;
        
        for (int[] forbiddenTuple : tupleList.getTuples()) {
            propositions[index++] = createConstraints(tupleList.getInvolvedParameters(), forbiddenTuple, model);
        }
        
        return propositions;
    }
    
    //
    // For a forbidden-tuple { 0, 0 } of identifiers { 0, 2 }; a constraint equivalent of ("0"=0 /\ "2"=0) is created.
    //
    private Constraint createConstraints(int[] involvedParameters, int[] excludedTuple, Model model) {
        final Constraint[] propositions = new Constraint[involvedParameters.length];
        int index = 0;
        
        for (int i = 0; i < excludedTuple.length; i++) {
            int parameter = involvedParameters[i];
            int value = excludedTuple[i];
            
            final Optional<Variable> candidate = ChocoSolverUtil.findVariable(model, parameter);
            
            if (candidate.isPresent() && candidate.get() instanceof IntVar) {
                final IntVar variable = (IntVar) candidate.get();
                
                final Constraint proposition = model.arithm(variable, "=", value);
                propositions[index++] = proposition;
            } else {
                // If you reach this branch, there's a programming error somewhere else"
                throw new IllegalStateException("ERROR: excluded tuple refers to unknown parameter " + Arrays.toString(excludedTuple));
            }
        }
        
        return and(model, propositions);
    }
}
