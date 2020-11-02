package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InterleavingGenerationPhaseTest implements MockingTest {
    private static final InterleavingGenerationContext context = mock(InterleavingGenerationContext.class);
    private static final ExtensionExecutor extensionExecutor = mock(ExtensionExecutor.class);
    private static final ModelConverter modelConverter = mock(ModelConverter.class);
    private static final InterleavingCombinatorialTestManager INTERLEAVING_COMBINATORIAL_TEST_MANAGER = mock(InterleavingCombinatorialTestManager.class);

    private static final Combination failingCombination = mock(Combination.class);
    private static final int[] failingCombinationInt = new int[]{0,0};
    private static final TestResult result = TestResult.failure(new ErrorConstraintException());

    private static final Combination combination1 = mock(Combination.class);
    private static final int[] combination1Int = new int[]{0,1};
    private static final Combination combination2 = mock(Combination.class);
    private static final int[] combination2Int = new int[]{1,0};
    private static final Combination combination3 = mock(Combination.class);
    private static final int[] combination3Int = new int[]{1,1};


    @BeforeAll
    static void prepareMocks() {
        when(context.getExtensionExecutor())
                .thenReturn(extensionExecutor);
        when(context.getTestManager())
                .thenReturn(INTERLEAVING_COMBINATORIAL_TEST_MANAGER);
        when(context.getModelConverter())
                .thenReturn(modelConverter);

        when(modelConverter.convertCombination(failingCombination))
                .thenReturn(failingCombinationInt);
        when(modelConverter.convertCombination(failingCombinationInt))
                .thenReturn(failingCombination);

        when(modelConverter.convertCombination(combination1))
                .thenReturn(combination1Int);
        when(modelConverter.convertCombination(combination1Int))
                .thenReturn(combination1);

        when(modelConverter.convertCombination(combination2))
                .thenReturn(combination2Int);
        when(modelConverter.convertCombination(combination2Int))
                .thenReturn(combination2);

        when(modelConverter.convertCombination(combination3))
                .thenReturn(combination3Int);
        when(modelConverter.convertCombination(combination3Int))
                .thenReturn(combination3);

        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.generateNextTestInput(null, null))
                .thenReturn(Optional.of(combination1Int));
        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.generateNextTestInput(combination1Int, TestResult.success()))
                .thenReturn(Optional.of(combination2Int));
        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.generateNextTestInput(combination2Int, result))
                .thenReturn(Optional.of(combination3Int));

        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.reinitializeIdentification())
                .thenReturn(Optional.of(combination3Int));
    }

    @Test
    void executePhase() {
        final InterleavingGenerationPhase phase = new InterleavingGenerationPhase(context);
        Combination combination;
        Map<Combination, TestResult> previouslyExecutedTests = new HashMap<>();

        combination = phase.execute(previouslyExecutedTests);

        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(1))
                .generateNextTestInput(null, null);
        assertEquals(combination1, combination);

        previouslyExecutedTests.put(combination1, TestResult.success());

        combination = phase.execute(previouslyExecutedTests);

        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(1))
                .updateCoverage(combination1Int);
        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(1))
                .generateNextTestInput(combination1Int, TestResult.success());

        assertEquals(combination2, combination);

        previouslyExecutedTests.remove(combination1);
        previouslyExecutedTests.put(combination2, result);

        combination = phase.execute(previouslyExecutedTests);

        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(0))
                .updateCoverage(combination2Int);
        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(1))
                .generateNextTestInput(combination2Int, result);

        assertEquals(combination3, combination);
    }

}
