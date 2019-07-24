package de.rwth.swc.coffee4j.engine.constraint;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

public class NegatingInternalConstraint extends InternalConstraint {
    
    public NegatingInternalConstraint(InternalConstraint internalConstraint) {
        super(internalConstraint);
    }
    
    @Override
    public Constraint apply(final Model model) {
        return super.apply(model).getOpposite();
    }
}
