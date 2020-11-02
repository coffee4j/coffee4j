package de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;

public class NoConflictDiagnostician implements ConflictDiagnostician {

    @Override
    public int[][] getMinimalDiagnoses(InternalConflictSet conflict) {
        return new int[0][];
    }
}
