package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.choco.ChocoModel;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.*;
import de.rwth.swc.coffee4j.algorithmic.constraint.Constraint;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ConflictDetectionManager {

    private final ConflictDetectionConfiguration configuration;
    private final ConflictExplainer explainer;
    private final ConflictDiagnostician diagnostician;
    private final TestModelExpander expander;

    private final CompleteTestModel testModel;
    private final Map<Boolean, List<Constraint>> partitionedConstraints;

    private final ChocoModel chocoModel;

    public ConflictDetectionManager(ConflictDetectionConfiguration configuration,
                                    CompleteTestModel originalTestModel) {
        Preconditions.notNull(configuration);
        Preconditions.notNull(originalTestModel);

        this.configuration = configuration;
        this.explainer = configuration.createConflictExplainer();
        this.diagnostician = configuration.createConflictDiagnostician();
        this.expander = configuration.createTestModelExpander(originalTestModel);

        this.testModel = expander.createExpandedTestModel();

        final List<Constraint> constraints = new ArrayList<>();
        constraints.addAll(this.testModel.getExclusionConstraints());
        constraints.addAll(this.testModel.getErrorConstraints());

        this.partitionedConstraints = constraints.stream()
                .collect(groupingBy(constraint -> constraint.getTupleList().isMarkedAsCorrect()));

        if(!partitionedConstraints.containsKey(true)) {
            partitionedConstraints.put(true, Collections.emptyList());
        }

        this.chocoModel = new ChocoModel(this.testModel.getParameterSizes(), constraints);
    }

    public List<MissingInvalidTuple> detectMissingInvalidTuples() {
        if(!configuration.isConflictDetectionEnabled()) {
            return Collections.emptyList();
        }

        final ConflictDetectionResultConverter converter = new ConflictDetectionResultConverter(testModel, expander);

        return testModel.getErrorTupleLists().stream()
                .map(this::checkForNegatedErrorConstraint)
                .flatMap(Collection::stream)
                .map(converter::convertMissingInvalidTuple)
                .collect(Collectors.toList());
    }

    private List<InternalMissingInvalidTuple> checkForNegatedErrorConstraint(TupleList toBeNegated) {
        final List<InternalMissingInvalidTuple> missingInvalidTuples = new ArrayList<>();

        chocoModel.reset();
        chocoModel.setNegationOfConstraint(toBeNegated.getId());

        for(int[] tuple : toBeNegated.getTuples()) {
            final IntSet background = new IntArraySet();
            background.add(toBeNegated.getId());
            background.addAll(partitionedConstraints.get(true).stream()
                    .map(constraint -> constraint.getTupleList().getId())
                    .collect(Collectors.toList()));

            final IntSet relaxable = new IntArraySet();
            relaxable.addAll(partitionedConstraints.get(false).stream()
                    .filter(constraint -> constraint.getTupleList().getId() != toBeNegated.getId())
                    .map(constraint -> constraint.getTupleList().getId())
                    .collect(Collectors.toList()));

            final Optional<InternalExplanation> optional
                    = checkForInvalidTuple(toBeNegated, tuple, background, relaxable);
            optional.ifPresent(explanation ->
                    missingInvalidTuples.add(new InternalMissingInvalidTuple(
                            toBeNegated.getId(),
                            toBeNegated.getInvolvedParameters(),
                            tuple,
                            explanation)));
        }

        chocoModel.resetNegationOfConstraint();
        return missingInvalidTuples;
    }

    private Optional<InternalExplanation> checkForInvalidTuple(TupleList tupleList,
                                                               int[] tuple,
                                                               IntSet background,
                                                               IntSet relaxable) {
        chocoModel.reset();

        final int assignmentId = chocoModel.setAssignmentConstraint(tupleList.getInvolvedParameters(), tuple);

        background.add(assignmentId);

        try {
            final Optional<InternalExplanation> optional = createExplanation(background.toIntArray(), relaxable.toIntArray());

            return optional.map(explanation -> {
                if(configuration.isConflictDiagnosisEnabled() && explanation instanceof InternalConflictSet) {
                    return new InternalDiagnosisSets((InternalConflictSet) explanation, diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation));
                } else if(explanation instanceof InternalInconsistentBackground) {
                    return removeAssignmentConstraintFromBackground(assignmentId, (InternalInconsistentBackground) explanation);
                } else {
                    return explanation;
                }
            });

        } finally {
            chocoModel.clearAssignmentConstraint();

            background.remove(assignmentId);
        }
    }

    private InternalExplanation removeAssignmentConstraintFromBackground(int assignmentId,
                                                                         InternalInconsistentBackground explanation) {
        final int[] cleanedBackground = Arrays.stream(explanation.getBackground())
                .filter(c -> c != assignmentId)
                .toArray();

        return new InternalInconsistentBackground(cleanedBackground, explanation.getRelaxable());
    }

    private Optional<InternalExplanation> createExplanation(int[] background, int[] relaxable) {
        if(!configuration.isConflictExplanationEnabled()) {
            chocoModel.reset();

            if(chocoModel.isSatisfiable()) {
                return Optional.empty();
            } else {
                return Optional.of(
                        new InternalUnknownExplanation()
                );
            }
        } else {
            return explainer.getMinimalConflict(chocoModel, background, relaxable);
        }
    }
}
