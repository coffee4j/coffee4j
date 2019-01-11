package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

class NegatingInternalConstraint extends InternalConstraint {
    
    NegatingInternalConstraint(InternalConstraint internalConstraint) {
        super(internalConstraint);
    }
    
    @Override
    protected Constraint apply(final InputParameterModel inputParameterModel, final Model model) {
        return super.apply(inputParameterModel, model).getOpposite();
    }
}
