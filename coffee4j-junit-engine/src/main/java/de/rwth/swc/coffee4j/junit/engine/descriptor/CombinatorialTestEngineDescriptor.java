package de.rwth.swc.coffee4j.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

/**
 * Very minimal description of a {@link org.junit.platform.engine.TestEngine}
 * in the {@link org.junit.platform.engine.TestDescriptor} hierarchy
 */
public class CombinatorialTestEngineDescriptor extends CombinatorialTestDescriptor {
    
    /**
     * Creates a new {@link CombinatorialTestEngineDescriptor} with the supplied {@link UniqueId} and display name
     *
     * @param uniqueId the {@link UniqueId} to set
     * @param displayName the display name to set
     */
    public CombinatorialTestEngineDescriptor(UniqueId uniqueId, String displayName) {
        super(uniqueId, displayName, null);
    }
    
    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
    
    @Override
    public void accept(CombinatorialTestDescriptorVisitor visitor) {
        visitor.visitCombinatorialTestEngineDescriptor(this);
    }
    
}
