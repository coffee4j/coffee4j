package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.function.BiFunction;

class InternalConstraint {
    
    private final int id;
    private final BiFunction<InputParameterModel, Model, Constraint> function;
    
    InternalConstraint(int id, BiFunction<InputParameterModel, Model, Constraint> function) {
        Preconditions.check(id > 0);
        Preconditions.notNull(function);
        
        this.id = id;
        this.function = function;
    }
    
    InternalConstraint(InternalConstraint constraint) {
        Preconditions.notNull(constraint);
        
        this.id = constraint.id;
        this.function = constraint.function;
    }
    
    public int getId() {
        return id;
    }
    
    protected Constraint apply(final InputParameterModel inputParameterModel, final Model model) {
        return function.apply(inputParameterModel, model);
    }
    
    public void post(final InputParameterModel inputParameterModel, final Model model) {
        apply(inputParameterModel, model).post();
    }
}