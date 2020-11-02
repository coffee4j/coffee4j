package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ExhaustiveConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.QuickConflictExplainer;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReduceBasedDiagnosisHittingSetBuilderTest {

    private boolean contains(List<DiagnosisHittingSet> hittingSets, int[] ... elements) {
        Preconditions.check(elements.length % 3 == 0);

        final Set<DiagnosisElement> set = buildDiagnosisHittingSet(elements);

        return hittingSets.stream().anyMatch(hittingSet ->
                hittingSet.getDiagnosisElements().containsAll(set) &&
                set.containsAll(hittingSet.getDiagnosisElements()));
    }

    private boolean contains(Set<Set<DiagnosisElement>> hittingSets, int[] ... elements) {
        Preconditions.check(elements.length % 3 == 0);

        final Set<DiagnosisElement> set = buildDiagnosisHittingSet(elements);

        return hittingSets.stream().anyMatch(hittingSet ->
                hittingSet.containsAll(set) && set.containsAll(hittingSet));
    }

    private Set<DiagnosisElement> buildDiagnosisHittingSet(int[] ... elements) {
        final Set<DiagnosisElement> diagnosisElements = new LinkedHashSet<>();

        for(int i = 0; i < elements.length; i += 3) {
            final int diagnosedConstrainedId = elements[i][0];
            final int[] involvedParameters = elements[i + 1];
            final int[] conflictingValues = elements[i + 2];

            diagnosisElements.add(new DiagnosisElement(diagnosedConstrainedId, involvedParameters, conflictingValues));
        }

        return diagnosisElements;
    }

    @Test
    void testComputeMinimalDiagnosisHittingSetsWithEmptyList() {
        final CompleteTestModel model = mock(CompleteTestModel.class);
        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);

        assertThrows(IllegalArgumentException.class,
                () -> builder.computeMinimalDiagnosisHittingSets(Collections.emptyList()));
    }

    @Test
    void testExtendWithDiagnosisSetsForNegatedErrorConstraint() {
        final CompleteTestModel model = mock(CompleteTestModel.class);
        when(model.getExclusionTupleLists()).thenReturn(emptyList());
        when(model.getErrorTupleLists()).thenReturn(Arrays.asList(
                new TupleList(1, new int[]{ 1 }, singletonList(new int[]{ 1 })),
                new TupleList(2, new int[]{ 1 }, singletonList(new int[]{ 1 })),
                new TupleList(4, new int[]{ 1 }, singletonList(new int[]{ 1 })),
                new TupleList(5, new int[]{ 1 }, singletonList(new int[]{ 1 }))
        ));

        final ConflictSet conflictSet = mock(ConflictSet.class);

        final List<MissingInvalidTuple> missingInvalidTuples = new ArrayList<>();
        missingInvalidTuples.add(new MissingInvalidTuple(2, new int[] {1}, new int[] {2},
                new DiagnosisSets(conflictSet, asList(
                        new DiagnosisSet(singletonList(new DiagnosisElement(1, new int[]{0}, new int[]{2}))),
                        new DiagnosisSet(singletonList(new DiagnosisElement(4, new int[]{0, 1}, new int[]{0, 2}))),
                        new DiagnosisSet(singletonList(new DiagnosisElement(5, new int[]{0, 1}, new int[]{1, 2})))))));
        missingInvalidTuples.add(new MissingInvalidTuple(4, new int[] {0, 1}, new int[] {0, 2},
                new DiagnosisSets(conflictSet, singletonList(
                        new DiagnosisSet(singletonList(new DiagnosisElement(2, new int[]{1}, new int[]{2})))))));
        missingInvalidTuples.add(new MissingInvalidTuple(5, new int[] {0, 1}, new int[] {1, 2},
                new DiagnosisSets(conflictSet, singletonList(
                        new DiagnosisSet(singletonList(new DiagnosisElement(2, new int[]{1}, new int[]{2})))))));

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);
        final MissingInvalidTuple extended = builder.extendWithDiagnosisSetsForNegatedErrorConstraint(missingInvalidTuples.get(0));

        assertTrue(((DiagnosisSets) extended.getExplanation()).getDiagnosisSets().stream()
                .anyMatch(diagnosisSet -> diagnosisSet.getDiagnosisElements().stream()
                        .anyMatch(element -> element.getDiagnosedConstraintId() == missingInvalidTuples.get(0).getNegatedErrorConstraintId()))
        );
    }

    @Test
    void testIsMinimalDiagnosisHittingSetForCTA2019Example() {
        final CompleteTestModel model = mock(CompleteTestModel.class);
        
        final List<Set<DiagnosisElement>> hittingSets = Arrays.asList(
                buildDiagnosisHittingSet(                       // c1, c2
                        new int[] {1}, new int[] {0}, new int[] {2},
                        new int[] {2}, new int[] {1}, new int[] {2}),
                buildDiagnosisHittingSet(                        // c1, c2, c5
                        new int[] {1}, new int[] {0}, new int[] {2},
                        new int[] {2}, new int[] {1}, new int[] {2},
                        new int[] {5}, new int[] {0, 1}, new int[] {1, 2}),
                buildDiagnosisHittingSet(                        // c1, c2, c4
                        new int[] {1}, new int[] {0}, new int[] {2},
                        new int[] {2}, new int[] {1}, new int[] {2},
                        new int[] {4}, new int[] {0, 1}, new int[] {0, 2}),
                buildDiagnosisHittingSet(                        // c1, c4, c5
                        new int[] {1}, new int[] {0}, new int[] {2},
                        new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                        new int[] {5}, new int[] {0, 1}, new int[] {1, 2}),
                buildDiagnosisHittingSet(             // c2, c4
                        new int[] {2}, new int[] {1}, new int[] {2},
                        new int[] {4}, new int[] {0, 1}, new int[] {0, 2}),
                buildDiagnosisHittingSet(             // c2, c4, c5
                        new int[] {2}, new int[] {1}, new int[] {2},
                        new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                        new int[] {5}, new int[] {0, 1}, new int[] {1, 2}),
                buildDiagnosisHittingSet(              // c4, c5
                        new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                        new int[] {5}, new int[] {0, 1}, new int[] {1, 2}),
                buildDiagnosisHittingSet(             // c2, c5
                        new int[] {2}, new int[] {1}, new int[] {2},
                        new int[] {5}, new int[] {0, 1}, new int[] {1, 2}),
                buildDiagnosisHittingSet(                        // c2
                        new int[] {2}, new int[] {1}, new int[] {2}));

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);

        assertFalse(builder.isMinimalDiagnosisHittingSet(hittingSets.get(0), new HashSet<>(hittingSets)));
        assertFalse(builder.isMinimalDiagnosisHittingSet(hittingSets.get(2), new HashSet<>(hittingSets)));
        assertTrue(builder.isMinimalDiagnosisHittingSet(hittingSets.get(6), new HashSet<>(hittingSets)));
        assertTrue(builder.isMinimalDiagnosisHittingSet(hittingSets.get(8), new HashSet<>(hittingSets)));
    }

    @Test
    void testFilterMinimalDiagnosisHittingSets() {
        final CompleteTestModel model = mock(CompleteTestModel.class);

        final Set<Set<DiagnosisElement>> hittingSets = new HashSet<>(asList(
                new HashSet<>(asList(
                        new DiagnosisElement(1, new int[] {0}, new int[]{2}),
                        new DiagnosisElement(4, new int[] {0, 1}, new int[]{0, 2}),
                        new DiagnosisElement(5, new int[] {0, 1}, new int[]{1, 2}))),
                new HashSet<>(asList(
                        new DiagnosisElement(4, new int[] {0, 1}, new int[]{0, 2}),
                        new DiagnosisElement(5, new int[] {0, 1}, new int[]{1, 2}))),
                new HashSet<>(asList(
                        new DiagnosisElement(4, new int[] {0, 1}, new int[]{0, 2}),
                        new DiagnosisElement(2, new int[] {1}, new int[]{2}))),
                new HashSet<>(singletonList(
                        new DiagnosisElement(2, new int[]{1}, new int[]{2})))
        ));

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);
        final Set<Set<DiagnosisElement>> minimalHittingSets = builder.filterMinimalDiagnosisHittingSets(hittingSets);

        assertEquals(2, minimalHittingSets.size());
        assertTrue(contains(minimalHittingSets, new int[] {2}, new int[] {1}, new int[] {2}));
        assertTrue(contains(minimalHittingSets,
                new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                new int[] {5}, new int[] {0, 1}, new int[] {1, 2}));
    }

    @Test
    void testComputeMinimalDiagnosisHittingSetsForCTA2019Example() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, singletonList(new int[]{2})));                    // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, singletonList(new int[]{2})));                    // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, singletonList(new int[]{2})));                    // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, asList(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, asList(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);
        final List<DiagnosisHittingSet> minimalHittingSets = builder.computeMinimalDiagnosisHittingSets(missingInvalidTuples);

        assertEquals(2, minimalHittingSets.size());
        assertTrue(contains(minimalHittingSets,                        // c4, c5
                new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                new int[] {5}, new int[] {0, 1}, new int[] {1, 2}));
        assertTrue(contains(minimalHittingSets,                        // c2
                new int[] {2}, new int[] {1}, new int[] {2}));
    }

    @Test
    void testComputeMinimalDiagnosisHittingSetsForCTA2019ExampleWithCorrectConstraint() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, singletonList(new int[]{2})));                    // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, singletonList(new int[]{2}), true));                    // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, singletonList(new int[]{2})));                    // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, asList(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, asList(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);
        final List<DiagnosisHittingSet> minimalHittingSets = builder.computeMinimalDiagnosisHittingSets(missingInvalidTuples);

        assertEquals(1, minimalHittingSets.size());
        assertTrue(contains(minimalHittingSets,                        // c4, c5
                new int[] {4}, new int[] {0, 1}, new int[] {0, 2},
                new int[] {5}, new int[] {0, 1}, new int[] {1, 2}));
    }

    @Test
    void testComputeMinimalDiagnosisHittingSetsForCTA2019ExampleWith2CorrectConstraints() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, singletonList(new int[]{2})));                                        // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, singletonList(new int[]{2})));                                        // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, singletonList(new int[]{2})));                                        // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, asList(new int[]{0, 1}, new int[]{0, 2}), true));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, asList(new int[]{1, 0}, new int[]{1, 2}), true));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        final ReduceBasedDiagnosisHittingSetBuilder builder = new ReduceBasedDiagnosisHittingSetBuilder(model);
        final List<DiagnosisHittingSet> minimalHittingSets = builder.computeMinimalDiagnosisHittingSets(missingInvalidTuples);

        assertEquals(1, minimalHittingSets.size());
        assertTrue(contains(minimalHittingSets,                        // c2
                new int[] {2}, new int[] {1}, new int[] {2}));
    }
}
