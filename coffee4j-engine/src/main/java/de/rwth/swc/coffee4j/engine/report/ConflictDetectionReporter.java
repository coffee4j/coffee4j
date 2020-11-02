package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.conflict.DiagnosisHittingSet;
import de.rwth.swc.coffee4j.algorithmic.conflict.MissingInvalidTuple;

import java.util.List;

public interface ConflictDetectionReporter {

    void reportDetectedMissingInvalidTuples(List<MissingInvalidTuple> missingInvalidTuples);

    void reportMinimalDiagnosisHittingSets(List<DiagnosisHittingSet> minimalHittingSets);
    
}
