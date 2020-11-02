package de.rwth.swc.coffee4j.engine.process.phase.sequential.generation;

import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a generation phase of a combinatorial test
 */
public class SequentialGenerationPhase extends AbstractPhase<SequentialGenerationContext, InputParameterModel, List<Combination>> {

    /**
     * Creates a new {@link SequentialGenerationPhase} with the configured {@link SequentialGenerationContext}
     *
     * @param context the {@link SequentialGenerationContext} with which to configure the {@link SequentialGenerationPhase}
     */
    public SequentialGenerationPhase(SequentialGenerationContext context) {
        super(context);
    }

    /**
     * Executes this phase by generating the initial test set of the combinatorial test
     * in the manner configured in the {@link SequentialGenerationContext} supplied in {@link #SequentialGenerationPhase(SequentialGenerationContext)}
     *
     * @param input the {@link InputParameterModel} of the combinatorial test
     * @return the generated combinations
     */
    @Override
    public List<Combination> execute(InputParameterModel input) {
        //Before Generation Phase
        context.getExtensionExecutor().executeBeforeGeneration();

        // Generation Phase
        final List<Combination> generatedCombinations = context.getGenerator()
                .generateInitialTests()
                .stream()
                .map(combination -> context.getModelConverter().convertCombination(combination))
                .collect(Collectors.toList());

        // After Generation Phase
        return context.getExtensionExecutor().executeAfterGeneration(generatedCombinations);
    }
}
