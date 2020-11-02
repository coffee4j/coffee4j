package de.rwth.swc.coffee4j.algorithmic.util;

import de.rwth.swc.coffee4j.algorithmic.interleaving.util.TupleBuilderUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TupleBuilderTest {
    @Test
    void testBuildProcess() {
        IntList tuple1 = new IntArrayList(new int[]{1,-1,-1});
        IntList tuple2 = new IntArrayList(new int[]{-1,1,-1});
        IntList tuple3 = new IntArrayList(new int[]{-1,-1,1});

        List<IntList> product = new ArrayList<>(TupleBuilderUtil.buildCartesianProduct(Set.of(
                Collections.singleton(tuple1),
                Collections.singleton(tuple2),
                Collections.singleton(tuple3)), 3));

        assertEquals(1, product.size());
        assertEquals(new IntArrayList(new int[]{1,1,1}), product.get(0) );
    }
}
