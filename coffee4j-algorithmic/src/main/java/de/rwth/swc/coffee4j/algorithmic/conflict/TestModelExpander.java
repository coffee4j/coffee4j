package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TestModelExpander {

    private final int factor;
    private final CompleteTestModel testModel;

    TestModelExpander(CompleteTestModel testModel) {
        Preconditions.notNull(testModel);

        this.testModel = testModel;
        this.factor = computeFactor(testModel);
    }

    int getFactor() {
        return factor;
    }

    CompleteTestModel createExpandedTestModel() {
        final List<TupleList> exclusionTuples = expandTupleLists(testModel.getExclusionTupleLists());
        final List<TupleList> errorTuples = expandTupleLists(testModel.getErrorTupleLists());

        return CompleteTestModel.builder(testModel)
                .exclusionTupleLists(exclusionTuples)
                .errorTupleLists(errorTuples)
                .build();
    }

    int computeOriginalId(TupleList tupleList) {
        return computeOriginalId(tupleList.getId());
    }

    int computeOriginalId(int id) {
        Preconditions.check(id > 0);

        return id / factor;
    }

    int computeOriginalIndexInTupleList(TupleList tupleList) {
        Preconditions.notNull(tupleList);

        return tupleList.getId() % factor;
    }

    private List<TupleList> expandTupleLists(List<TupleList> tupleLists) {
        return tupleLists
                .stream()
                .flatMap(tupleList -> IntStream
                        .range(0, tupleList.getTuples().size())
                        .mapToObj(i -> new TupleList(
                                computeExpandedId(tupleList, i),
                                tupleList.getInvolvedParameters(),
                                Collections.singletonList(tupleList.getTuples().get(i)),
                                tupleList.isMarkedAsCorrect())))
                .collect(Collectors.toList());
    }

    private int computeExpandedId(TupleList tupleList, int tupleIndex) {
        return tupleList.getId() * factor + tupleIndex;
    }

    private int computeFactor(CompleteTestModel testModel) {
        final int tupleLists = testModel.getExclusionTupleLists().size() + testModel.getErrorTupleLists().size();
        final int maxTuples = Math.max(
                testModel.getExclusionTupleLists().stream().mapToInt(tupleList -> tupleList.getTuples().size()).max().orElse(0),
                testModel.getErrorTupleLists().stream().mapToInt(tupleList -> tupleList.getTuples().size()).max().orElse(0)
        );

        return computeFactor(tupleLists, maxTuples);
    }

    private int computeFactor(int tupleLists, int maxTuples) {
        int power = 1;

        if(tupleLists > 0) {
            while(tupleLists / ((int) Math.pow(10, power)) != 0) {
                power += 1;
            }
        }

        if(maxTuples > 0) {
            while(maxTuples / ((int) Math.pow(10, power)) != 0) {
                power += 1;
            }
        }

        return (int) Math.pow(10, power);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestModelExpander expander = (TestModelExpander) o;
        return factor == expander.factor &&
                testModel.equals(expander.testModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factor, testModel);
    }

    @Override
    public String toString() {
        return "TestModelExpander{" +
                "factor=" + factor +
                ", testModel=" + testModel +
                '}';
    }
}
