package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class ConstructorBasedConverterProvider implements ConverterProvider, AnnotationConsumer<EnableConverter> {

    private Class<? extends ArgumentConverter>[] converterClasses;

    @Override
    public void accept(EnableConverter converter) {
        converterClasses = converter.value();
    }

    @Override
    public Collection<ArgumentConverter> provide(Method method) {
        return Arrays.stream(converterClasses)
                .map(ReflectionUtils::createNewInstance)
                .collect(Collectors.toList());
    }

}
