package de.rwth.swc.coffee4j.junit.engine;

import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestEngineDescriptor;
import de.rwth.swc.coffee4j.junit.engine.discovery.EngineDiscoverySelectorResolver;
import de.rwth.swc.coffee4j.junit.engine.execution.CombinatorialTestExecutor;
import de.rwth.swc.coffee4j.junit.engine.execution.ExecutionContext;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.*;

/**
 * A JUnit {@link TestEngine} which provides combinatorial testing capabilities
 * via the coffee4j framework and using annotations.
 * <p>
 *     This engine is also registered via the service loader mechanism by Java.
 *     It is thus not necessary to set the engine somewhere.
 *     As long as this project is imported the launcher automatically picks up this engine.
 * </p>
 */
public class CombinatorialTestEngine implements TestEngine {

	public static final String ENGINE_ID = "coffee4j";

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	/**
	 * {@inheritDoc}
	 * @see EngineDiscoverySelectorResolver
	 */
	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		CombinatorialTestEngineDescriptor
				engineDescriptor = new CombinatorialTestEngineDescriptor(uniqueId, ENGINE_ID);
		new EngineDiscoverySelectorResolver().resolveSelectors(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		final TestDescriptor rootDescriptor = request.getRootTestDescriptor();
		
		if (rootDescriptor instanceof CombinatorialTestEngineDescriptor) {
			final CombinatorialTestEngineDescriptor engineDescriptor
					= (CombinatorialTestEngineDescriptor) rootDescriptor;
			final EngineExecutionListener executionListener = request.getEngineExecutionListener();
			final ExecutionContext rootContext = ExecutionContext.fromExecutionListener(executionListener);
			
			engineDescriptor.accept(new CombinatorialTestExecutor(rootContext));
		} else {
			throw new JUnitException("Root test descriptor has to be of type "
					+ CombinatorialTestEngineDescriptor.class.getCanonicalName());
		}
	}

}
