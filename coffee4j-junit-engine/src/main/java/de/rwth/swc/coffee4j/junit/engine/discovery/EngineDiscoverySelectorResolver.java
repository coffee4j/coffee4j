package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestEngineDescriptor;
import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestDescriptor;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;

/**
 * Resolves an {@link EngineDiscoveryRequest} for the {@link CombinatorialTestEngine}
 *
 * This is an adapted copy of {@code org.junit.jupiter.engine.discovery.DiscoverySelectorResolver}
 */
public class EngineDiscoverySelectorResolver {

    private static final EngineDiscoveryRequestResolver<CombinatorialTestEngineDescriptor> resolver =
            EngineDiscoveryRequestResolver.<CombinatorialTestEngineDescriptor>builder()
                    .addClassContainerSelectorResolver(new IsCombinatorialTestContainer())
                    .addSelectorResolver(new ClassSelectorResolver())
                    .addSelectorResolver(new MethodSelectorResolver())
                    .build();

    /**
     * Populates the supplied {@link CombinatorialTestEngineDescriptor}
     * with {@link CombinatorialTestDescriptor}s
     * based on test classes supplied in the request
     * according to the definition of combinatorial test classes.
     *
     * @param request the request in which to look for combinatorial test classes
     * @param engineDescriptor the root descriptor which to populate
     */
    public void resolveSelectors(EngineDiscoveryRequest request, CombinatorialTestEngineDescriptor engineDescriptor) {
        resolver.resolve(request, engineDescriptor);
    }
    
}
