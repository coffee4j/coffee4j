package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.rwth.swc.coffee4j.engine.constraint.diagnosis.ConflictingErrorConstraintSearcher.*;

public class BasicConstraintDiagnosisManager {

    private final CombinatorialTestModel model;

    public BasicConstraintDiagnosisManager(CombinatorialTestModel model) {
        this.model = model;
    }

    public List<InternalConflict> checkForConflicts() {
        final ConstraintCheckerFactory checkerFactory = new ConstraintCheckerFactory(model);

        return checkAllTupleListsForConflicts(checkerFactory);
    }

    private List<InternalConflict> checkAllTupleListsForConflicts(ConstraintCheckerFactory checkerFactory) {
        final List<InternalConflict> internalConflicts = new ArrayList<>();

        for(TupleList tupleList : model.getErrorTupleLists()) {
            final ConflictingErrorConstraintSearcher conflictSearcher = checkerFactory.createConflictingErrorConstraintsSearcher(tupleList);

            findPreferredExplanationForTupleList(tupleList, conflictSearcher)
                    .ifPresent(internalConflicts::add);
        }

        return internalConflicts;
    }

    private Optional<InternalConflict> findPreferredExplanationForTupleList(TupleList tupleList,
                                                                            ConflictingErrorConstraintSearcher searcher) {
        for(int[] tuple : tupleList.getTuples()) {
            final IntList preferredExplanation = searcher.findAndExplainConflict(tupleList.getInvolvedParameters(), tuple);

            if(hasNoConflict(preferredExplanation)) {
                // do nothing and check next tuple
            } else if(isInconsistent(preferredExplanation, tupleList.getId())) {
                final InternalConflict internalConflict = new InternalConflict(
                        tupleList.getInvolvedParameters(),
                        tuple,
                        tupleList,
                        new TupleList[] { tupleList }
                );

                return Optional.of(internalConflict);
            } else if(conflictIsNotMinimal(preferredExplanation)) {
                final TupleList[] conflictingTupleLists = model.getErrorTupleLists()
                        .stream()
                        .filter(conflictingTupleList -> preferredExplanation.contains(conflictingTupleList.getId()))
                        .toArray(TupleList[]::new);

                final InternalConflict internalConflict = new InternalConflict(
                        tupleList.getInvolvedParameters(),
                        tuple,
                        tupleList,
                        new TupleList[0]
                );

                return Optional.of(internalConflict);
            } else {
                final TupleList[] conflictingTupleLists = model.getErrorTupleLists()
                        .stream()
                        .filter(conflictingTupleList -> preferredExplanation.contains(conflictingTupleList.getId()))
                        .toArray(TupleList[]::new);

                final InternalConflict internalConflict = new InternalConflict(
                        tupleList.getInvolvedParameters(),
                        tuple,
                        tupleList,
                        conflictingTupleLists
                );

                return Optional.of(internalConflict);
            }
        }

        return Optional.empty();
    }
}
