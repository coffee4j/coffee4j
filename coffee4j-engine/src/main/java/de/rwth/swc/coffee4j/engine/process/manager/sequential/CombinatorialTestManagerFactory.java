package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;

import java.util.function.BiFunction;

/**
 * A factory for creating new {@link SequentialCombinatorialTestManager} instances from a given configuration.
 * This is needed to reuse {@link SequentialExecutionConfiguration} instances, as they can now be
 * reused for multiple {@link CompleteTestModel test models}.
 */
@FunctionalInterface
public interface CombinatorialTestManagerFactory extends BiFunction<SequentialCombinatorialTestConfiguration, CompleteTestModel, SequentialCombinatorialTestManager> {
}
