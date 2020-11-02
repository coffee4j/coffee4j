package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.configuration.extension.generation.BeforeGenerationCallback;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstructorBasedExtensionProviderTest {

    @Test
    void singleUnknownExtension() throws NoSuchMethodException {
        ConstructorBasedExtensionProvider provider = new ConstructorBasedExtensionProvider();
        final Method method = this.getClass().getMethod("unknownExtensionTestMethod");
        
        provider.accept(method.getAnnotation(EnableExtension.class));
        final List<Extension> loadedExtensions = provider.provide(method);

        assertThat(loadedExtensions)
                .hasSize(1)
                .hasOnlyElementsOfType(UnknownExtension.class);
    }

    @Test
    void singleKnownExtension() throws NoSuchMethodException {
        ConstructorBasedExtensionProvider provider = new ConstructorBasedExtensionProvider();
        final Method method = this.getClass().getMethod("knownExtensionTestMethod");
    
        provider.accept(method.getAnnotation(EnableExtension.class));
        final List<Extension> loadedExtensions = provider.provide(method);

        assertThat(loadedExtensions)
                .hasSize(1)
                .hasOnlyElementsOfType(KnownExtension.class);
    }

    @Test
    void multipleExtensions() throws NoSuchMethodException {
        ConstructorBasedExtensionProvider provider = new ConstructorBasedExtensionProvider();
        final Method method = this.getClass().getMethod("multipleExtensionsTestMethod");
        
        provider.accept(method.getAnnotation(EnableExtension.class));
        final List<Extension> loadedExtensions = provider.provide(method);

        assertThat(loadedExtensions)
                .hasSize(2)
                .hasAtLeastOneElementOfType(KnownExtension.class)
                .hasAtLeastOneElementOfType(UnknownExtension.class);
    }
    
    @Test
    void configuredExtension() throws NoSuchMethodException {
        ConstructorBasedExtensionProvider provider = new ConstructorBasedExtensionProvider();
        final Method method = getClass().getMethod("configuredExtensionTestMethod");
    
        provider.accept(method.getAnnotation(EnableExtension.class));
        final List<Extension> loadedExtensions = provider.provide(method);
        
        assertEquals(1, loadedExtensions.size());
        assertTrue(loadedExtensions.get(0) instanceof ConfigurableExtension);
        assertEquals("someValue", ((ConfigurableExtension) loadedExtensions.get(0)).value);
    }

    @EnableExtension(UnknownExtension.class)
    public void unknownExtensionTestMethod() {
    }

    @EnableExtension(KnownExtension.class)
    public void knownExtensionTestMethod() {
    }

    @EnableExtension({UnknownExtension.class, KnownExtension.class})
    public void multipleExtensionsTestMethod() {
    }
    
    @EnableExtension(ConfigurableExtension.class)
    @ConfigurationSource(
            provider = ConstructorBasedExtensionProviderTest.StringConfigurationProvider.class,
            configurable = ConfigurableExtension.class,
            configuration = String.class)
    public void configuredExtensionTestMethod() {
    }

    static class UnknownExtension implements Extension {}

    static class KnownExtension implements BeforeGenerationCallback {
        @Override
        public void beforeGeneration() {}
    }
    
    public static final class ConfigurableExtension implements Extension {
        
        private final String value;
        
        public ConfigurableExtension(String value) {
            this.value = value;
        }
        
    }
    
    public static final class StringConfigurationProvider implements ConfigurationProvider<String> {
    
        @Override
        public String provide(Method method) {
            return "someValue";
        }
    
    }
    
}
