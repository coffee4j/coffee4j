package de.rwth.swc.coffee4j.engine.process.phase.model;

import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.AbstractPhase;
import de.rwth.swc.coffee4j.engine.configuration.extension.model.ModelModifier;

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
        final InputParameterModel modifiedModel = context.getExtensionExecutor()
                .executeModelModifiers(originalModel, context.getReporter());
        
        if (modifiedModel != null) {
            return modifiedModel;
        } else {
            return originalModel;
        }
    }
    
}
