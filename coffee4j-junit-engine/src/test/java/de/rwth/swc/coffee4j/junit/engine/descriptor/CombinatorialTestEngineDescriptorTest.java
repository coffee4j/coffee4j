package de.rwth.swc.coffee4j.junit.engine.descriptor;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import static org.assertj.core.api.Assertions.assertThat;

class CombinatorialTestEngineDescriptorTest {

    @Test
    void singleEngineDescriptor() {
        final UniqueId someEngineId = UniqueId.forEngine("someEngineId");
        final String someDisplayName = "someDisplayName";

        final CombinatorialTestEngineDescriptor engineDescriptor =
                new CombinatorialTestEngineDescriptor(someEngineId, someDisplayName);

        assertThat(engineDescriptor)
                .extracting(
                        TestDescriptor::getType,
                        TestDescriptor::getUniqueId,
                        TestDescriptor::getDisplayName)
                .containsExactly(
                        TestDescriptor.Type.CONTAINER,
                        someEngineId,
                        someDisplayName);
    }
    
}
