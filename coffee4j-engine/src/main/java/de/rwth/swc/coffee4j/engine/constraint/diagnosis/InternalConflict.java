package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.TupleList;

public class InternalConflict {
    private final int[] parameters;
    private final int[] values;
    private final TupleList sourceTupleList;
    private final TupleList[] targetTupleLists;

    public InternalConflict(int[] parameters,
                            int[] values,
                            TupleList sourceTupleList,
                            TupleList[] targetTupleLists) {
        this.parameters = parameters;
        this.values = values;
        this.sourceTupleList = sourceTupleList;
        this.targetTupleLists = targetTupleLists;
    }

    public int[] getParameters() {
        return parameters;
    }

    public int[] getValues() {
        return values;
    }

    public TupleList getSourceTupleList() {
        return sourceTupleList;
    }

    public TupleList[] getTargetTupleLists() {
        return targetTupleLists;
    }
}
