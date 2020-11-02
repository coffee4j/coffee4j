package de.rwth.swc.coffee4j.algorithmic.classification;

/**
 * Factory used to create a {@link ClassificationStrategy} using a {@link ClassificationConfiguration}
 */
@FunctionalInterface
public interface ClassificationStrategyFactory {
    /**
     * @param configuration configuration for {@link ClassificationStrategy}
     * @return the created {@link ClassificationStrategy} using the provided configuration
     */
    ClassificationStrategy create(ClassificationConfiguration configuration);
}
