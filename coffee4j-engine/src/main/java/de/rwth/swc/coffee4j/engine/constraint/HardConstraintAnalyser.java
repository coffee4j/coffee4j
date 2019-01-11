package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.rwth.swc.coffee4j.engine.constraint.ModelBasedConstraintChecker.addAssignmentConstraint;
import static de.rwth.swc.coffee4j.engine.constraint.QuickXPlain.union;

class HardConstraintAnalyser {
    
    private static final Logger LOG = LoggerFactory.getLogger(HardConstraintAnalyser.class);
    
    private final Model model;
    private final int negatedConstraintId;
    
    private boolean firstRun;
    
    private Constraint[] allSolverExclusionConstraints;
    
    private Object2IntMap<Constraint> solverErrorConstraintsMap;
    private Constraint[] allErrorConstraints;
    private Constraint[] allSolverErrorConstraints;
    private Constraint[] assignmentConstraints;
    
    HardConstraintAnalyser(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        
        Optional<InternalConstraint> optional = errorConstraints.stream().filter(NegatingInternalConstraint.class::isInstance).findFirst();
        
        this.negatedConstraintId = optional.orElseThrow(() -> new IllegalArgumentException("Exactly one negated error constraint is required")).getId();
        
        this.model = new Model();
        createVariables(inputParameterModel);
        createConstraints(inputParameterModel, exclusionConstraints, errorConstraints);
        
        this.firstRun = true;
    }
    
    private void createVariables(InputParameterModel inputParameterModel) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private void createConstraints(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        
        for (InternalConstraint exclusionConstraint : exclusionConstraints) {
            Constraint constraint = exclusionConstraint.apply(inputParameterModel, model);
            constraint.post();
        }
        
        for (InternalConstraint errorConstraint : errorConstraints) {
            if (errorConstraint.getId() == negatedConstraintId) {
                Constraint constraint = errorConstraint.apply(inputParameterModel, model);
                constraint.post();
                break;
            }
        }
        
        allSolverExclusionConstraints = model.getCstrs();
        
        solverErrorConstraintsMap = new Object2IntOpenHashMap<>();
        for (InternalConstraint errorConstraint : errorConstraints) {
            if (errorConstraint.getId() == negatedConstraintId) {
                continue;
            }
            
            Constraint constraint = errorConstraint.apply(inputParameterModel, model);
            constraint.post();
            
            solverErrorConstraintsMap.put(constraint, errorConstraint.getId());
        }
        
        allErrorConstraints = solverErrorConstraintsMap.keySet().toArray(new Constraint[0]);
        allSolverErrorConstraints = exclude(model.getCstrs(), allSolverExclusionConstraints);
    }
    
    static Constraint[] exclude(Constraint[] allConstraints, Constraint[] excludedConstraints) {
        List<Constraint> constraints = new ArrayList<>();
        
        for (Constraint constraint : allConstraints) {
            
            if (!contains(constraint, excludedConstraints)) {
                constraints.add(constraint);
            }
        }
        
        return constraints.toArray(new Constraint[0]);
    }
    
    private static boolean contains(Constraint constraint, Constraint[] excludedConstraints) {
        for (Constraint excludedConstraint : excludedConstraints) {
            if (excludedConstraint.equals(constraint)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Finds a preferred explanation according to Junker2004
     *
     * @param parameters parameters of the values
     * @param values     values of the parameters
     * @return an empty list if there is no conflict
     * all error-constraints if a preferred explanation could not be obtained
     * a subset of all error-constraints otherwise
     */
    IntList explainConflict(int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);
        
        return checkIfSatisfiableAndReturnConflicts(parameters, values, this::searchForExplanation);
    }
    
    private IntList checkIfSatisfiableAndReturnConflicts(int[] parameters, int[] values, Supplier<IntList> conflictSupplier) {
        resetAnalyser();
        
        ArrayList<Constraint> list = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            addAssignmentConstraint(parameters[i], values[i], model, list);
        }
        
        assignmentConstraints = list.toArray(new Constraint[0]);
        model.post(assignmentConstraints);
        
        final boolean result = model.getSolver().solve();
        
        IntList conflicts;
        if (result) {
            // satisfiable and, therefore, no unsatisfiable core exists.
            conflicts = IntLists.EMPTY_LIST;
        } else {
            conflicts = conflictSupplier.get();
        }
        
        return conflicts;
    }
    
    private IntList searchForExplanation() {
        final Constraint[] background = new Constraint[allSolverExclusionConstraints.length + assignmentConstraints.length];
        System.arraycopy(allSolverExclusionConstraints, 0, background, 0, allSolverExclusionConstraints.length);
        System.arraycopy(assignmentConstraints, 0, background, allSolverExclusionConstraints.length, assignmentConstraints.length);
        
        final Constraint[] constraints = allSolverErrorConstraints.clone();
        
        final Constraint[] conflictSet = QuickXPlain.explain(model, background, constraints);
        
        if (conflictSet.length == 0) {
            LOG.info("No unsatisfied core identified; return all error-constraints");
            
            return new IntArrayList(solverErrorConstraintsMap.values());
        } else {
            int[] knownConflicts = Arrays.stream(conflictSet).mapToInt(constraint -> solverErrorConstraintsMap.getOrDefault(constraint, -1)).filter(i -> i != -1).toArray();
            
            return new IntArrayList(knownConflicts);
        }
    }
    
    IntList diagnoseConflict(int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);
        
        return checkIfSatisfiableAndReturnConflicts(parameters, values, this::searchForDiagnosis);
    }
    
    private IntList searchForDiagnosis() {
        final Constraint[] conflictSet = FastDiag.diagnose(model, allErrorConstraints, union(union(allSolverErrorConstraints, allSolverExclusionConstraints), assignmentConstraints));
        
        if (conflictSet.length == 0) {
            LOG.info("No diagnosis found; return empty-set");
            
            return IntLists.EMPTY_LIST;
        } else {
            int[] knownConflicts = Arrays.stream(conflictSet).mapToInt(constraint -> solverErrorConstraintsMap.getOrDefault(constraint, -1)).filter(i -> i != -1).toArray();
            
            return new IntArrayList(knownConflicts);
        }
    }
    
    private void resetAnalyser() {
        if (firstRun) {
            firstRun = false;
            return;
        }
        
        model.getSolver().reset();
        model.unpost(model.getCstrs());
        
        model.post(allSolverExclusionConstraints);
        model.post(allSolverErrorConstraints);
    }
}
