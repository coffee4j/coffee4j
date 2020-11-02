package de.rwth.swc.coffee4j.engine.process.extension;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.AfterFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.BeforeFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.BeforeExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.AfterGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.BeforeGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class DefaultExtensionExecutorTest implements MockingTest {

    private ExtensionExecutor executor;
    private Extension callback;

    @BeforeEach
    void prepareExecutor() {
        callback = mock(Extension.class,
                withSettings().extraInterfaces(
                        BeforeGenerationCallback.class, AfterGenerationCallback.class,
                        BeforeExecutionCallback.class, AfterExecutionCallback.class,
                        BeforeFaultCharacterizationCallback.class, AfterFaultCharacterizationCallback.class
                )
        );
        executor = new DefaultExtensionExecutor(List.of(callback));
    }

    @Test
    void executesBeforeGenerationCallback() {
        executor.executeBeforeGeneration();

        assertThat(callback)
                .isInstanceOf(BeforeGenerationCallback.class);
        final BeforeGenerationCallback beforeGenerationCallback = (BeforeGenerationCallback) callback;

        verify(beforeGenerationCallback).beforeGeneration();
    }

    @Test
    void executesAfterGenerationCallback() {
        final Combination comboOne = mock(Combination.class);
        final Combination comboTwo = mock(Combination.class);
        final Combination comboThree = mock(Combination.class);
        final List<Combination> unsortedList = List.of(comboThree, comboOne, comboTwo);
        final List<Combination> sortedList = List.of(comboOne, comboTwo, comboThree);

        assertThat(callback)
                .isInstanceOf(AfterGenerationCallback.class);
        final AfterGenerationCallback afterGenerationCallback = (AfterGenerationCallback) callback;
        when(afterGenerationCallback.afterGeneration(unsortedList)).thenReturn(sortedList);

        final List<Combination> providedList = executor.executeAfterGeneration(unsortedList);

        verify(afterGenerationCallback).afterGeneration(unsortedList);
        assertThat(providedList).isEqualTo(sortedList);
    }

    @Test
    void executesBeforeExecutionCallback() {
        final Combination comboOne = mock(Combination.class);
        final Combination comboTwo = mock(Combination.class);
        final Combination comboThree = mock(Combination.class);
        final List<Combination> comboList = List.of(comboThree, comboOne, comboTwo);

        assertThat(callback)
                .isInstanceOf(BeforeExecutionCallback.class);
        final BeforeExecutionCallback beforeExecutionCallback = (BeforeExecutionCallback) callback;

        executor.executeBeforeExecution(comboList);

        verify(beforeExecutionCallback).beforeExecution(comboList);
    }

    @Test
    void executesAfterExecutionCallback() {
        final Combination comboOne = mock(Combination.class);
        final Combination comboTwo = mock(Combination.class);
        final Combination comboThree = mock(Combination.class);

        final TestResult testResultOneOne = mock(TestResult.class);
        final TestResult testResultTwoOne = mock(TestResult.class);
        final TestResult testResultThreeOne = mock(TestResult.class);

        final TestResult testResultOneTwo = mock(TestResult.class);
        final TestResult testResultTwoTwo = mock(TestResult.class);

        final Map<Combination, TestResult> unalteredExecutionResultMap = Map.of(
                comboOne, testResultOneOne,
                comboTwo, testResultTwoOne,
                comboThree, testResultThreeOne
        );

        final Map<Combination, TestResult> alteredExecutionResultMap = Map.of(
                comboOne, testResultTwoTwo,
                comboTwo, testResultOneTwo,
                comboThree, testResultTwoTwo
        );

        assertThat(alteredExecutionResultMap).isNotEqualTo(unalteredExecutionResultMap);

        assertThat(callback)
                .isInstanceOf(AfterExecutionCallback.class);
        final AfterExecutionCallback afterExecutionCallback = (AfterExecutionCallback) callback;
        when(afterExecutionCallback.afterExecution(unalteredExecutionResultMap))
                .thenReturn(alteredExecutionResultMap);

        final Map<Combination, TestResult> providedExecutionResultMap = executor
                .executeAfterExecution(unalteredExecutionResultMap);

        verify(afterExecutionCallback)
                .afterExecution(unalteredExecutionResultMap);
        assertThat(providedExecutionResultMap)
                .isEqualTo(alteredExecutionResultMap);
    }

    @Test
    void executesBeforeFaultCharacterizationCallback() {
        final Combination comboOne = mock(Combination.class);
        final Combination comboTwo = mock(Combination.class);
        final Combination comboThree = mock(Combination.class);
        final TestResult testResultOne = mock(TestResult.class);
        final TestResult testResultTwo = mock(TestResult.class);
        final TestResult testResultThree = mock(TestResult.class);
        final Map<Combination, TestResult> comboMap = Map.of(
                comboOne, testResultOne,
                comboTwo, testResultTwo,
                comboThree, testResultThree
        );

        assertThat(callback)
                .isInstanceOf(BeforeFaultCharacterizationCallback.class);
        final BeforeFaultCharacterizationCallback beforeFaultCharacterizationCallback =
                (BeforeFaultCharacterizationCallback) callback;

        executor.executeBeforeFaultCharacterization(comboMap);

        verify(beforeFaultCharacterizationCallback).beforeFaultCharacterization(comboMap);
    }

    @Test
    void executesAfterFaultCharacterizationCallback() {
        final Combination comboOne = mock(Combination.class);
        final Combination comboTwo = mock(Combination.class);
        final Combination comboThree = mock(Combination.class);
        final List<Combination> comboList = List.of(comboThree, comboOne, comboTwo);

        assertThat(callback)
                .isInstanceOf(AfterFaultCharacterizationCallback.class);
        final AfterFaultCharacterizationCallback afterFaultCharacterizationCallback = (AfterFaultCharacterizationCallback) callback;

        executor.executeAfterFaultCharacterization(comboList);

        verify(afterFaultCharacterizationCallback).afterFaultCharacterization(comboList);
    }
}
