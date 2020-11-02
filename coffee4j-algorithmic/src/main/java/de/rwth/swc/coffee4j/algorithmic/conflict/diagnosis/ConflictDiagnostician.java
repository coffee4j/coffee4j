package de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;

public interface ConflictDiagnostician {

    int[][] getMinimalDiagnoses(InternalConflictSet conflict);
}
