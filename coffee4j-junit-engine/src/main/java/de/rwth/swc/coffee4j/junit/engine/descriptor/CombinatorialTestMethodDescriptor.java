package de.rwth.swc.coffee4j.junit.engine.descriptor;

import de.rwth.swc.coffee4j.junit.engine.UniqueIdGenerator;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

/**
 * JUnit description of a combinatorial test.
 */
public class CombinatorialTestMethodDescriptor extends CombinatorialTestDescriptor {
    
    private final Method method;
    
    /**
     * Creates a new {@link CombinatorialTestMethodDescriptor} with the supplied {@link UniqueId}, display name, and
     * {@link TestSource}.
     *
     * @param parent the descriptor of the parent object in the hierarchy in junit
     * @param method the actual test method
     */
    public CombinatorialTestMethodDescriptor(TestDescriptor parent, Method method) {
        super(UniqueIdGenerator.appendIdFromMethod(parent.getUniqueId(), method),
                method.getName(), MethodSource.from(method));
        
        this.method = method;
    }
    
    public Method getMethod() {
        return method;
    }
    
    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
    
    @Override
    public boolean mayRegisterTests() {
        return true;
    }
    
    @Override
    public void accept(CombinatorialTestDescriptorVisitor visitor) {
        visitor.visitCombinatorialTestMethodDescriptor(this);
    }
    
}
