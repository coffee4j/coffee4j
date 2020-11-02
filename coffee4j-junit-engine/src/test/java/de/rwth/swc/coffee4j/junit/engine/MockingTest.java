package de.rwth.swc.coffee4j.junit.engine;

import org.junit.jupiter.api.AfterAll;
import org.mockito.Mockito;

public interface MockingTest {

    @AfterAll
    static void clearMocks() {
        Mockito.framework().clearInlineMocks();
    }

}
