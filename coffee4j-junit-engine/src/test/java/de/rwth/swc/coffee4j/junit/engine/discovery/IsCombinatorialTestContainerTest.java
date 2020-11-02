package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsCombinatorialTestContainerTest {

    @Test
    void recognizesCombinatorialTestContainer() {
        final IsCombinatorialTestContainer predicate = new IsCombinatorialTestContainer();

        class LocalClass {}

        assertThat(predicate)
                .accepts(TestCase.class)
                .rejects(AbstractClass.class, PrivateClass.class, LocalClass.class, TestCase.InnerClass.class);
    }

    public static class TestCase {
        
        class InnerClass {
        }

        @CombinatorialTest
        void testMethod() {
        }
    }

    private abstract class AbstractClass { }

    private class PrivateClass { }
}
