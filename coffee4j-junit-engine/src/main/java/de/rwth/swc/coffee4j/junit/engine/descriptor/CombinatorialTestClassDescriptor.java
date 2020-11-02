package de.rwth.swc.coffee4j.junit.engine.descriptor;

import de.rwth.swc.coffee4j.junit.engine.UniqueIdGenerator;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

public class CombinatorialTestClassDescriptor extends CombinatorialTestDescriptor {
    
    private final Class<?> testClass;
    
    public CombinatorialTestClassDescriptor(TestDescriptor parent, Class<?> testClass) {
        super(UniqueIdGenerator.appendIdFromClass(parent.getUniqueId(), testClass),
                testClass.getSimpleName(), ClassSource.from(testClass));
        
        this.testClass = testClass;
    }
    
    public Class<?> getTestClass() {
        return testClass;
    }
    
    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
    
    @Override
    public void accept(CombinatorialTestDescriptorVisitor visitor) {
        visitor.visitCombinatorialTestClassDescriptor(this);
    }
    
}
