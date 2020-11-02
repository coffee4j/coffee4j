package de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization;

import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FaultCharacterizationPhaseTest implements MockingTest {

    private static final Combination combinationThree = mock(Combination.class);
    private static final Combination combinationFour = mock(Combination.class);
    private static final SequentialGenerationContext context = mock(SequentialGenerationContext.class);
    private static final ExtensionExecutor extensionExecutor = mock(ExtensionExecutor.class);
    private static final SequentialCombinatorialTestManager SEQUENTIAL_COMBINATORIAL_TEST_MANAGER = mock(SequentialCombinatorialTestManager.class);
    private static Map<Combination, TestResult> testResultMap;
    private static int[] intCombinationOne;
    private static int[] intCombinationTwo;
    private static TestResult testResultOne;
    private static TestResult testResultTwo;

    @BeforeAll
    static void prepareMocks() {
        final Combination combinationOne = mock(Combination.class);
        final Combination combinationTwo = mock(Combination.class);
        testResultOne = mock(TestResult.class);
        testResultTwo = mock(TestResult.class);
        testResultMap = Map.of(combinationOne, testResultOne,
                combinationTwo, testResultTwo);

        intCombinationOne = new int[] {1,2,3};
        intCombinationTwo = new int[] {4,5,6};
        final int[] intCombinationThree = new int[] {7,8,9};
        final int[] intCombinationFour = new int[] {1,5,9};

        final ModelConverter modelConverter = mock(ModelConverter.class);
        when(modelConverter.convertCombination(combinationOne))
                .thenReturn(intCombinationOne);
        when(modelConverter.convertCombination(combinationTwo))
                .thenReturn(intCombinationTwo);
        when(modelConverter.convertCombination(intCombinationThree))
                .thenReturn(combinationThree);
        when(modelConverter.convertCombination(intCombinationFour))
                .thenReturn(combinationFour);

        when(SEQUENTIAL_COMBINATORIAL_TEST_MANAGER.generateAdditionalTestInputsWithResult(intCombinationOne, testResultOne))
                .thenReturn(List.of(intCombinationThree, intCombinationFour));
        when(SEQUENTIAL_COMBINATORIAL_TEST_MANAGER.generateAdditionalTestInputsWithResult(intCombinationTwo, testResultTwo))
                .thenReturn(Collections.emptyList());

        when(context.getExtensionExecutor())
                .thenReturn(extensionExecutor);
        when(context.getGenerator())
                .thenReturn(SEQUENTIAL_COMBINATORIAL_TEST_MANAGER);
        when(context.getModelConverter())
                .thenReturn(modelConverter);
    }

    @Test
    void executesPhase() {
        final FaultCharacterizationPhase phase = new FaultCharacterizationPhase(context);

        final List<Combination> providedCombinations = phase.execute(testResultMap);

        assertThat(providedCombinations)
                .containsExactly(combinationThree, combinationFour);

        final InOrder inOrder = inOrder(extensionExecutor);
        inOrder.verify(extensionExecutor)
                .executeBeforeFaultCharacterization(testResultMap);
        inOrder.verify(extensionExecutor)
                .executeAfterFaultCharacterization(providedCombinations);

        verify(SEQUENTIAL_COMBINATORIAL_TEST_MANAGER)
                .generateAdditionalTestInputsWithResult(intCombinationOne, testResultOne);
        verify(SEQUENTIAL_COMBINATORIAL_TEST_MANAGER)
                .generateAdditionalTestInputsWithResult(intCombinationTwo, testResultTwo);
    }
}
