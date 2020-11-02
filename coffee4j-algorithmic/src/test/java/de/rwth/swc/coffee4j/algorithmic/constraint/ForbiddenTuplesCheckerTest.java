package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ForbiddenTuplesCheckerTest {
    private static ConstraintChecker checker;

    @BeforeAll
    static void initChecker() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{0, 2})));
        forbiddenTupleLists.add(new TupleList(2, new int[]{0, 3}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0})));
        forbiddenTupleLists.add(new TupleList(3, new int[]{1, 3}, Collections.singletonList(new int[]{1, 0})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .exclusionTupleLists(forbiddenTupleLists)
                .build();

        checker = new MinimalForbiddenTuplesChecker(model);
    }

    @Test
    void checkGenerationOfMinimalForbiddenTupleSet() {
        Set<IntList> minimalForbiddenTuples = new HashSet<>();
        minimalForbiddenTuples.add(new IntArrayList(new int[]{0,0,-1,-1}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{0,2,-1,-1}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{-1,-1,-1,0}));

        assertEquals(((ForbiddenTuplesChecker) checker).getMinimalForbiddenTuples(), minimalForbiddenTuples);
    }

    @Test
    void checkGenerationOfMinimalForbiddenTupleSetAfterAddingForbiddenTuple() {
        Set<IntList> minimalForbiddenTuples = new HashSet<>();
        minimalForbiddenTuples.add(new IntArrayList(new int[]{0,0,-1,-1}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{0,2,-1,-1}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{-1,-1,-1,0}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{-1,1,1,-1}));
        minimalForbiddenTuples.add(new IntArrayList(new int[]{0,-1,1,-1}));

        checker.addConstraint(new int[]{-1,1,1,-1});

        assertEquals(((ForbiddenTuplesChecker) checker).getMinimalForbiddenTuples(), minimalForbiddenTuples);
    }

    @Test
    void testIsValidWithAdditionalParameter() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 2}, Collections.singletonList(new int[]{1, 1})));
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(forbiddenTupleLists)
                .build();

        final ConstraintChecker solver = new MinimalForbiddenTuplesChecker(model);

        assertTrue(solver.isExtensionValid(new int[]{1, 0}, 2, 0));
        assertFalse(solver.isExtensionValid(new int[]{1, 0}, 2, 1));
    }

    @ParameterizedTest
    @MethodSource("provideTestInputs")
    void testIsValid(int[] testInput, boolean isFault) {
        assertEquals(checker.isValid(testInput), !isFault);
    }

    private static Stream<Arguments> provideTestInputs() {
        Int2IntMap map = new Int2IntArrayMap();
        map.put(0,3);
        map.put(1,3);
        map.put(2,3);
        map.put(3,3);

        Map<int[], Boolean> tests = new HashMap<>();

        Set<int[]> forbiddenTuples = new HashSet<>();
        forbiddenTuples.add(new int[]{0,0,-1,-1});
        forbiddenTuples.add(new int[]{0,2,-1,-1});
        forbiddenTuples.add(new int[]{-1,-1,-1,0});

        for (int[] testInput : Combinator.computeCartesianProduct(map, 4)) {
            boolean isFault = false;
            for (int[] forbiddenTuple : forbiddenTuples) {
                if (CombinationUtil.contains(testInput, forbiddenTuple))
                    isFault = true;
            }

            tests.put(testInput, isFault);
        }

        return tests.entrySet().stream().map(test -> Arguments.of(test.getKey(), test.getValue()));
    }
}
