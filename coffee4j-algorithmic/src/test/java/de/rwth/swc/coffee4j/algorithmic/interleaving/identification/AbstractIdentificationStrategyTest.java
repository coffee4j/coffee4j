package de.rwth.swc.coffee4j.algorithmic.interleaving.identification;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class AbstractIdentificationStrategyTest {
    protected IdentificationStrategy strategy;
    protected Map<int[], Throwable> failureInducingCombinations;

    @Test
    void failureInducingCombinationCorrectlyIdentified() {
        failureInducingCombinations.put(new int[]{-1,0,-1,-1}, new ErrorConstraintException());

        computeMinimalFailureInducingCombinations(strategy.startIdentification(new int[]{0,0,0,0}, TestResult.failure(new ErrorConstraintException("failure!"))));

        Map<IntList, CombinationType> fics = strategy.getIdentifiedCombinations();

        assertFalse(fics.isEmpty());

        Set<IntList> expected = new HashSet<>();
        Set<IntList> found = new HashSet<>();

        fics.forEach((fic, result) -> found.add(fic));
        failureInducingCombinations.keySet().forEach(fic -> expected.add(new IntArrayList(fic)));

        assertEquals(expected, found);
    }

    @Test
    void exceptionInducingCombinationBesidesFaultCorrectlyIdentified() {
        failureInducingCombinations.put(new int[]{0,0,-1,-1}, new ErrorConstraintException());
        failureInducingCombinations.put(new int[]{1,0,-1,-1}, new AssertionError());

        computeMinimalFailureInducingCombinations(strategy.startIdentification(new int[]{0,0,0,0}, TestResult.failure(new ErrorConstraintException())));

        Map<IntList, CombinationType> fics = strategy.getIdentifiedCombinations();

        assertFalse(fics.isEmpty());

        Set<IntList> expected = new HashSet<>();
        Set<IntList> found = new HashSet<>();

        fics.forEach((fic, result) -> found.add(fic));
        expected.add(new IntArrayList(new int[]{0,0,-1,-1}));

        assertEquals(expected, found);
    }

    protected void computeMinimalFailureInducingCombinations(Optional<int[]> nextTestInput) {
        while (nextTestInput.isPresent()) {
            TestResult result = TestResult.success();

            for (Map.Entry<int[], Throwable> fic : failureInducingCombinations.entrySet()) {
                if (CombinationUtil.contains(nextTestInput.get(), fic.getKey())) {
                    result = TestResult.failure(fic.getValue());
                    break;
                }
            }

            nextTestInput = strategy.generateNextTestInputForIdentification(nextTestInput.get(), result);
        }
    }
}
