package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.InternalMissingInvalidTuple;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.checkValidIdentifier;

public class DiagnosticConstraintCheckerFactory implements ConstraintCheckerFactory {

    private final DiagnosticConstraintSplitter splitter;
    private final DiagnosticConstraintThresholdComputer thresholdComputer;

    public DiagnosticConstraintCheckerFactory() {
        this(new DiagnosticConstraintSplitter(), new DiagnosticConstraintThresholdComputer());
    }

    public DiagnosticConstraintCheckerFactory(DiagnosticConstraintSplitter splitter,
                                              DiagnosticConstraintThresholdComputer thresholdComputer) {
        this.splitter = splitter;
        this.thresholdComputer = thresholdComputer;
    }

    @Override
    public ConstraintChecker createConstraintChecker(CompleteTestModel testModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintChecker createConstraintCheckerWithNegation(CompleteTestModel testModel, TupleList toBeNegated) {
        Preconditions.notNull(testModel);
        Preconditions.check(checkValidIdentifier(testModel, toBeNegated.getId()));

        final InternalConflictDiagnosisManager diagnostician = new InternalConflictDiagnosisManager();
        final List<InternalMissingInvalidTuple> missingInvalidTuples = diagnostician.diagnose(testModel, toBeNegated);

        if(missingInvalidTuples.isEmpty()) {
            return new HardConstraintCheckerFactory()
                    .createConstraintCheckerWithNegation(testModel, toBeNegated);
        } else {
            final Pair<List<Constraint>, List<Constraint>> partitions = splitter
                    .splitConstraints(testModel, toBeNegated, missingInvalidTuples);

            final List<Constraint> hardConstraints = partitions.getLeft();
            final List<Constraint> softConstraints = partitions.getRight();

            final Object2IntMap<IntArrayWrapper> thresholds = thresholdComputer
                    .computeThresholds(toBeNegated, missingInvalidTuples);

            return new DiagnosticConstraintChecker(testModel, toBeNegated, hardConstraints, softConstraints, thresholds);
        }
    }
}
