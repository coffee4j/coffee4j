package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TreeBuilderTest {
    @Test
    void correctForSizeOne() {
        List<Set<TupleNode>> nodes = new ArrayList<>();

        BitSet rootNode = new BitSet(1);
        rootNode.flip(0, 1);
        TupleNode trt = new TupleNode(rootNode);

        TupleNode tree = TreeBuilder.createTree(1, 1, nodes);

        assertEquals(trt, tree);
    }

    @Test
    void testForInvalidSize() {
        List<Set<TupleNode>> nodes = new ArrayList<>();

        BitSet rootNode = new BitSet(1);
        rootNode.flip(0, 1);
        TupleNode trt = new TupleNode(rootNode);

        TupleNode tree = TreeBuilder.createTree(2, 1, nodes);

        assertEquals(trt, tree);
    }

    @Test
    void correctForHigherSize() {
        List<Set<TupleNode>> nodes = new ArrayList<>();

        BitSet rootNode = new BitSet(2);
        rootNode.flip(0, 2);
        TupleNode trt = new TupleNode(rootNode);
        BitSet one = new BitSet(1);
        one.flip(0);

        TupleNode childOne = new TupleNode(one);
        TupleNode childTwo = new TupleNode(new BitSet(1));

        Set<TupleNode> childNodes = new HashSet<>();
        childNodes.add(childOne);
        childOne.setPossibleParentNodes(Collections.singleton(trt));
        childNodes.add(childTwo);
        childTwo.setPossibleParentNodes(Collections.singleton(trt));

        trt.setPossibleChildNodes(childNodes);

        TupleNode tree = TreeBuilder.createTree(2, 2, nodes);

        assertEquals(trt, tree);
    }
}
