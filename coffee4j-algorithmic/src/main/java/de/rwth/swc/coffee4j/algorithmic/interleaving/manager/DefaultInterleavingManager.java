package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Default Implementation of the interface {@link InterleavingCombinatorialTestManager}.
 */
public class DefaultInterleavingManager extends AbstractInterleavingManager {
    /**
     * @param configuration {@link InterleavingCombinatorialTestConfiguration} used for initialization of strategies etc.
     * @param testModel model to process.
     */
    DefaultInterleavingManager(InterleavingCombinatorialTestConfiguration configuration, CompleteTestModel testModel) {
        super(configuration, testModel);
    }

    @Override
    protected void terminateInterleavingGroup() {
        reporter.interleavingGroupFinished(testGroup, new HashMap<>(), failureInducingCombinations);
    }

    @Override
    protected void terminateIdentification() {
        failureInducingCombinationsToCheck.addAll(identificationStrategy
                .getIdentifiedCombinations()
                .keySet()
                .stream()
                .map(IntList::toIntArray)
                .collect(Collectors.toSet()));

        reporter.identificationFinished(testGroup, new HashSet<>(), failureInducingCombinationsToCheck);
    }

    @Override
    protected boolean noCombinationsToBeCheckedPresent() {
        return failureInducingCombinationsToCheck.isEmpty();
    }

    @Override
    protected void resetCombinationsToBeChecked() {
        failureInducingCombinationsToCheck = new HashSet<>();
    }

    @Override
    protected void determineCombinationsToBeChecked() {
        combinationsToCheck.addAll(failureInducingCombinationsToCheck);
    }

    @Override
    protected void updateCoverage() {
        updateCoverageAfterFailureInducingCombinationIsIdentified(failureInducingCombinationsToCheck);
        failureInducingCombinations.addAll(failureInducingCombinationsToCheck);
    }

    /**
     * @return returns an {@link ExecutingInterleavingManagerFactory} for creating this manager.
     */
    public static ExecutingInterleavingManagerFactory managerFactory() {
        return DefaultInterleavingManager::new;
    }
}