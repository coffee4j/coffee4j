package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to build a tuple-relationship-tree for {@link TupleRelationshipStrategy}.
 */
public class TreeBuilder {
    private TreeBuilder() {
        // empty constructor
    }

    /**
     * creates a tuple-relationship-tree.
     *
     * @param size maximum size of sub-combinations the resulting tree must contain. As a result, the tree is of size
     *             max(numberOfParameters, size + 1). If size == numberOfParameters, a full tree is returned.
     *             Otherwise, the root contains a tuple of size numberOfParameters and the child nodes are of
     *             size size, i.e. the tree has a height of size + 1.
     * @param numberOfParameters number of parameters in the processed test model.
     * @param nodes list where to store all nodes.
     *
     * @return root node of the built tree.
     */
    public static TupleNode createTree(int size, int numberOfParameters, List<Set<TupleNode>> nodes) {
        if(size > numberOfParameters) {
            size = numberOfParameters;
        }

        BitSet rootNode = new BitSet(numberOfParameters);
        rootNode.flip(0, numberOfParameters);

        TupleNode trt = new TupleNode(rootNode);

        Set<BitSet> leaves = new HashSet<>(numberOfParameters);

        for (int index = 0; index < numberOfParameters; index++) {
            BitSet leaf = new BitSet(numberOfParameters);
            leaf.set(index);
            leaves.add(leaf);
        }

        nodes.add(leaves.stream().map(TupleNode::new).collect(Collectors.toSet()));

        for (int currentSize = 0; currentSize < size - 1 && currentSize < numberOfParameters - 2; currentSize++) {
            Set<TupleNode> nextSet = new HashSet<>();

            for (TupleNode child : nodes.get(currentSize)) {
                for (int index = child.getTuple().length(); index < numberOfParameters; index++) {
                    BitSet parent = (BitSet) child.getTuple().clone();
                    parent.set(index);
                    nextSet.add(new TupleNode(parent));
                }
            }

            nodes.add(nextSet);
        }

        if (numberOfParameters > 1) {
            nodes.add(Collections.singleton(trt));
            Collections.reverse(nodes);
        }

        for (int i = 0; i < nodes.size() - 1; i++) {
            int finalI = i;

            nodes.get(i).forEach(node -> node.setPossibleChildNodes(nodes.get(finalI + 1)));
            nodes.get(finalI + 1).forEach(node -> node.setPossibleParentNodes(nodes.get(finalI)));
        }

        return trt;
    }
}
