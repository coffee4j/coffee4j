package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

import org.testng.annotations.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TupleNodeTest {
    @Test
    void testSize() {
        BitSet bitset = new BitSet(5);
        bitset.set(1);
        bitset.set(3);

        TupleNode node = new TupleNode(bitset);

        assertEquals(2, node.getSize());
    }

    @Test
    void testParentAndChildren() {
        List<Set<TupleNode>> nodes = new ArrayList<>();
        TupleNode tree = TreeBuilder.createTree(2, 2, nodes);

        assertEquals(nodes.get(1), tree.getChildren());
        nodes.get(1).forEach(node -> assertEquals(Collections.singleton(tree), node.getParents()));
        nodes.get(1).forEach(node -> assertTrue(tree.isDirectParentOf(node)));
        try {
            assertTrue(tree.hasUnknownTuples());
        } catch (InterruptedException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    void testDirectParent() {
        List<Set<TupleNode>> nodes = new ArrayList<>();
        TupleNode tree = TreeBuilder.createTree(3, 3, nodes);

        nodes.get(2).forEach(node -> assertFalse(tree.isDirectParentOf(node)));
    }

    @Test
    void testEquals() {
        List<Set<TupleNode>> nodes = new ArrayList<>();
        TupleNode tree = TreeBuilder.createTree(2, 2, nodes);


        TupleNode copyOfTree = new TupleNode(tree);

        assertTrue(copyOfTree.isUnknown());
        copyOfTree.setFaulty();
        assertTrue(copyOfTree.isFaulty());
        copyOfTree.setAsExceptionInducingCombination();
        assertTrue(copyOfTree.isExceptionInducingCombination());
        copyOfTree.setHealthy();
        assertTrue(copyOfTree.isHealthy());
        assertNotEquals(copyOfTree, tree);
    }
}
