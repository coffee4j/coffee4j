package de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.conflict.InternalConflictSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ExhaustiveConflictDiagnostician implements AbstractConflictDiagnostician  {

    public int[][] getMinimalDiagnoses(InternalConflictSet conflict) {
        final List<int[]> diagnoses = new ArrayList<>();
        final Queue<int[]> pathsToExpand = new LinkedList<>(expandPaths(new int[0], conflict.getConflictSet()));

        while(!pathsToExpand.isEmpty()) {
            expandNextNode(conflict, diagnoses, pathsToExpand);
        }

        return diagnoses.toArray(new int[0][]);
    }
}
