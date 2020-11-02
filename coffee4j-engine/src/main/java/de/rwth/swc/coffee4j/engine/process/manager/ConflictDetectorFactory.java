package de.rwth.swc.coffee4j.engine.process.manager;

import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;

/**
 * Factory for a {@link ConflictDetector}
 */
@FunctionalInterface
public interface ConflictDetectorFactory {

    /**
     * Creates a {@link ConflictDetector}
     *
     * @param conflictDetectionConfiguration  the configuration of the conflict detection
     * @param modelConverter the model convert that should be used for potential reporting
     * @return the created {@link ConflictDetector}
     */
    ConflictDetector create(ConflictDetectionConfiguration conflictDetectionConfiguration, ModelConverter modelConverter);

}
