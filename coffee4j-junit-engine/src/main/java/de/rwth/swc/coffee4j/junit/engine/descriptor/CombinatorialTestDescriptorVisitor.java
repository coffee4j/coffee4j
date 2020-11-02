package de.rwth.swc.coffee4j.junit.engine.descriptor;

public interface CombinatorialTestDescriptorVisitor {
    
    void visitCombinatorialTestEngineDescriptor(CombinatorialTestEngineDescriptor descriptor);
    
    void visitCombinatorialTestClassDescriptor(CombinatorialTestClassDescriptor descriptor);
    
    void visitCombinatorialTestMethodDescriptor(CombinatorialTestMethodDescriptor descriptor);
    
}
