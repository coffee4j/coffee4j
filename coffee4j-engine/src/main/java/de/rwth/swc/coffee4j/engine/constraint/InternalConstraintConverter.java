package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.TupleList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.*;
import java.util.function.Function;

public class InternalConstraintConverter {

    public List<InternalConstraint> convertAll(final Collection<TupleList> tupleLists) {
        final List<InternalConstraint> constraints = new ArrayList<>(tupleLists.size());

        for (TupleList forbiddenTuples : tupleLists) {
            final List<InternalConstraint> constraint = convert(forbiddenTuples);

            constraints.addAll(constraint);
        }

        return constraints;
    }

    public List<InternalConstraint> convert(TupleList tupleList) {
        //
        // For list of tuples { {0, 0}, {1, 1} } of identifiers { 0, 2 };
        // a constraint equivalent of !( ("0"=0 /\ "2"=0) \/ ("0"=1 /\ "2"=1) ) is created.
        //
        final Function<Model, Constraint> constraint =
                (Model model)
                        -> or(model, createConstraints(tupleList, model)).getOpposite();

        return Collections.singletonList(
                new InternalConstraint(tupleList.getId(), constraint, tupleList.isMarkedAsCorrect())
        );
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
    // For a tuple { 0, 0 } of identifiers { 0, 2 }; a constraint equivalent of ("0"=0 /\ "2"=0) is created.
    //
    private Constraint createConstraints(int[] involvedParameters,
                                         int[] excludedTuple,
                                         Model model) {
        final Constraint[] propositions = new Constraint[involvedParameters.length];
        
        for (int i = 0; i < excludedTuple.length; i++) {
            final Optional<Constraint> optional  = createProposition(involvedParameters[i], excludedTuple[i], model);
            final Constraint proposition = optional.orElseThrow();

            propositions[i] = proposition;
        }
        
        return and(model, propositions);
    }

    private Optional<Constraint> createProposition(int involvedParameter,
                                                   int excludedValue,
                                                   Model model) {
        final Optional<Variable> candidate = ChocoSolverUtil.findVariable(model, involvedParameter);

        if (candidate.isPresent() && candidate.get() instanceof IntVar) {
            final IntVar variable = (IntVar) candidate.get();

            final Constraint proposition = model.arithm(variable, "=", excludedValue);

            return Optional.of(proposition);
        }

        return Optional.empty();
    }
}
