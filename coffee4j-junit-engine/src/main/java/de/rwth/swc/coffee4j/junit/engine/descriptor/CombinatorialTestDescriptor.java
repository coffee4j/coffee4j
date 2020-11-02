package de.rwth.swc.coffee4j.junit.engine.descriptor;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

/**
 * Base class for the JUnit {@link org.junit.platform.engine.TestDescriptor}s
 * used by the {@link CombinatorialTestEngine}
 */
public abstract class CombinatorialTestDescriptor extends AbstractTestDescriptor {

    /**
     * Creates a new {@link CombinatorialTestDescriptor} with the supplied {@link UniqueId}, display name, and {@link TestSource}
     *
     * @param uniqueId the {@link UniqueId} of this {@link org.junit.platform.engine.TestDescriptor}
     * @param displayName the display name of this {@link org.junit.platform.engine.TestDescriptor}
     * @param source the {@link TestSource} of this {@link org.junit.platform.engine.TestDescriptor}
     */
    public CombinatorialTestDescriptor(UniqueId uniqueId, String displayName, TestSource source) {
        super(uniqueId, displayName, source);
    }
    
    public abstract void accept(CombinatorialTestDescriptorVisitor visitor);

}
