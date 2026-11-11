package io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration;

import io.github.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import io.github.coffee4j.algorithmic.sequential.generator.ipogneg.IpogNegConfiguration;
import io.github.coffee4j.junit.engine.annotation.AnnotationConsumer;
import io.github.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import io.github.coffee4j.junit.engine.annotation.util.configuration.ConfigurationProvider;

import java.lang.reflect.Method;

public class IpogNegConfigurationProvider
        implements ConfigurationProvider<IpogNegConfiguration>, AnnotationConsumer<ConfigureIpogNeg> {

    private Class<? extends ConstraintCheckerFactory> constraintCheckerFactoryClass;
    private int strengthA;

    @Override
    public void accept(ConfigureIpogNeg configureIpogNeg) {
        constraintCheckerFactoryClass = configureIpogNeg.constraintCheckerFactory();
        strengthA = configureIpogNeg.strengthA();
    }

    @Override
    public IpogNegConfiguration provide(Method method) {
        final ConstraintCheckerFactory constraintCheckerFactory = ReflectionUtils.createNewInstance(constraintCheckerFactoryClass);

        return new IpogNegConfiguration(constraintCheckerFactory, strengthA);
    }
    
}
