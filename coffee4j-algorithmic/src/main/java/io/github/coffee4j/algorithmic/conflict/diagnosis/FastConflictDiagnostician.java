package io.github.coffee4j.algorithmic.conflict.diagnosis;

import io.github.coffee4j.algorithmic.conflict.InternalConflictSet;

import java.util.*;

public class FastConflictDiagnostician implements AbstractConflictDiagnostician {

    public int[][] getMinimalDiagnoses(InternalConflictSet conflict) {
        final List<int[]> diagnoses = new ArrayList<>();
        final Queue<int[]> pathsToExpand = new LinkedList<>(expandPaths(new int[0], conflict.getConflictSet()));

        while(!pathsToExpand.isEmpty()) {
            expandNextNode(conflict, diagnoses, pathsToExpand);

            if(!diagnoses.isEmpty()) {
                return new int[][] { diagnoses.getFirst() };
            }
        }

        return new int[0][];
    }
}
