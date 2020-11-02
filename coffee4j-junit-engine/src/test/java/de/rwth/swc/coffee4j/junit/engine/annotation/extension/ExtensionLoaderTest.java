package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionLoaderTest {
    
    @Test
    void loadsEmptyListIfNoExtensionSource() throws NoSuchMethodException {
        final Method method = getClass().getMethod("testMethodWithoutExtensionSources");
        final ExtensionLoader loader = new ExtensionLoader();
        
        final List<Extension> loadedExtensions = loader.load(method);
        
        assertNotNull(loadedExtensions);
        assertTrue(loadedExtensions.isEmpty());
    }
    
    @Test
    void loadsProvidedExtensions() throws NoSuchMethodException {
        final Method method = getClass().getMethod("testMethodWithExtensionSources");
        final ExtensionLoader loader = new ExtensionLoader();
    
        final List<Extension> loadedExtensions = loader.load(method);
        
        assertNotNull(loadedExtensions);
        assertEquals(2, loadedExtensions.size());
        assertTrue(loadedExtensions.get(0) instanceof FirstExtension);
        assertTrue(loadedExtensions.get(1) instanceof SecondExtension);
        assertEquals("test", ((SecondExtension) loadedExtensions.get(1)).name);
    }
    
    @Test
    void loadsProvidedExtensionsWithAnnotationInitialization() throws NoSuchMethodException {
        final Method method = getClass().getMethod("testMethodWithArgumentInitialization");
        final ExtensionLoader loader = new ExtensionLoader();
    
        final List<Extension> loadedExtensions = loader.load(method);
        
        assertNotNull(loadedExtensions);
        assertEquals(1, loadedExtensions.size());
        assertTrue(loadedExtensions.get(0) instanceof SecondExtension);
        assertEquals("someValue", ((SecondExtension) loadedExtensions.get(0)).name);
    }
    
    public void testMethodWithoutExtensionSources() {
        // only needed as a target for reflection
    }
    
    @ExtensionSource(FirstExtensionProvider.class)
    @ExtensionSource(SecondExtensionProvider.class)
    public void testMethodWithExtensionSources() {
        // only needed as a target for reflection
    }
    
    @ExtensionSource(ThirdExtensionProvider.class)
    @StringAnnotation("someValue")
    public void testMethodWithArgumentInitialization() {
        // only needed as a target for reflection
    }
    
    public static final class FirstExtension implements Extension {
    }
    
    public static final class SecondExtension implements Extension {
        
        private final String name;
        
        public SecondExtension(String name) {
            this.name = name;
        }
        
    }
    
    public static final class FirstExtensionProvider implements ExtensionProvider {
    
        @Override
        public List<Extension> provide(Method method) {
            return List.of(new FirstExtension());
        }
    
    }
    
    public static final class SecondExtensionProvider implements ExtensionProvider {
    
        @Override
        public List<Extension> provide(Method method) {
            return List.of(new SecondExtension("test"));
        }
    
    }
    
    public static final class ThirdExtensionProvider
            implements AnnotationConsumer<StringAnnotation>, ExtensionProvider {
    
        private String name;
    
        @Override
        public void accept(StringAnnotation stringAnnotation) {
            name = stringAnnotation.value();
        }
        
        @Override
        public List<Extension> provide(Method method) {
            return List.of(new SecondExtension(name));
        }
    
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StringAnnotation {
        
        String value();
        
    }
    
}
