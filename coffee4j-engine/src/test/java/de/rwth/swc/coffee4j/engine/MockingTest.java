package de.rwth.swc.coffee4j.engine;

import org.junit.jupiter.api.AfterAll;
import org.mockito.Mockito;

public interface MockingTest {

    @AfterAll
    static void clearMocks() {
        Mockito.framework().clearInlineMocks();
    }

}
