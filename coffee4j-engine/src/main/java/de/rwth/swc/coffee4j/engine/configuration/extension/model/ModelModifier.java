package de.rwth.swc.coffee4j.engine.configuration.extension.model;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;

/**
 * An interface for an extension which modifies an input parameter model before the normal combinatorial testing
 * process.
 *
 * <p>Example uses:
 * <ul>
 *     <li>Adding seed test cases from csv files</li>
 *     <li>Adding information from previous CT runs (e.g. weights/seeds)</li>
 * </ul>
 */
public interface ModelModifier extends Extension {
    
    /**
     * Modifies the model in an implementation specific way and returns the newly computed input parameter model.
     * This model may be further processed by another {@link ModelModifier}.
     *
     * @param original the original {@link InputParameterModel}. Is never {@code null}
     * @return the modified model. Should never be {@code null}
     */
    InputParameterModel modify(InputParameterModel original);

}
