package de.rwth.swc.coffee4j.junit.provider.configuration.generator;

import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.JUnitException;

import java.util.ArrayList;
import java.util.List;

class ConstructorBasedGeneratorProvider implements GeneratorProvider, AnnotationConsumer<Generator> {
    
    private Class<? extends TestInputGroupGenerator>[] generatorClasses;
    
    @Override
    public void accept(Generator generatorSource) {
        generatorClasses = generatorSource.value();
    }
    
    @Override
    public List<TestInputGroupGenerator> provide(ExtensionContext extensionContext) {
        final List<TestInputGroupGenerator> generators = new ArrayList<>();
        
        for (Class<? extends TestInputGroupGenerator> generatorClass : generatorClasses) {
            generators.add(createGeneratorInstance(generatorClass));
        }
        
        return generators;
    }
    
    private TestInputGroupGenerator createGeneratorInstance(Class<? extends TestInputGroupGenerator> generatorClass) {
        try {
            return generatorClass.getConstructor().newInstance();
        } catch (Exception e) {
            final String message = "Could not create a new instance of " + generatorClass.getSimpleName() + " with a default constructor";
            throw new JUnitException(message, e);
        }
    }
    
}
