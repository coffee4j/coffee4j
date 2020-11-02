package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationBasedInstanceCreator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorBasedExtensionProvider implements AnnotationConsumer<EnableExtension>, ExtensionProvider {
    
    private Class<? extends Extension>[] extensionClasses;
    
    @Override
    public void accept(EnableExtension configuration) {
        extensionClasses = configuration.value();
    }
    
    @Override
    public List<Extension> provide(Method method) {
        return Arrays.stream(extensionClasses)
                .map(extensionClass -> ConfigurationBasedInstanceCreator.create(extensionClass, method))
                .collect(Collectors.toList());
    }
    
}
