package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraint;
import it.unimi.dsi.fastutil.ints.IntList;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Collection;

public class ConflictingErrorConstraintSearcher {
    
    private final InputParameterModel inputParameterModel;
    private final Collection<InternalConstraint> exclusionConstraints;
    private final Collection<InternalConstraint> errorConstraints;
    
    private HardConstraintAnalyser analyser;
    
    public ConflictingErrorConstraintSearcher(InputParameterModel inputParameterModel,
                                       Collection<InternalConstraint> exclusionConstraints,
                                       Collection<InternalConstraint> errorConstraints) {
        this.inputParameterModel = inputParameterModel;
        this.exclusionConstraints = exclusionConstraints;
        this.errorConstraints = errorConstraints;
        this.analyser = null;
    }
    
    public IntList findAndExplainConflict(int[] parameters, int[] values) {
        if (analyser == null) {
            analyser = new HardConstraintAnalyser(inputParameterModel, exclusionConstraints, errorConstraints);
        }
        
        return analyser.findAndExplainConflict(parameters, values);
    }

    public static boolean hasNoConflict(IntList conflict) {
        return conflict == null;
    }

    public static boolean isInconsistent(IntList conflict, int negatedErrorConstraintId) {
        return conflict.size() == 1 && conflict.getInt(0) == negatedErrorConstraintId;
    }

    public static boolean conflictIsNotMinimal(IntList conflict) {
        return conflict.size() == 0;
    }
    
//    public IntList diagnoseValueBasedConflict(int[] parameters, int[] values) {
//        if (analyser == null) {
//           analyser = new HardConstraintAnalyser(inputParameterModel, exclusionConstraints, errorConstraints);
//        }
//
//        return analyser.diagnoseConflict(parameters, values);
//    }
}
