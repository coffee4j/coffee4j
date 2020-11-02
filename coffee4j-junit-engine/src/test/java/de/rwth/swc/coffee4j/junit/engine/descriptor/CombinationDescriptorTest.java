package de.rwth.swc.coffee4j.junit.engine.descriptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class CombinationDescriptorTest {

    @AfterEach
    void clearMocks() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void singleCombinationDescriptor() throws NoSuchMethodException {
        final UniqueId someId = UniqueId.forEngine("someEngineId");

        final String someDisplayName = "someDisplayName";
        final CombinationDescriptor combinationDescriptor =
                new CombinationDescriptor(someId, someDisplayName, this.getClass().getMethod("someMethod"));

        assertThat(combinationDescriptor)
                .extracting(
                        TestDescriptor::getType,
                        TestDescriptor::getUniqueId,
                        TestDescriptor::getDisplayName)
                .containsExactly(
                        TestDescriptor.Type.TEST,
                        someId,
                        someDisplayName);
    }
    
    public void someMethod() {
    }
    
}
