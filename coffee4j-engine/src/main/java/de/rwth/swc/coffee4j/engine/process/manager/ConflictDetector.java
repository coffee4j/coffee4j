package de.rwth.swc.coffee4j.engine.process.manager;

import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionManager;
import de.rwth.swc.coffee4j.algorithmic.conflict.DiagnosisHittingSet;
import de.rwth.swc.coffee4j.algorithmic.conflict.MissingInvalidTuple;
import de.rwth.swc.coffee4j.algorithmic.conflict.ReduceBasedDiagnosisHittingSetBuilder;
import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.process.report.LoggingConflictDetectionReporter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;

import java.util.List;

public class ConflictDetector {

    private final ConflictDetectionConfiguration conflictDetectionConfiguration;
    private final LoggingConflictDetectionReporter conflictDetectionReporter;
    private final ModelConverter modelConverter;

    public ConflictDetector(
            ConflictDetectionConfiguration conflictDetectionConfiguration, ModelConverter modelConverter) {
        
        this.conflictDetectionConfiguration = conflictDetectionConfiguration;
        this.conflictDetectionReporter = new LoggingConflictDetectionReporter(modelConverter);
        this.modelConverter = modelConverter;
    }

    /**
     * This is an adapted copy from the Coffee4j Junit Jupiter Extension.
     */
    public void diagnoseConstraints() {
        if(conflictDetectionConfiguration.isConflictDetectionEnabled()) {
            final boolean isConflictFree = checkConstraintsForConflicts();

            if(conflictDetectionConfiguration.shouldAbort() && !isConflictFree) {
                throw new Coffee4JException("Error: conflicts among constraints detected");
            }
        }
    }

    /**
     * Checks for conflicts among error-constraints, converts them and logs them using a {@link LoggingConflictDetectionReporter}
     * @return true if not conflicts were detected
     *         false if conflicts were detected
     */
    private boolean checkConstraintsForConflicts() {
        final List<MissingInvalidTuple> missingInvalidTuples = detectMissingInvalidTuples();

        if(missingInvalidTuples.isEmpty()) {
            return true;
        } else {
            conflictDetectionReporter.reportDetectedMissingInvalidTuples(missingInvalidTuples);

            if(conflictDetectionConfiguration.isConflictDiagnosisEnabled()) {
                final List<DiagnosisHittingSet> minimalHittingSets
                        = computeMinimalDiagnosisHittingSets(missingInvalidTuples);

                conflictDetectionReporter.reportMinimalDiagnosisHittingSets(minimalHittingSets);
            }

            return false;
        }
    }
    
    private List<MissingInvalidTuple> detectMissingInvalidTuples() {
        final ConflictDetectionManager conflictDetectionManager = new ConflictDetectionManager(
                conflictDetectionConfiguration,
                modelConverter.getConvertedModel());
    
        return conflictDetectionManager.detectMissingInvalidTuples();
    }
    
    private List<DiagnosisHittingSet> computeMinimalDiagnosisHittingSets(List<MissingInvalidTuple> missingInvalidTuples) {
        Preconditions.check(conflictDetectionConfiguration.isConflictDiagnosisEnabled());
    
        final ReduceBasedDiagnosisHittingSetBuilder builder
                = new ReduceBasedDiagnosisHittingSetBuilder(modelConverter.getConvertedModel());
    
        return builder.computeMinimalDiagnosisHittingSets(missingInvalidTuples);
    }
}
