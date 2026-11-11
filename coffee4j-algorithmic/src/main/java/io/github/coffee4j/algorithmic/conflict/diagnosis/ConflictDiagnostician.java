package io.github.coffee4j.algorithmic.conflict.diagnosis;

import io.github.coffee4j.algorithmic.conflict.InternalConflictSet;

public interface ConflictDiagnostician {

    int[][] getMinimalDiagnoses(InternalConflictSet conflict);
}
