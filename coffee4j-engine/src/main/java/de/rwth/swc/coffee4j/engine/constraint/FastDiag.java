package de.rwth.swc.coffee4j.engine.constraint;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Arrays;

/**
 * Algorithm to find a preferred diagnosis for an over-constrained CSP
 * <p>
 * This implementation is based on the following paper.
 * Felfernig, Alexander & Schubert, Monika & Zehentner, Christoph. (2012).
 * An efficient diagnosis algorithm for inconsistent constraint sets.
 * Artificial Intelligence for Engineering Design, Analysis and Manufacturing. 26. 53 - 62.
 */
class FastDiag {
    
    private FastDiag() {
    }
    
    static Constraint[] diagnose(Model model, Constraint[] relaxableConstraints, Constraint[] allConstraints) {
        if (relaxableConstraints.length == 0) {
            return new Constraint[0];
        }
        
        if (!isConsistent(model, HardConstraintAnalyser.exclude(allConstraints, relaxableConstraints))) {
            return new Constraint[0];
        }
        
        return doDiagnose(model, new Constraint[0], relaxableConstraints, allConstraints);
        
    }
    
    private static Constraint[] doDiagnose(Model model, Constraint[] delta, Constraint[] relaxableConstraints, Constraint[] allConstraints) {
        if (delta.length != 0 && isConsistent(model, allConstraints)) {
            return new Constraint[0];
        }
        
        if (relaxableConstraints.length == 1) {
            return relaxableConstraints;
        }
        
        final int k = relaxableConstraints.length / 2;
        Constraint[] relaxableConstraints1 = Arrays.copyOfRange(relaxableConstraints, 0, k);
        Constraint[] relaxableConstraints2 = Arrays.copyOfRange(relaxableConstraints, k, relaxableConstraints.length);
        
        Constraint[] delta1 = doDiagnose(model, relaxableConstraints1, relaxableConstraints2, HardConstraintAnalyser.exclude(allConstraints, relaxableConstraints1));
        Constraint[] delta2 = doDiagnose(model, delta1, relaxableConstraints1, HardConstraintAnalyser.exclude(allConstraints, delta1));
        
        return QuickXPlain.distinctUnion(delta1, delta2);
    }
    
    private static boolean isConsistent(Model model, Constraint[] allConstraints) {
        model.getSolver().reset();
        model.unpost(model.getCstrs());
        model.post(allConstraints);
        
        return model.getSolver().solve();
    }
    
}
