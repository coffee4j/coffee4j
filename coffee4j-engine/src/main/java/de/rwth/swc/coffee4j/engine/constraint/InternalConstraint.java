package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.function.BiFunction;

public class InternalConstraint {
    
    private final int id;
    private final BiFunction<InputParameterModel, Model, Constraint> function;
    private final boolean markedAsCorrect;

    InternalConstraint(int id, BiFunction<InputParameterModel, Model, Constraint> function, boolean markedAsCorrect) {
        Preconditions.check(id > 0);
        Preconditions.notNull(function);
        
        this.id = id;
        this.function = function;
        this.markedAsCorrect = markedAsCorrect;
    }
    
    InternalConstraint(InternalConstraint constraint) {
        Preconditions.notNull(constraint);
        
        this.id = constraint.id;
        this.function = constraint.function;
        this.markedAsCorrect = constraint.markedAsCorrect;
    }
    
    public int getId() {
        return id;
    }

    public boolean isMarkedAsCorrect() {
        return markedAsCorrect;
    }

    public Constraint apply(final InputParameterModel inputParameterModel, final Model model) {
        return function.apply(inputParameterModel, model);
    }
    
    void post(final InputParameterModel inputParameterModel, final Model model) {
        apply(inputParameterModel, model).post();
    }
}