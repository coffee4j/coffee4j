package io.github.coffee4j.engine.process.phase.model;

import io.github.coffee4j.engine.configuration.TestMethodConfiguration;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import io.github.coffee4j.engine.process.extension.ExtensionExecutor;
import io.github.coffee4j.engine.process.phase.AbstractPhase;
import io.github.coffee4j.engine.configuration.extension.model.ModelModifier;

/**
 * Phase which modifies the {@link InputParameterModel} in a {@link TestMethodConfiguration}
 * using the {@link ModelModifier modifiers} given in a {@link ExtensionExecutor}.
 */
public class ModelModificationPhase
        extends AbstractPhase<ModelModificationContext, InputParameterModel, InputParameterModel> {
    
    /**
     * Creates a new instance with the context containing the model modifiers to use when executing the phase.
     *
     * @param context phase context containing the model modifiers
     */
    public ModelModificationPhase(ModelModificationContext context) {
        super(context);
    }
    
    @Override
    public InputParameterModel execute(InputParameterModel originalModel) {
        final InputParameterModel modifiedModel = context.extensionExecutor()
                .executeModelModifiers(originalModel, context.reporter());
        
        if (modifiedModel != null) {
            return modifiedModel;
        } else {
            return originalModel;
        }
    }
    
}
