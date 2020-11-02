package de.rwth.swc.coffee4j.engine.process.extension;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.AfterFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.characterization.BeforeFaultCharacterizationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.BeforeExecutionCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.AfterGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.BeforeGenerationCallback;
import de.rwth.swc.coffee4j.engine.configuration.extension.model.ModelModifier;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.util.ReversedUnmodifiableListView;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default {@link ExtensionExecutor}, that manages the execution of all known Extensions.
 * <p>
 * Ensures wrapping behavior for extensions.
 * I.e., assume callback A and callback B both implement the callback before and after an entity.
 * If callback A gets registered before callback B, wrapping behavior would ensure,
 * that its behavior before the entity is always executed before the corresponding behavior of callback B
 * and its behavior after the entity is always executed after the corresponding behavior of callback B.
 */
public class DefaultExtensionExecutor implements ExtensionExecutor {
    
    private static final Set<Class<? extends Extension>> KNOWN_EXTENSION_TYPES = Set.of(
            ModelModifier.class, BeforeGenerationCallback.class, AfterGenerationCallback.class,
            BeforeExecutionCallback.class, AfterExecutionCallback.class,
            BeforeFaultCharacterizationCallback.class, AfterFaultCharacterizationCallback.class);

    private final List<ModelModifier> modelModifiers;
    private final List<BeforeGenerationCallback> beforeGenerationCallbacks;
    private final List<AfterGenerationCallback> afterGenerationCallbacks;
    private final List<AfterExecutionCallback> afterExecutionCallbacks;
    private final List<BeforeExecutionCallback> beforeExecutionCallbacks;
    private final List<BeforeFaultCharacterizationCallback> beforeFaultCharacterizationCallbacks;
    private final List<AfterFaultCharacterizationCallback> afterFaultCharacterizationCallbacks;

    /**
     * Creates a new executor with the given extensions.
     *
     * @param extensions  the list of Extensions to be managed
     * @throws Coffee4JException  if not all Extensions types are known by this Executor
     */
    public DefaultExtensionExecutor(Collection<? extends Extension> extensions) {
        Preconditions.notNull(extensions);
        assertContainsOnlyKnowsExtensionTypes(extensions);
        
        this.modelModifiers = getFilteredExtensionList(extensions, ModelModifier.class);
        
        this.beforeGenerationCallbacks = getFilteredExtensionList(extensions, BeforeGenerationCallback.class);
        this.afterGenerationCallbacks = getFilteredExtensionList(extensions, AfterGenerationCallback.class);

        this.beforeExecutionCallbacks = getFilteredExtensionList(extensions, BeforeExecutionCallback.class);
        this.afterExecutionCallbacks =  getFilteredExtensionList(extensions, AfterExecutionCallback.class);

        this.beforeFaultCharacterizationCallbacks = getFilteredExtensionList(extensions, BeforeFaultCharacterizationCallback.class);
        this.afterFaultCharacterizationCallbacks = getFilteredExtensionList(extensions, AfterFaultCharacterizationCallback.class);
    }
    
    private void assertContainsOnlyKnowsExtensionTypes(Collection<? extends Extension> extensions) {
        for (Extension extension : extensions) {
            boolean isKnownExtensionType = KNOWN_EXTENSION_TYPES.stream()
                    .anyMatch(type -> type.isInstance(extension));
            
            if (!isKnownExtensionType) {
                throw new IllegalArgumentException("Unknown extension type " + extension.getClass().getCanonicalName());
            }
        }
    }

    private <T extends  Extension> List<T> getFilteredExtensionList(
            Collection<? extends Extension> extensions, Class<T> clazz) {
        
        return extensions.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
    
    @Override
    public InputParameterModel executeModelModifiers(InputParameterModel original, ExecutionReporter reporter) {
        InputParameterModel current = original;
        
        for (ModelModifier modifier : modelModifiers) {
            final InputParameterModel old = current;
            current = modifier.modify(current);
            reporter.modelModified(old, current);
        }
        
        return current;
    }
    
    @Override
    public void executeBeforeGeneration() {
        for (BeforeGenerationCallback callback : beforeGenerationCallbacks)
            callback.beforeGeneration();
    }

    /**
     * {@inheritDoc}
     * @throws Coffee4JException  if the items of the input and output list do not match
     */
    @Override
    public List<Combination> executeAfterGeneration(List<Combination> combinations) {
        List<Combination> returnValue = new ArrayList<>(combinations);
        
        if (!afterGenerationCallbacks.isEmpty()) {
            for (AfterGenerationCallback callback : ReversedUnmodifiableListView.of(afterGenerationCallbacks)) {
                returnValue = callback.afterGeneration(returnValue);
            }
    
            boolean sameInputAsOutputValues = combinations.containsAll(returnValue)
                    && returnValue.containsAll(combinations);
    
            if (!sameInputAsOutputValues) {
                throw new Coffee4JException("One of the AfterGenerationCallbacks modified the list of combinations");
            }
        }
        
        return returnValue;
    }

    @Override
    public void executeBeforeExecution(List<Combination> combinations) {
        for (BeforeExecutionCallback callback : beforeExecutionCallbacks)
            callback.beforeExecution(combinations);
    }

    /**
     * {@inheritDoc}
     * @throws Coffee4JException if a callback returned null or a callback modified the combinations or executors
     */
    @Override
    public Map<Combination, TestResult> executeAfterExecution(Map<Combination, TestResult> executionResultMap) {
        Map<Combination, TestResult> returnValue = new LinkedHashMap<>(executionResultMap);
        
        if (!afterExecutionCallbacks.isEmpty()) {
            for (AfterExecutionCallback callback : ReversedUnmodifiableListView.of(afterExecutionCallbacks)) {
                returnValue = callback.afterExecution(returnValue);
                if (returnValue == null) {
                    throw new Coffee4JException(String.format(
                            "The executionCallback %s returned a null value, which it should not have.",
                            callback.getClass().getName()));
                }
            }
            
            boolean combinationsModified = !Objects.equals(returnValue.keySet(), executionResultMap.keySet());
            boolean atLeastOneResultIsNull = returnValue.values().stream()
                    .anyMatch(Objects::isNull);
            
            if (combinationsModified || atLeastOneResultIsNull) {
                throw new Coffee4JException("One of the AfterExecutionCallbacks modified the combinations " +
                        "or contained a null result");
            }
        }
        
        return returnValue;
    }

    @Override
    public void executeBeforeFaultCharacterization(Map<Combination, TestResult> combinationTestResultMap) {
        for (BeforeFaultCharacterizationCallback callback : beforeFaultCharacterizationCallbacks) {
            callback.beforeFaultCharacterization(combinationTestResultMap);
        }
    }

    @Override
    public void executeAfterFaultCharacterization(List<Combination> combinations) {
        for (AfterFaultCharacterizationCallback callback :
                ReversedUnmodifiableListView.of(afterFaultCharacterizationCallbacks)) {
            callback.afterFaultCharacterization(combinations);
        }
    }

}
