package de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.choco.ChocoModel;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.QuickConflictExplainer;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalExplanation;
import de.rwth.swc.coffee4j.algorithmic.constraint.Constraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.rwth.swc.coffee4j.algorithmic.AssertUtils.assertInstanceOf;
import static de.rwth.swc.coffee4j.algorithmic.conflict.choco.ChocoModelTest.createTestModel;
import static de.rwth.swc.coffee4j.algorithmic.util.ChocoUtil.findVariable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

class ConflictDiagnosticianTest {

    @Test
    void testGetAllDiagnosesForImplicitConflict() {
        final ChocoModel chocoModel = createTestModel(2);
        chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });

        final int[] background = new int[] { 2, 6 };
        final int[] relaxable = new int[] {1, 3, 4, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

        assertEquals(3, diagnoses.length);
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{1})));
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{4})));
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{5})));
    }

    @Test
    void testGetFirstDiagnosisForImplicitConflict() {
        final ChocoModel chocoModel = createTestModel(2);
        chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });

        final int[] background = new int[] { 2, 6 };
        final int[] relaxable = new int[] {1, 3, 4, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new FastConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

        assertEquals(1, diagnoses.length);
        assertArrayEquals(new int[]{1}, diagnoses[0]);
    }

    @Test
    void testGetAllDiagnosesForExplicitConflict() {
        final ChocoModel chocoModel = createTestModel(4);
        chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });

        final int[] background = new int[] { 4, 6 };
        final int[] relaxable = new int[] {1, 2, 3, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

        assertEquals(1, diagnoses.length);
        assertArrayEquals(new int[]{2}, diagnoses[0]);
    }

    @Test
    void testGetFirstDiagnosisForExplicitConflict() {
        final ChocoModel chocoModel = createTestModel(4);
        chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });

        final int[] background = new int[] { 4, 6 };
        final int[] relaxable = new int[] {1, 2, 3, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());
        assertInstanceOf(InternalConflictSet.class, explanation.orElseThrow(IllegalStateException::new));

        final ConflictDiagnostician diagnostician = new FastConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

        assertEquals(1, diagnoses.length);
        assertArrayEquals(new int[]{2}, diagnoses[0]);
    }

    @Nested
    class MoreDetailed {

        @Test
        void testNoConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            final int id = chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 1 });
            chocoModel.setNegationOfConstraint(41);

            final int[] background = new int[] { 41, id };
            final int[] relaxable = new int[] {1, 2, 3, 42, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertFalse(explanation.isPresent());
        }

        @Test
        void testGetAllDiagnosesForExplicitConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            final int id = chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });
            chocoModel.setNegationOfConstraint(42);

            final int[] background = new int[] { 42, id };
            final int[] relaxable = new int[] {1, 2, 3, 41, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertTrue(explanation.isPresent());

            final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
            final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

            assertEquals(1, diagnoses.length);
            assertArrayEquals(new int[] {2}, diagnoses[0]);
        }

        @Test
        void testGetAllDiagnosesForImplicitConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            int id = chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });
            chocoModel.setNegationOfConstraint(2);

            final int[] background = new int[] { 2, id };
            final int[] relaxable = new int[] {1, 3, 41, 42, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertTrue(explanation.isPresent());

            final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
            final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.orElseThrow(IllegalStateException::new));

            assertEquals(3, diagnoses.length);
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{1})));
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{42})));
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{52})));
        }

        private TupleList tupleList(int id) {
            final TupleList tupleList = mock(TupleList.class);
            when(tupleList.getId()).thenReturn(id);
            
            return tupleList;
        }
        
        private ChocoModel createMoreDetailedTestModel() {
            final List<Constraint> constraints = new ArrayList<>();

            constraints.add(
                    new Constraint(tupleList(1), (Model model)
                            -> model.arithm((IntVar) findVariable(model, 0).orElseThrow(IllegalStateException::new), "=", 2).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(2), (Model model)
                            -> model.arithm((IntVar) findVariable(model, 1).orElseThrow(IllegalStateException::new), "=", 2).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(3), (Model model)
                            -> model.arithm((IntVar) findVariable(model, 2).orElseThrow(IllegalStateException::new), "=", 2).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(41), (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).orElseThrow(IllegalStateException::new), "=", 0),
                            model.arithm((IntVar) findVariable(model, 1).orElseThrow(IllegalStateException::new), "=", 1)
                    ).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(42), (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).orElseThrow(IllegalStateException::new), "=", 0),
                            model.arithm((IntVar) findVariable(model, 1).orElseThrow(IllegalStateException::new), "=", 2)
                    ).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(51), (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).orElseThrow(IllegalStateException::new), "=", 1),
                            model.arithm((IntVar) findVariable(model, 1).orElseThrow(IllegalStateException::new), "=", 0)
                    ).getOpposite())
            );
            constraints.add(
                    new Constraint(tupleList(52), (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).orElseThrow(IllegalStateException::new), "=", 1),
                            model.arithm((IntVar) findVariable(model, 1).orElseThrow(IllegalStateException::new), "=", 2)
                    ).getOpposite())
            );

            return new ChocoModel(new int[] { 3, 3, 3 }, constraints);
        }
    }

    @Nested
    class ExpandPathsTest {
        @Test
        void testInitialExpansion() {
            final int[] currentPath = new int[0];
            final int[] nextLabels = { 1, 2, 3 };

            final List<int[]> expansions = new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels);
    
            assertEquals(3, expansions.size());
        }

        @Test
        void testExpansion() {
            final int[] currentPath = { 1 };
            final int[] nextLabels = { 2, 3 };

            final List<int[]> expansions = new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels);
    
            assertEquals(2, expansions.size());
        }

        @Test
        void testEmptyExpansion() {
            final int[] currentPath = { 1 };
            final int[] nextLabels = { };

            assertThrows(IllegalArgumentException.class,
                    () -> new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels));
        }
    }

    @Nested
    class IsCurrentPathAlreadyContainedByRelaxationTest {

        @Test
        void testCoverage() {
            final List<int[]> diagnoses = Collections.singletonList(new int[]{ 2 });

            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1 }, diagnoses));
            assertTrue(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 2 }, diagnoses));
            assertTrue(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 2 }, diagnoses));
            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 4 }, diagnoses));
        }

        @Test
        void testNoRelaxations() {
            final List<int[]> diagnoses = Collections.emptyList();

            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1 }, diagnoses));
            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 4 }, diagnoses));
        }

        @Test
        void testNoCurrentPath() {
            final List<int[]> diagnoses = Collections.singletonList(new int[]{ 2 });

            assertThrows(IllegalArgumentException.class,
                    () -> new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[0], diagnoses));
        }
    }

    @Nested
    class IsSubsetTest {

        @Test
        void testIsSubset() {
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 1 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 2 }));
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 1, 2 }));
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1, 2 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1, 3 }));
        }
    }
}
