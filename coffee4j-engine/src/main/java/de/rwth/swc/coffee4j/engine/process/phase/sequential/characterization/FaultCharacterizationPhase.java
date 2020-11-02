package de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization;

import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.commons.function.Try;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages a fault characterization phase
 */
public class FaultCharacterizationPhase extends AbstractPhase<SequentialGenerationContext, Map<Combination, TestResult>, List<Combination>> {

    /**
     * Creates a new {@link FaultCharacterizationPhase} with a given {@link SequentialGenerationContext}
     * @param context the context with which to configure the {@link FaultCharacterizationPhase}
     */
    public FaultCharacterizationPhase(SequentialGenerationContext context) {
        super(context);
    }

    /**
     * Executes the {@link FaultCharacterizationPhase} as configured with the {@link SequentialGenerationContext} once.
     * Consumes an input and provides an output.
     *
     * @param input the test results of the combinations.
     *              Must not be null or contain any null values or keys.
     * @return the additionally generated combinations based upon the configured {@link SequentialGenerationContext} and the input
     */
    @Override
    public List<Combination> execute(Map<Combination, TestResult> input) {
        Preconditions.notNull(input);
        Preconditions.check(Try.call(() -> !input.containsKey(null)).toOptional().orElse(true));
        Preconditions.check(Try.call(() -> !input.containsValue(null)).toOptional().orElse(true));

        // Before Fault Characterization
        context.getExtensionExecutor().executeBeforeFaultCharacterization(input);

        // Fault Characterization Phase
        final List<Combination> additionalInput = input.entrySet().stream()
                .flatMap(entry ->
                        context.getGenerator()
                                .generateAdditionalTestInputsWithResult(
                                        context.getModelConverter()
                                                .convertCombination(entry.getKey()),
                                        entry.getValue()
                                ).stream()
                                .map(intCombination -> context.getModelConverter()
                                        .convertCombination(intCombination)
                                )
                )
                .collect(Collectors.toList());

        // After Fault Characterization
        context.getExtensionExecutor().executeAfterFaultCharacterization(additionalInput);

        return additionalInput;
    }
}
