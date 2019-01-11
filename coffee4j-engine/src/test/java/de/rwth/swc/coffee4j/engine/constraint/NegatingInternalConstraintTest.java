package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NegatingInternalConstraintTest {

    @Test
    void testSatisfiable() {
        List<TupleList> forbiddenTupleLists = Collections.singletonList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})));
        CombinatorialTestModel ipm = new CombinatorialTestModel(1, new int[]{2, 2, 2}, forbiddenTupleLists);
        InternalConstraint internalConstraint = new NegatingInternalConstraint(new InternalConstraintConverter().convertForbiddenTuples(ipm).get(0));

        Model model = new Model();
        IntVar var0 = model.intVar("0", 0, 1);
        IntVar var1 = model.intVar("1", 0, 1);
        internalConstraint.post(ipm, model);

        model.arithm(var0, "=", 0).post();
        model.arithm(var1, "=", 0);

        assertTrue(model.getSolver().solve());
    }

    @Test
    void testUnsatisfiable() {
        List<TupleList> forbiddenTupleLists = Collections.singletonList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})));
        CombinatorialTestModel ipm = new CombinatorialTestModel(1, new int[]{2, 2, 2}, forbiddenTupleLists);
        InternalConstraint internalConstraint = new NegatingInternalConstraint(new InternalConstraintConverter().convertForbiddenTuples(ipm).get(0));

        Model model = new Model();
        IntVar var0 = model.intVar("0", 0, 1);
        IntVar var1 = model.intVar("1", 0, 1);

        internalConstraint.post(ipm, model);

        model.arithm(var0, "=", 1).post();
        model.arithm(var1, "=", 1);

        assertFalse(model.getSolver().solve());
    }
}
