package de.rwth.swc.coffee4j.algorithmic.interleaving.util;

import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;

/**
 * Helper class building combinations used, for example, by the {@link de.rwth.swc.coffee4j.algorithmic.constraint.ForbiddenTuplesChecker}
 * to derive new tuples.
 */
public final class TupleBuilderUtil {
    private TupleBuilderUtil() {
        // empty constructor
    }

    /**
     * builds the cartesian product for a given set of combinations,
     *
     * @param tuplesSet set containing combinations to build the cartesian product for.
     * @param numberOfParameters size of combinations to be created.
     *
     * @return returns the cartesian product for the combinations in tupleSet.
     */
    public static Collection<IntList> buildCartesianProduct(Set<Collection<IntList>> tuplesSet, int numberOfParameters) {
        List<Collection<IntList>> tupleList = new ArrayList<>(tuplesSet);

        while (tupleList.size() > 2) {
            Collection<IntList> tuple1 = tupleList.remove(0);
            Collection<IntList> tuple2 = tupleList.remove(0);

            Set<IntList> productSet = buildProduct(tuple1, tuple2);

            if (!productSet.isEmpty()) {
                tupleList.add(productSet);
            } else {
                return Collections.emptySet();
            }
        }

        if (tupleList.size() == 2) {
            Collection<IntList> tuple1 = tupleList.remove(0);
            Collection<IntList> tuple2 = tupleList.remove(0);

            return buildProduct(tuple1, tuple2);
        } else if (tupleList.size() == 1) {
            Collection<IntList> forbiddenTuples = tupleList.remove(0);

            if (forbiddenTuples.size() == 1 && forbiddenTuples.contains(new IntArrayList(CombinationUtil.emptyCombination(numberOfParameters)))) {
                return Collections.emptySet();
            }
            return forbiddenTuples;
        } else {
            return Collections.emptySet();
        }
    }

    private static Set<IntList> buildProduct(Collection<IntList> set1, Collection<IntList> set2) {
        Set<IntList> productSet = new HashSet<>();

        for (IntList tuple1 : set1) {
            for (IntList tuple2 : set2) {
                IntList tupleProduct = combineTuples(tuple1, tuple2);

                if (tupleProduct != null) {
                    productSet.add(tupleProduct);
                }
            }
        }

        return productSet;
    }

    private static IntList combineTuples(IntList tuple1, IntList tuple2) {
        IntList tupleProduct = new IntArrayList(tuple1);

        for (int i = 0; i < tuple1.size(); i++) {
            if (tuple1.getInt(i) == -1 || tuple1.getInt(i) == tuple2.getInt(i)) {
                tupleProduct.set(i, tuple2.getInt(i));
            } else if (tuple2.getInt(i) != -1) {
                return null;
            }
        }

        if (isEmptyCombination(tupleProduct)) {
            return null;
        }

        return tupleProduct;
    }

    private static boolean isEmptyCombination(IntList tupleProduct) {
        for (int param = 0; param < tupleProduct.size(); param++) {
            if (tupleProduct.getInt(param) != -1) {
                return false;
            }
        }
        return true;
    }
}
