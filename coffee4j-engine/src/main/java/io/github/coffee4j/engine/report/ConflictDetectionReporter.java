package io.github.coffee4j.engine.report;

import io.github.coffee4j.algorithmic.conflict.DiagnosisHittingSet;
import io.github.coffee4j.algorithmic.conflict.MissingInvalidTuple;

import java.util.List;

public interface ConflictDetectionReporter {

    void reportDetectedMissingInvalidTuples(List<MissingInvalidTuple> missingInvalidTuples);

    void reportMinimalDiagnosisHittingSets(List<DiagnosisHittingSet> minimalHittingSets);
    
}
