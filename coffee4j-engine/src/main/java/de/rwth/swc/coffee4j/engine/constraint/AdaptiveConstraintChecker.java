package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class AdaptiveConstraintChecker extends ModelBasedConstraintChecker {
    
    AdaptiveConstraintChecker(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints, TupleList negatedErrorTuples) {
        super(createModel(inputParameterModel, exclusionConstraints, errorConstraints, negatedErrorTuples));
    }
    
    private static Model createModel(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints, TupleList negatedErrorTuples) {
        final Model model = new Model();
        ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(inputParameterModel, exclusionConstraints, errorConstraints, negatedErrorTuples);
        createVariables(inputParameterModel, model);
        createHardConstraints(inputParameterModel, model, partitioner);
        createSoftConstraints(inputParameterModel, negatedErrorTuples, model, partitioner);
        
        return model;
    }
    
    private static void createVariables(InputParameterModel inputParameterModel, Model model) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private static void createHardConstraints(InputParameterModel inputParameterModel, Model model, ConflictingErrorConstraintPartitioner partitioner) {
        for (InternalConstraint constraint : partitioner.getHardConstraints()) {
            constraint.post(inputParameterModel, model);
        }
    }
    
    private static void createSoftConstraints(InputParameterModel inputParameterModel, TupleList negatedErrorTuples, Model model, ConflictingErrorConstraintPartitioner partitioner) {
        if (partitioner.getSoftConstraints().isEmpty()) {
            return;
        }
        
        Int2ObjectMap<BoolVar> reifiedSoftConstraints = reifySoftConstraints(inputParameterModel, model, partitioner);
        Constraint[] propositions = new Constraint[negatedErrorTuples.getTuples().size()];
        
        for (int i = 0; i < negatedErrorTuples.getTuples().size(); i++) {
            List<BoolVar> satisfiableSubset = findSatisfiableSoftConstraintSubset(partitioner, reifiedSoftConstraints, i);
            
            int[] tuple = negatedErrorTuples.getTuples().get(i);
            propositions[i] = createPropositions(model, negatedErrorTuples.getInvolvedParameters(), tuple);
            
            model.ifThen(propositions[i], model.and(satisfiableSubset.toArray(new BoolVar[0])));
        }
        
        model.ifThen(model.or(propositions).getOpposite(), model.and(reifiedSoftConstraints.values().toArray(new BoolVar[0])));
    }
    
    private static Int2ObjectMap<BoolVar> reifySoftConstraints(InputParameterModel inputParameterModel, Model model, ConflictingErrorConstraintPartitioner partitioner) {
        Int2ObjectMap<BoolVar> reifiedSoftConstraints = new Int2ObjectOpenHashMap<>();
        
        for (InternalConstraint constraint : partitioner.getSoftConstraints()) {
            reifiedSoftConstraints.put(constraint.getId(), constraint.apply(inputParameterModel, model).reify());
        }
        
        return reifiedSoftConstraints;
    }
    
    private static List<BoolVar> findSatisfiableSoftConstraintSubset(ConflictingErrorConstraintPartitioner partitioner, Int2ObjectMap<BoolVar> reifiedSoftConstraints, int tupleIndex) {
        IntList conflicts = new IntArrayList(partitioner.getValueBasedConflicts().get(tupleIndex));
        conflicts.removeAll(partitioner.getIgnoredConstraintIds());
        
        List<BoolVar> subset = new ArrayList<>();
        
        for (int key : reifiedSoftConstraints.keySet()) {
            if (!conflicts.contains(key)) {
                subset.add(reifiedSoftConstraints.get(key));
            }
        }
        
        return subset;
    }
    
    private static Constraint createPropositions(Model model, int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);
        
        Constraint[] propositions = new Constraint[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            final Optional<Variable> candidate = ChocoSolverUtil.findVariable(model, parameters[i]);
            
            if (candidate.isPresent() && candidate.get() instanceof IntVar) {
                final IntVar var = (IntVar) candidate.get();
                
                propositions[i] = model.arithm(var, "=", values[i]);
            } else {
                // If you reach this branch, there's a programming error somewhere else"
                throw new IllegalStateException("INTERNAL-ERROR: " + values[i] + " belongs to unknown parameter " + parameters[i]);
            }
        }
        
        return model.and(propositions);
    }
    
}
