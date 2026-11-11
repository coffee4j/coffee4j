package io.github.coffee4j.algorithmic.constraint;

import io.github.coffee4j.algorithmic.model.TupleList;
import io.github.coffee4j.algorithmic.conflict.InternalDiagnosisSets;
import io.github.coffee4j.algorithmic.conflict.InternalMissingInvalidTuple;
import io.github.coffee4j.algorithmic.util.IntArrayWrapper;
import io.github.coffee4j.algorithmic.util.Preconditions;
import io.github.coffee4j.algorithmic.util.TupleUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.github.coffee4j.algorithmic.util.IntArrayWrapper.wrap;

class DiagnosticConstraintThresholdComputer {

    Object2IntMap<IntArrayWrapper> computeThresholds(TupleList toBeNegated,
                                    List<InternalMissingInvalidTuple> missingInvalidTuples) {
        Preconditions.notNull(toBeNegated);
        Preconditions.notNull(missingInvalidTuples);
        Preconditions.check(missingInvalidTuples.stream()
                .allMatch(mit -> mit.getExplanation() instanceof InternalDiagnosisSets));

        final Object2IntMap<IntArrayWrapper> thresholds = new Object2IntArrayMap<>();

        for(int[] tuple : toBeNegated.getTuples()) {
            final Optional<InternalMissingInvalidTuple> optional = findMissingInvalidTuple(toBeNegated, tuple, missingInvalidTuples);

            final int threshold = optional.map(this::computeThreshold).orElse(0);

            thresholds.put(wrap(tuple), threshold);
        }

        return thresholds;
    }

    private Optional<InternalMissingInvalidTuple> findMissingInvalidTuple(TupleList toBeNegated,
                                                                          int[] tuple,
                                                                          List<InternalMissingInvalidTuple> missingInvalidTuples) {
        return missingInvalidTuples.stream()
                .filter(missingInvalidTuple ->
                        missingInvalidTuple.getNegatedErrorConstraintId() == toBeNegated.getId())
                .filter(missingInvalidTuple ->
                        TupleUtil.tuplesAreEqual(
                                 missingInvalidTuple.getInvolvedParameters(),
                                missingInvalidTuple.getMissingValues(),
                                toBeNegated.getInvolvedParameters(),
                                tuple))
                .findFirst();
    }

    private int computeThreshold(InternalMissingInvalidTuple missingInvalidTuple) {
        final InternalDiagnosisSets sets = (InternalDiagnosisSets) missingInvalidTuple.getExplanation();

        return Arrays.stream(sets.getDiagnosisSets())
                .mapToInt(diagnosisSet -> diagnosisSet.length)
                .max()
                .orElseThrow(() -> new IllegalStateException("no diagnosis sets"));
    }
}
