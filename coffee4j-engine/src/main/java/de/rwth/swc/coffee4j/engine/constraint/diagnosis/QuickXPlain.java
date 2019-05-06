package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.constraint.NegatingInternalConstraint;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.SolverException;

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
class QuickXPlain {
    
    private QuickXPlain() {
    }
    
    /**
     * Finds a preferred explanation for an over-constrained CSP
     *
     * @param model       CSP which included backgrounds and constraints
     * @param background  consistent set of constraints that cannot be relaxed
     * @param relaxable inconsistent constraints that can be relaxed
     * @return  null if there is no conflict
     *          an empty array if a preferred explanation could not be obtained
     *          background if background is inconsistent
     *          otherwise, a subset of relaxable constraints
     */
    static Constraint[] explain(Model model, Constraint[] background, Constraint[] relaxable) {
        Preconditions.notNull(model);
        Preconditions.notNull(background);
        Preconditions.notNull(relaxable);
        Preconditions.check(relaxable.length > 0);

        if (isConsistent(model, union(background, relaxable))) {
            return null;
        }

        if(!isConsistent(model, background)) {
            return background;
        }

        return doExplain(model, background, background, relaxable);
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
    
    private static Constraint[] union(Constraint[] a, Constraint[] b) {
        Constraint[] array = new Constraint[a.length + b.length];
        
        System.arraycopy(a, 0, array, 0, a.length);
        System.arraycopy(b, 0, array, a.length, b.length);
        
        return array;
    }
    
    private static Constraint[] distinctUnion(Constraint[] a, Constraint[] b) {
        Set<Constraint> set = new HashSet<>(Arrays.asList(a));
        set.addAll(Arrays.asList(b));
        
        return set.toArray(new Constraint[0]);
    }
}