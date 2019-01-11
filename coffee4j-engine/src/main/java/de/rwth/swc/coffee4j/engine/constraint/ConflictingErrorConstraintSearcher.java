package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Collection;

public class ConflictingErrorConstraintSearcher {
    
    private final InputParameterModel inputParameterModel;
    private final Collection<InternalConstraint> exclusionConstraints;
    private final Collection<InternalConstraint> errorConstraints;
    
    private HardConstraintAnalyser analyser;
    
    ConflictingErrorConstraintSearcher(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        this.inputParameterModel = inputParameterModel;
        this.exclusionConstraints = exclusionConstraints;
        this.errorConstraints = errorConstraints;
        this.analyser = null;
    }
    
    public IntList explainValueBasedConflict(int[] parameters, int[] values) {
        if (analyser == null) {
            analyser = new HardConstraintAnalyser(inputParameterModel, exclusionConstraints, errorConstraints);
        }
        
        return analyser.explainConflict(parameters, values);
    }
    
    public IntList diagnoseValueBasedConflict(int[] parameters, int[] values) {
        if (analyser == null) {
            analyser = new HardConstraintAnalyser(inputParameterModel, exclusionConstraints, errorConstraints);
        }
        
        return analyser.diagnoseConflict(parameters, values);
    }
}
