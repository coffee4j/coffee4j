package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.BitSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Class representing Nodes used by {@link TupleRelationshipStrategy} to identify minimal failure or exception-inducing
 * combinations.
 *
 * <ul>
 *     <li>{@link #tuple} stores a {@link BitSet} representing a combination of parameters and values</li>
 *     <li>{@link #status} stores whether the node is unknown, healthy, faulty or exceptional</li>
 * </ul>
 */
public class TupleNode {
    
    private final BitSet tuple;
    private TupleStatus status;
    private Set<TupleNode> childNodes;
    private Set<TupleNode> possibleChildNodes;
    private Set<TupleNode> parentNodes;
    private Set<TupleNode> possibleParentNodes;

    TupleNode(BitSet tuple) {
        this.tuple = tuple;
        this.status = TupleStatus.UNKNOWN;
        this.childNodes = null;
        this.possibleChildNodes = null;
        this.parentNodes = null;
        this.possibleParentNodes = null;
    }

    TupleNode(TupleNode node) {
        this.tuple = (BitSet) node.tuple.clone();
        this.status = node.status;
        this.childNodes = node.childNodes;
        this.possibleChildNodes = node.possibleChildNodes;
        this.parentNodes = node.parentNodes;
        this.possibleParentNodes = node.possibleParentNodes;
    }

    BitSet getTuple() {
        return tuple;
    }

    boolean hasChildren() {
        return possibleChildNodes != null;
    }

    boolean hasParents() {
        return possibleParentNodes != null;
    }

    void setPossibleChildNodes(Set<TupleNode> possibleChildNodes) {
        this.possibleChildNodes = possibleChildNodes;
    }

    void setPossibleParentNodes(Set<TupleNode> possibleParentNodes) {
        this.possibleParentNodes = possibleParentNodes;
    }

    Set<TupleNode> getChildren() {
        if (possibleChildNodes != null) {
            if (childNodes == null) {
                childNodes = possibleChildNodes.stream().filter(this::isParentOf).collect(Collectors.toSet());
            }
            return childNodes;
        } else {
            return Collections.emptySet();
        }
    }

    Set<TupleNode> getParents() {
        if (possibleParentNodes != null) {
            if (parentNodes == null) {
                parentNodes = possibleParentNodes.stream().filter(this::isChildOf).collect(Collectors.toSet());
            }
            return parentNodes;
        } else {
            return Collections.emptySet();
        }
    }

    void setHealthy() {
        this.status = TupleStatus.HEALTHY;
    }

    void setFaulty() {
        this.status = TupleStatus.FAULTY;
    }

    void setAsExceptionInducingCombination() { this.status = TupleStatus.EXCEPTIONAL_COMBINATION; }

    void setAsUnknown() { this.status = TupleStatus.UNKNOWN; }

    boolean isUnknown() {
        return this.status == TupleStatus.UNKNOWN;
    }

    boolean isHealthy() {
        return this.status == TupleStatus.HEALTHY;
    }

    boolean isFaulty() {
        return this.status == TupleStatus.FAULTY;
    }

    boolean isExceptionInducingCombination() {
        return this.status == TupleStatus.EXCEPTIONAL_COMBINATION;
    }

    TupleStatus getStatus() {
        return this.status;
    }

    boolean isDirectParentOf(TupleNode node) {
        if (tuple.length() >= node.tuple.length()) {
            if (tuple.cardinality() == node.tuple.cardinality() + 1) {
                for (int index = 0; index < tuple.length(); index++) {
                    if (node.tuple.get(index) && !tuple.get(index)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }

    boolean isParentOf(TupleNode node) {
        if (tuple.length() >= node.tuple.length()) {
            for (int index = 0; index < tuple.length(); index++) {
                if (node.tuple.get(index) && !tuple.get(index)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    boolean isChildOf(TupleNode node) {
        return node.isParentOf(this);
    }

    boolean hasUnknownTuples() throws InterruptedException {
        if (status == TupleStatus.UNKNOWN) {
            return true;
        }

        if (possibleChildNodes == null) {
            return false;
        }

        ExecutorService es = Executors.newCachedThreadPool();
        AtomicBoolean hasUnknownChildren = new AtomicBoolean(false);

        for (TupleNode child : getChildren()) {
            Runnable task = () -> {
                if (child.hasUnknownChildrenThread()) {
                    hasUnknownChildren.set(true);
                }
            };

            es.execute(task);
        }

        es.shutdown();
        es.awaitTermination(2, TimeUnit.MINUTES);

        return false;
    }

    private boolean hasUnknownChildrenThread() {
        if (status == TupleStatus.UNKNOWN) {
            return true;
        }

        if (possibleChildNodes == null) {
            return false;
        }

        for (TupleNode child : getChildren()) {
            if (child.hasUnknownChildrenThread()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true iff this tuple is minimal failure- or exception-inducing, i.e. it has no children or all children
     * are healthy.
     */
    public boolean isMinimalInducingTuple() {
        if (status != TupleStatus.EXCEPTIONAL_COMBINATION && status != TupleStatus.FAULTY) {
            return false;
        }

        if (possibleChildNodes == null) {
            return true;
        }

        for (TupleNode child : getChildren()) {
            if (child.isFaulty() || child.isExceptionInducingCombination() || child.isUnknown()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return number of set parameters
     */
    public int getSize() {
        return tuple.cardinality();
    }

    /**
     * maps the stored {@link BitSet} to the sub-combination it represents.
     * @param failingTestInput test input to compute the sub-combination for.
     *
     * @return the sub-combination of the failingTestInput this tuple represents.
     */
    public int[] getCombination(int[] failingTestInput) {
        Preconditions.check(failingTestInput.length >= tuple.length());

        int[] combination = new int[failingTestInput.length];

        for (int index = 0; index < failingTestInput.length; index++) {
            if (tuple.get(index)) {
                combination[index] = failingTestInput[index];
            } else {
                combination[index] = -1;
            }
        }

        return combination;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tuple, status);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleNode tupleNode = (TupleNode) o;
        return tuple.equals(tupleNode.tuple) &&
                status == tupleNode.status;
    }
    
}
