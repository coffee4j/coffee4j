package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraint;
import de.rwth.swc.coffee4j.engine.constraint.NegatingInternalConstraint;
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
import java.util.function.Supplier;

import static de.rwth.swc.coffee4j.engine.constraint.ModelBasedConstraintChecker.addAssignmentConstraint;

class HardConstraintAnalyser {
    
    private static final Logger LOG = LoggerFactory.getLogger(HardConstraintAnalyser.class);
    
    private final Model model;
    private boolean firstRun;

    private Constraint[] assignmentConstraints;

    private final int negatedConstraintId;

    private List<Constraint> backgroundConstraints;
    private List<Constraint> relaxableConstraints;
    private Constraint[] allSolverRelaxableConstraints;
    private Constraint[] allSolverBackgroundConstraints;

    private Object2IntMap<Constraint> constraintsToIdMap;


    HardConstraintAnalyser(InputParameterModel inputParameterModel,
                           Collection<InternalConstraint> exclusionConstraints,
                           Collection<InternalConstraint> errorConstraints) {

        this.backgroundConstraints = new ArrayList<>();
        this.relaxableConstraints = new ArrayList<>();

        this.constraintsToIdMap = new Object2IntOpenHashMap<>();

        this.negatedConstraintId = errorConstraints.stream()
                .filter(NegatingInternalConstraint.class::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("One negated error constraint is required"))
                .getId();

        this.model = new Model();
        this.firstRun = true;

        createVariables(inputParameterModel);
        createConstraints(inputParameterModel, exclusionConstraints, errorConstraints);
    }
    
    private void createVariables(InputParameterModel inputParameterModel) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);
            
            model.intVar(key, 0, parameterSize - 1);
        }
    }
    
    private void createConstraints(InputParameterModel inputParameterModel,
                                   Collection<InternalConstraint> exclusionConstraints,
                                   Collection<InternalConstraint> errorConstraints) {

        for (InternalConstraint exclusionConstraint : exclusionConstraints) {
            if(!exclusionConstraint.isMarkedAsCorrect()) {
                Constraint constraint = exclusionConstraint.apply(inputParameterModel, model);
                constraint.post();

                relaxableConstraints.add(constraint);
                constraintsToIdMap.put(constraint, exclusionConstraint.getId());
            }
        }

        for (InternalConstraint errorConstraint : errorConstraints) {
            if(errorConstraint.getId() != negatedConstraintId && !errorConstraint.isMarkedAsCorrect()) {
                Constraint constraint = errorConstraint.apply(inputParameterModel, model);
                constraint.post();

                relaxableConstraints.add(constraint);
                constraintsToIdMap.put(constraint, errorConstraint.getId());
            }
        }

        allSolverRelaxableConstraints = model.getCstrs();

        for (InternalConstraint exclusionConstraint : exclusionConstraints) {
            if(exclusionConstraint.isMarkedAsCorrect()) {
                Constraint constraint = exclusionConstraint.apply(inputParameterModel, model);
                constraint.post();

                backgroundConstraints.add(constraint);
                constraintsToIdMap.put(constraint, exclusionConstraint.getId());
            }
        }

        for (InternalConstraint errorConstraint : errorConstraints) {
            if(errorConstraint.getId() == negatedConstraintId || errorConstraint.isMarkedAsCorrect()) {
                Constraint constraint = errorConstraint.apply(inputParameterModel, model);
                constraint.post();

                backgroundConstraints.add(constraint);
                constraintsToIdMap.put(constraint, errorConstraint.getId());
            }
        }
        
        allSolverBackgroundConstraints = exclude(model.getCstrs(), allSolverRelaxableConstraints);
    }
    
    private static Constraint[] exclude(Constraint[] allConstraints, Constraint[] excludedConstraints) {
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
    IntList findAndExplainConflict(int[] parameters, int[] values) {
        Preconditions.check(parameters.length == values.length);

        return checkIfSatisfiableAndReturnConflict(parameters, values, this::searchForExplanation);
    }
    
    private IntList checkIfSatisfiableAndReturnConflict(int[] parameters,
                                                        int[] values,
                                                        Supplier<IntList> conflictSupplier) {
        resetAnalyser();
        
        ArrayList<Constraint> list = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            addAssignmentConstraint(parameters[i], values[i], model, list);
        }
        
        assignmentConstraints = list.toArray(new Constraint[0]);
        model.post(assignmentConstraints);
        
        final boolean result = model.getSolver().solve();
        
        if (result) {
            return null;
        } else {
            return conflictSupplier.get();
        }
    }
    
    private IntList searchForExplanation() {
        final Constraint[] background = new Constraint[this.allSolverBackgroundConstraints.length + assignmentConstraints.length];

        for(int i = 0; i < this.allSolverBackgroundConstraints.length; i++) {
            background[i] = this.allSolverBackgroundConstraints[i];
        }
        System.arraycopy(assignmentConstraints, 0, background, this.allSolverBackgroundConstraints.length, assignmentConstraints.length);
        
        final Constraint[] relaxable = allSolverRelaxableConstraints.clone();
        
        final Constraint[] conflictSet = QuickXPlain.explain(model, background, relaxable);

        if(hasNoConflict(conflictSet)) {
            return null;
        } else if(isInconsistent(conflictSet, background)) {
            return IntLists.singleton(negatedConstraintId);
        } else if(conflictIsNotMinimal(conflictSet)) {
            return IntLists.EMPTY_LIST;
        } else {
            int[] knownConflicts = Arrays.stream(conflictSet)
                    .mapToInt(constraint -> constraintsToIdMap.getOrDefault(constraint, -1))
                    .filter(i -> i != -1)
                    .filter(i -> i != negatedConstraintId)
                    .toArray();

            return new IntArrayList(knownConflicts);
        }
    }

    private static boolean hasNoConflict(Constraint[] conflictSet) {
        return conflictSet == null;
    }

    private static boolean isInconsistent(Constraint[] conflictSet, Constraint[] background) {
        return conflictSet == background;
    }

    private static boolean conflictIsNotMinimal(Constraint[] conflictSet) {
        return conflictSet.length == 0;
    }

//    IntList diagnoseConflict(int[] parameters, int[] values) {
//        Preconditions.check(parameters.length == values.length);
//
//        return checkIfSatisfiableAndReturnConflict(parameters, values, this::searchForDiagnosis);
//    }
    
//    private IntList searchForDiagnosis() {
//        final Constraint[] conflictSet = FastDiag.diagnose(model, allErrorConstraints, union(union(allSolverErrorConstraints, allSolverExclusionConstraints), assignmentConstraints));
//
//        if (conflictSet.length == 0) {
//            LOG.info("No diagnosis found; return empty-set");
//
//            return IntLists.EMPTY_LIST;
//        } else {
//            int[] knownConflicts = Arrays.stream(conflictSet).mapToInt(constraint -> solverErrorConstraintsMap.getOrDefault(constraint, -1)).filter(i -> i != -1).toArray();
//
//            return new IntArrayList(knownConflicts);
//        }
//    }
    
    private void resetAnalyser() {
        if (firstRun) {
            firstRun = false;
            return;
        }
        
        model.getSolver().reset();
        model.unpost(model.getCstrs());
        
        model.post(allSolverRelaxableConstraints);
        model.post(allSolverBackgroundConstraints);
    }
}
