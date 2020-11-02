package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.*;
import java.util.function.Function;

import static de.rwth.swc.coffee4j.algorithmic.util.ChocoUtil.findVariable;

public class ConstraintConverter {

    public List<Constraint> convertAll(final Collection<TupleList> tupleLists) {
        Preconditions.notNull(tupleLists);

        final List<Constraint> constraints = new ArrayList<>(tupleLists.size());

        for (TupleList forbiddenTuples : tupleLists) {
            final Constraint constraint = convert(forbiddenTuples);

            constraints.add(constraint);
        }

        return constraints;
    }

    public Constraint convert(TupleList tupleList) {
        Preconditions.notNull(tupleList);
        //
        // For list of tuples { {0, 0}, {1, 1} } of identifiers { 0, 2 };
        // a constraint equivalent of !( ("0"=0 /\ "2"=0) \/ ("0"=1 /\ "2"=1) ) is created.
        //
        final Function<Model, org.chocosolver.solver.constraints.Constraint> constraint =
                (Model model)
                        -> or(model, createConstraints(tupleList, model)).getOpposite();

        return new Constraint(tupleList, constraint);
    }

    private org.chocosolver.solver.constraints.Constraint or(Model model,
                                                     org.chocosolver.solver.constraints.Constraint[] constraints) {
        if (constraints.length == 1) {
            return constraints[0];
        } else {
            return model.or(constraints);
        }
    }

    org.chocosolver.solver.constraints.Constraint and(Model model,
                                                      org.chocosolver.solver.constraints.Constraint[] constraints) {
        if (constraints.length == 1) {
            return constraints[0];
        } else {
            return model.and(constraints);
        }
    }

    org.chocosolver.solver.constraints.Constraint[] createConstraints(final TupleList tupleList,
                                                                      Model model) {
        final org.chocosolver.solver.constraints.Constraint[] propositions = new org.chocosolver.solver.constraints.Constraint[tupleList.getTuples().size()];
        int index = 0;

        for (int[] forbiddenTuple : tupleList.getTuples()) {
            propositions[index++] = createConstraints(tupleList.getInvolvedParameters(), forbiddenTuple, model);
        }

        return propositions;
    }

    //
    // For a tuple { 0, 0 } of identifiers { 0, 2 }; a constraint equivalent of ("0"=0 /\ "2"=0) is created.
    //
    org.chocosolver.solver.constraints.Constraint createConstraints(int[] involvedParameters,
                                                                    int[] excludedTuple,
                                                                    Model model) {
        final org.chocosolver.solver.constraints.Constraint[] propositions = new org.chocosolver.solver.constraints.Constraint[involvedParameters.length];

        for (int i = 0; i < excludedTuple.length; i++) {
            final Optional<org.chocosolver.solver.constraints.Constraint> optional  = createProposition(involvedParameters[i], excludedTuple[i], model);
            final org.chocosolver.solver.constraints.Constraint proposition = optional.orElseThrow(() -> new IllegalStateException("missing constraint"));

            propositions[i] = proposition;
        }

        return and(model, propositions);
    }

    Optional<org.chocosolver.solver.constraints.Constraint> createProposition(int involvedParameter,
                                                                              int excludedValue,
                                                                              Model model) {
        final Optional<Variable> candidate = findVariable(model, involvedParameter);

        if (candidate.isPresent() && candidate.get() instanceof IntVar) {
            final IntVar variable = (IntVar) candidate.get();

            final org.chocosolver.solver.constraints.Constraint proposition = model.arithm(variable, "=", excludedValue);

            return Optional.of(proposition);
        }

        return Optional.empty();
    }
}
