package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.FaultCharacterizationAlgorithmFactoryProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * {@link FaultCharacterizationAlgorithmFactoryProvider} using the constructor of the provided characterization strategy.
 */
public class ConstructorBasedSequentialConstraintGenerationProvider implements FaultCharacterizationAlgorithmFactoryProvider, AnnotationConsumer<EnableSequentialConstraintGeneration> {
    private Class<? extends GeneratingFaultCharacterizationAlgorithm> generatingAlgorithmClass;

    @Override
    public FaultCharacterizationAlgorithmFactory provide(Method method) {
        Constructor<? extends GeneratingFaultCharacterizationAlgorithm> constructor;

        try {
            constructor = generatingAlgorithmClass.getConstructor(FaultCharacterizationConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new Coffee4JException(e, "The class %s must have public constructor which accepts a %s",
                    generatingAlgorithmClass.getName(),
                    FaultCharacterizationConfiguration.class.getSimpleName());
        }

        return configuration -> {
            try {
                return constructor.newInstance(configuration);
            } catch (Exception e) {
                throw new Coffee4JException(
                        e,
                        "Could not create a new instance of %s the given constructor %s",
                        generatingAlgorithmClass.getName(),
                        constructor.getName()
                );
            }
        };
    }

    @Override
    public void accept(EnableSequentialConstraintGeneration enableSequentialConstraintGeneration) {
        generatingAlgorithmClass = enableSequentialConstraintGeneration.value();
    }
}
