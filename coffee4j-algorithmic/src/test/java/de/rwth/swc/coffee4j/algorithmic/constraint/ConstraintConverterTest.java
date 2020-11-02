package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstraintConverterTest {

    @Test
    void testUnsatisfiable() {
        List<TupleList> exclusionTupleLists = Collections.singletonList(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0})));
        CompleteTestModel ipm = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();
        Constraint internalConstraint = new ConstraintConverter().convertAll(ipm.getExclusionTupleLists()).get(0);

        Model model = new Model();
        model.getSettings().setCheckDeclaredConstraints(false);
        IntVar var0 = model.intVar("0", 0, 1);
        IntVar var1 = model.intVar("1", 0, 1);
        internalConstraint.apply(model).post();

        model.arithm(var0, "=", 0).post();
        model.arithm(var1, "=", 0).post();

        assertFalse(model.getSolver().solve());
    }

    @Test
    void testSatisfiable() {
        List<TupleList> exclusionTupleLists = Collections.singletonList(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0})));
        CompleteTestModel ipm = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();
        Constraint internalConstraint = new ConstraintConverter()
                .convertAll(ipm.getExclusionTupleLists()).get(0);

        Model model = new Model();
        model.getSettings().setCheckDeclaredConstraints(false);
        IntVar var0 = model.intVar("0", 0, 1);
        IntVar var1 = model.intVar("1", 0, 1);

        internalConstraint.apply(model).post();

        model.arithm(var0, "=", 1).post();
        model.arithm(var1, "=", 1).post();

        assertTrue(model.getSolver().solve());
    }
}
