package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.HashSet;
import java.util.Map;

/**
 * Default implementation of the interface {@link GeneratingInterleavingCombinatorialTestManager}.
 */
public class DefaultGeneratingInterleavingManager extends AbstractGeneratingInterleavingManager {
    /**
     * @param configuration {@link InterleavingCombinatorialTestConfiguration} used for initialization of strategies etc.
     * @param testModel model to process.
     */
    DefaultGeneratingInterleavingManager(InterleavingCombinatorialTestConfiguration configuration, CompleteTestModel testModel) {
        super(configuration, testModel);
    }

    @Override
    protected void terminateInterleavingGroup() {
        reporter.interleavingGroupFinished(testGroup, minimalExceptionInducingCombinations, failureInducingCombinations);
    }

    @Override
    protected void terminateIdentification() {
        for (Map.Entry<IntList, CombinationType> combination : identificationStrategy.getIdentifiedCombinations().entrySet()) {
            if (combination.getValue() == CombinationType.EXCEPTION_INDUCING) {
                minimalExceptionInducingCombinationsToCheck.add(combination.getKey().toIntArray());
            } else {
                failureInducingCombinationsToCheck.add(combination.getKey().toIntArray());
            }
        }

        reporter.identificationFinished(testGroup, minimalExceptionInducingCombinationsToCheck, failureInducingCombinationsToCheck);
    }

    @Override
    protected void resetCombinationsToBeChecked() {
        minimalExceptionInducingCombinationsToCheck = new HashSet<>();
        failureInducingCombinationsToCheck = new HashSet<>();
    }

    @Override
    protected boolean noCombinationsToBeCheckedPresent() {
         return minimalExceptionInducingCombinationsToCheck.isEmpty() && failureInducingCombinationsToCheck.isEmpty();
    }

    @Override
    protected void determineCombinationsToBeChecked() {
        combinationsToCheck.addAll(minimalExceptionInducingCombinationsToCheck);
        combinationsToCheck.addAll(failureInducingCombinationsToCheck);
    }

    @Override
    protected void updateCoverage() {
        updateCoverageAfterFailureInducingCombinationIsIdentified(minimalExceptionInducingCombinationsToCheck);
        minimalExceptionInducingCombinationsToCheck.forEach(combination -> minimalExceptionInducingCombinations.put(combination, null));

        updateCoverageAfterFailureInducingCombinationIsIdentified(failureInducingCombinationsToCheck);
        failureInducingCombinations.addAll(failureInducingCombinationsToCheck);
    }

    /**
     * @return returns an {@link GeneratingInterleavingManagerFactory} to create this class.
     */
    public static GeneratingInterleavingManagerFactory managerFactory() {
        return DefaultGeneratingInterleavingManager::new;
    }
}
