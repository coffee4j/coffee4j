package de.rwth.swc.coffee4j.engine.constraint;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Algorithm to find a preferred explanation for an over-constrained CSP
 * <p>
 * This implementation is based on the following paper.
 * Junker, Ulrich. (2004).
 * QuickXPlain: Preferred explanations and relaxations for over-constrained problems.
 * AAAI. 167 - 172.
 */
final class QuickXPlain {
    
    private QuickXPlain() {
    }
    
    /**
     * Finds a preferred explanation for an over-constrained CSP
     *
     * @param model       CSP which included backgrounds and constraints
     * @param background  consistent set of constraints that cannot be relaxed
     * @param constraints inconsistent constraints that can be relaxed
     * @return an empty list if there is no conflict
     * all constraints if a preferred explanation could not be obtained
     * a subset of all constraints otherwise
     */
    static Constraint[] explain(Model model, Constraint[] background, Constraint[] constraints) {
        if (constraints.length == 0) {
            return new Constraint[0];
        }
        
        if (isConsistent(model, union(background, constraints))) {
            return new Constraint[0];
        }
        
        return doExplain(model, background, background, constraints);
    }
    
    private static Constraint[] doExplain(Model model, Constraint[] background, Constraint[] delta, Constraint[] constraints) {
        if (delta.length != 0 && !isConsistent(model, background)) {
            return new Constraint[0];
        }
        
        if (constraints.length == 1) {
            return constraints;
        }
        
        final int k = constraints.length / 2;
        Constraint[] constraints1 = Arrays.copyOfRange(constraints, 0, k);
        Constraint[] constraints2 = Arrays.copyOfRange(constraints, k, constraints.length);
        
        Constraint[] delta2 = doExplain(model, union(background, constraints1), constraints1, constraints2);
        Constraint[] delta1 = doExplain(model, union(background, delta2), delta2, constraints1);
        
        return distinctUnion(delta1, delta2);
    }
    
    private static boolean isConsistent(Model model, Constraint[] allConstraints) {
        model.getSolver().reset();
        model.unpost(model.getCstrs());
        model.post(allConstraints);
        
        return model.getSolver().solve();
    }
    
    static Constraint[] union(Constraint[] a, Constraint[] b) {
        Constraint[] array = new Constraint[a.length + b.length];
        
        System.arraycopy(a, 0, array, 0, a.length);
        System.arraycopy(b, 0, array, a.length, b.length);
        
        return array;
    }
    
    static Constraint[] distinctUnion(Constraint[] a, Constraint[] b) {
        Set<Constraint> set = new HashSet<>(Arrays.asList(a));
        set.addAll(Arrays.asList(b));
        
        return set.toArray(new Constraint[0]);
    }
}