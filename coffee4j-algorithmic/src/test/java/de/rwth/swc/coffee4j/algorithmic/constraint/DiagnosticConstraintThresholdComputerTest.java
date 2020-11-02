package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalDiagnosisSets;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalMissingInvalidTuple;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DiagnosticConstraintThresholdComputerTest {

    private static final CompleteTestModel MODEL = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .parameterSizes(3, 3, 3)
            .errorTupleLists(List.of(
                    new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})),
                    new TupleList(2, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})),
                    new TupleList(3, new int[]{2}, List.of(new int[]{1}))))
            .build();

    @Test
    void testComputeSingleThresholds() {
        final DiagnosticConstraintThresholdComputer computer = new DiagnosticConstraintThresholdComputer();

        final List<InternalMissingInvalidTuple> missingInvalidTuples = Arrays.asList(
                new InternalMissingInvalidTuple(1, new int[]{0, 1}, new int[]{0, 0},
                        new InternalDiagnosisSets(mock(InternalConflictSet.class), new int[][] { new int[] {2} })),
                new InternalMissingInvalidTuple(1, new int[]{0, 1}, new int[]{1, 1},
                        new InternalDiagnosisSets(mock(InternalConflictSet.class), new int[][] { new int[] {2} })));

        final Object2IntMap<IntArrayWrapper> map = computer.computeThresholds(MODEL.getErrorTupleLists().get(0), missingInvalidTuples);

        assertEquals(1, map.getInt(wrap(0, 0)));
        assertEquals(1, map.getInt(wrap(1, 1)));
    }

    @Test
    void testComputeThresholds() {
        final DiagnosticConstraintThresholdComputer computer = new DiagnosticConstraintThresholdComputer();

        final List<InternalMissingInvalidTuple> missingInvalidTuples = Arrays.asList(
                new InternalMissingInvalidTuple(1, new int[]{0, 1}, new int[]{0, 0},
                        new InternalDiagnosisSets(mock(InternalConflictSet.class), new int[][] { new int[] {2} })),
                new InternalMissingInvalidTuple(1, new int[]{0, 1}, new int[]{1, 1},
                        new InternalDiagnosisSets(mock(InternalConflictSet.class), new int[][] { new int[] {2, 3} })));

        final Object2IntMap<IntArrayWrapper> map = computer.computeThresholds(MODEL.getErrorTupleLists().get(0), missingInvalidTuples);

        assertEquals(1, map.getInt(wrap(0, 0)));
        assertEquals(2, map.getInt(wrap(1, 1)));
    }

    @Test
    void testComputeThresholdsWithoutMissingInvalidTuples() {
        final DiagnosticConstraintThresholdComputer computer = new DiagnosticConstraintThresholdComputer();

        final List<InternalMissingInvalidTuple> missingInvalidTuples = Collections.emptyList();
        final Object2IntMap<IntArrayWrapper> map = computer.computeThresholds(MODEL.getErrorTupleLists().get(2), missingInvalidTuples);

        assertEquals(0, map.getInt(wrap(0, 0)));
        assertEquals(0, map.getInt(wrap(1, 1)));

    }
}
