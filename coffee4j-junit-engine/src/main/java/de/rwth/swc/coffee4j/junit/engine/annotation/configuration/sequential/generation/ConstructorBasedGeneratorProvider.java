package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationBasedInstanceCreator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class ConstructorBasedGeneratorProvider implements GeneratorProvider, AnnotationConsumer<EnableGeneration> {

    private Class<? extends TestInputGroupGenerator>[] generatorClasses;

    @Override
    public void accept(EnableGeneration generator) {
        generatorClasses = generator.algorithms();
    }

    @Override
    public Collection<TestInputGroupGenerator> provide(Method method) {
        return Arrays.stream(generatorClasses)
                .map(generatorClass -> ConfigurationBasedInstanceCreator.create(generatorClass, method))
                .collect(Collectors.toList());
    }
    
}
