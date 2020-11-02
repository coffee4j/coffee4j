package de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.GeneratingInterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InterleavingClassificationPhaseTest {
    
    private static final InterleavingGenerationContext context = mock(InterleavingGenerationContext.class);
    private static final GeneratingInterleavingCombinatorialTestManager INTERLEAVING_COMBINATORIAL_TEST_MANAGER = mock(GeneratingInterleavingCombinatorialTestManager.class);

    static final ExtensionExecutor extensionExecutor = mock(ExtensionExecutor.class);
    static final ModelConverter modelConverter = mock(ModelConverter.class);

    static final TestResult result = TestResult.failure(new ErrorConstraintException());

    static final Combination combination1 = mock(Combination.class);
    static final int[] combination1Int = new int[]{0,1};
    static final Combination combination2 = mock(Combination.class);
    static final int[] combination2Int = new int[]{1,0};
    static final Combination combination3 = mock(Combination.class);
    static final int[] combination3Int = new int[]{1,1};

    final static Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs = new HashMap<>();
    final static Map<int[], TestResult> errorConstraintExceptionCausingTestInputsInt = new HashMap<>();

    @BeforeAll
    static void prepareMocks() {
        when(context.getExtensionExecutor())
                .thenReturn(extensionExecutor);
        when(context.getTestManager())
                .thenReturn(INTERLEAVING_COMBINATORIAL_TEST_MANAGER);
        when(context.getModelConverter())
                .thenReturn(modelConverter);

        errorConstraintExceptionCausingTestInputs.put(combination1, result);
        errorConstraintExceptionCausingTestInputs.put(combination2, result);

        errorConstraintExceptionCausingTestInputsInt.put(combination1Int, result);
        errorConstraintExceptionCausingTestInputsInt.put(combination2Int, result);

        when(modelConverter.convertCombination(combination1))
                .thenReturn(combination1Int);

        when(modelConverter.convertCombination(combination2))
                .thenReturn(combination2Int);

        when(modelConverter.convertCombination(combination3))
                .thenReturn(combination3Int);
        when(modelConverter.convertCombination(combination3Int))
                .thenReturn(combination3);

        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.initializeClassification(errorConstraintExceptionCausingTestInputsInt))
                .thenReturn(Optional.of(combination3Int));

        when(INTERLEAVING_COMBINATORIAL_TEST_MANAGER.generateNextTestInput(combination3Int, TestResult.success()))
                .thenReturn(Optional.of(combination3Int));
    }

    @Test
    void executePhase() {
        final InterleavingClassificationPhase phase = new InterleavingClassificationPhase(context);
        Combination combination = phase.initialize(errorConstraintExceptionCausingTestInputs);

        assertEquals(combination3, combination);

        Map<Combination, TestResult> previouslyExecutedTests = new HashMap<>();
        previouslyExecutedTests.put(combination3, TestResult.success());

        combination = phase.execute(previouslyExecutedTests);

        verify(INTERLEAVING_COMBINATORIAL_TEST_MANAGER, times(1))
                .generateNextTestInput(combination3Int, TestResult.success());

        assertEquals(combination3, combination);
    }

    @Test
    void testInvalidInput() {
        Map<Combination, TestResult> input1 = new HashMap<>();
        input1.put(null,null);

        final InterleavingClassificationPhase phase = new InterleavingClassificationPhase(context);

        assertThrows(IllegalArgumentException.class, () -> phase.execute(input1));

        Map<Combination, TestResult> input2 = new HashMap<>();
        input2.put(Combination.empty(), null);

        assertThrows(IllegalArgumentException.class, () -> phase.execute(input2));
    }
    
}
