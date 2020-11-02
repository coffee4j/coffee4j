package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ConstraintCheckerUtil {

    private ConstraintCheckerUtil() {
    }

    static Constraint findErrorConstraintToBeNegated(CompleteTestModel testModel, TupleList toBeNegated) {
        return testModel.getErrorConstraints().stream()
                .filter(constraint -> constraint.getTupleList().getId() == toBeNegated.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("unknown constraint"));
    }

    static List<Constraint> filterErrorConstraintToBeNegated(CompleteTestModel testModel, TupleList toBeNegated) {
        return testModel.getErrorConstraints().stream()
                .filter(constraint -> constraint.getTupleList().getId() != toBeNegated.getId())
                .collect(Collectors.toList());
    }

    static List<Constraint> errorConstraintsWithNegation(CompleteTestModel testModel, TupleList tupleList) {
        final List<Constraint> constraintsWithNegation = new ArrayList<>(testModel.getErrorConstraints().size());

        for (Constraint constraint : testModel.getErrorConstraints()) {
            if (constraint.getTupleList().getId() == tupleList.getId()) {
                constraintsWithNegation.add(negateConstraint(constraint));
            } else {
                constraintsWithNegation.add(constraint);
            }
        }

        return constraintsWithNegation;
    }

    static Constraint negateConstraint(Constraint constraint) {
        return new NegatedConstraint(constraint);
    }

    static List<Constraint> errorConstraintsWithExistentialNegation(CompleteTestModel testModel, TupleList tupleList) {
        final List<Constraint> constraintsWithNegation = new ArrayList<>(testModel.getErrorConstraints().size());

        for (Constraint constraint : testModel.getErrorConstraints()) {
            if (constraint.getTupleList().getId() == tupleList.getId()) {
                constraintsWithNegation.add(negateExistentialConstraint(constraint));
            } else {
                constraintsWithNegation.add(constraint);
            }
        }

        return constraintsWithNegation;
    }

    static Constraint negateExistentialConstraint(Constraint constraint) {
        final TupleList existentialTupleList = convertToExistentialTupleList(constraint.getTupleList());

        final ConstraintConverter constraintConverter = new ConstraintConverter();
        final Constraint existentialConstraint = constraintConverter.convert(existentialTupleList);

        return new NegatedConstraint(existentialConstraint);
    }

    static TupleList convertToExistentialTupleList(TupleList tupleList) {
        return new TupleList(
                tupleList.getId(),
                tupleList.getInvolvedParameters(),
                List.of(tupleList.getTuples().get(0)),
                tupleList.isMarkedAsCorrect());
    }

    static boolean checkValidIdentifier(CompleteTestModel testModel, int identifier) {
        return testModel.getExclusionConstraints()
                .stream()
                .anyMatch(constraint -> constraint.getTupleList().getId() == identifier)
            || testModel.getErrorConstraints()
                .stream()
                .anyMatch(constraint -> constraint.getTupleList().getId() == identifier);
    }
}
