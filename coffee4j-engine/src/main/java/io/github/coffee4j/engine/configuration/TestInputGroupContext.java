package io.github.coffee4j.engine.configuration;

import io.github.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import io.github.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import io.github.coffee4j.algorithmic.util.Preconditions;
import io.github.coffee4j.engine.report.ExecutionReporter;

/**
 * A class representing a {@link TestInputGroup} to the outside. This does not
 * contain test inputs but therefore the {@link TestInputGroupGenerator} used to generate the group.
 * It is used for identification in {@link ExecutionReporter}.
 */
public record TestInputGroupContext(Object identifier, TestInputGroupGenerator generator) {

    /**
     * Creates a new context with the given identifier and generator.
     *
     * @param identifier a unique identifier of this test input group
     * @param generator  the generator used to generate the test input group
     */
    public TestInputGroupContext {
        Preconditions.notNull(identifier);
        Preconditions.notNull(generator);
    }
}
