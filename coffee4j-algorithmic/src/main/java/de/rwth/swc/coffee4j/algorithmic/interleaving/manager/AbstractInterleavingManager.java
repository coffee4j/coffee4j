package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.algorithmic.interleaving.Phase;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.EmptyInterleavingGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.InterleavingGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;

/**
 * Abstract class implementing {@link InterleavingCombinatorialTestManager}. Adds the capability to store the current
 * {@link Phase} of the Interleaving Combinatorial Test Execution.
 */
abstract class AbstractInterleavingManager implements InterleavingCombinatorialTestManager {
    
    protected final InterleavingGenerationReporter reporter;
    protected final CoverageMap coverageMap;
    protected final ConstraintChecker checker;

    protected final TestInputGenerationStrategy testInputGenerationStrategy;
    protected final IdentificationStrategy identificationStrategy;
    private final FeedbackCheckingStrategy feedbackCheckingStrategy;
    protected final InterleavingCombinatorialTestGroup testGroup;

    private boolean failureInducingCombinationFound;
    protected Set<int[]> failureInducingCombinationsToCheck;
    protected final Set<int[]> failureInducingCombinations = new HashSet<>();
    protected final List<int[]> combinationsToCheck = new ArrayList<>();
    private boolean firstCheckingRound = true;
    private int[] currentlyCheckedPossiblyFailingTuple = null;
    private int[] currentlyCheckedFailingTest;

    // checked combinations that are not failure-inducing together with number of times they were tested
    private Map<IntList, Integer> falseNegatives = new HashMap<>();

    protected Phase currentPhase = Phase.GENERATION;
    
    protected boolean testInputHasFailed = false;

    /**
     * @param configuration {@link InterleavingCombinatorialTestConfiguration} used for initialization of strategies etc.
     * @param testModel model to process.
     */
    AbstractInterleavingManager(InterleavingCombinatorialTestConfiguration configuration, CompleteTestModel testModel) {
        reporter = configuration.getGenerationReporter().orElse(new EmptyInterleavingGenerationReporter());
        checker = configuration.getConstraintCheckerFactory().createConstraintChecker(testModel);

        coverageMap = new CoverageMap(testModel.getParameterSizes(),
                testModel.getPositiveTestingStrength(),
                checker);

        testInputGenerationStrategy = Preconditions.notNull(configuration.getTestInputGenerationStrategyFactory())
                .create(TestInputGenerationConfiguration
                        .configuration()
                        .constraintChecker(checker)
                        .testModel(testModel)
                        .coverageMap(coverageMap)
                        .build());

        identificationStrategy = Preconditions.notNull(configuration.getIdentificationStrategyFactory())
                .create(IdentificationConfiguration
                        .configuration()
                        .constraintChecker(checker)
                        .testModel(testModel)
                        .coverageMap(coverageMap)
                        .build());

        feedbackCheckingStrategy = Preconditions.notNull(configuration.getFeedbackCheckingStrategyFactory())
                .create(FeedbackCheckingConfiguration
                        .configuration()
                        .constraintChecker(checker)
                        .testModel(testModel)
                        .coverageMap(coverageMap)
                        .build());

        testGroup = new InterleavingCombinatorialTestGroup(
                "Interleaving Combinatorial Test Group",
                testInputGenerationStrategy,
                identificationStrategy,
                feedbackCheckingStrategy
        );

        reporter.interleavingGroupGenerated(testGroup);
    }

    @Override
    public Optional<int[]> generateNextTestInput(int[] testInput, TestResult result) {
        switch (currentPhase) {
            case GENERATION:
                return generateNextTestInput();
            case IDENTIFICATION:
                return generateNextTestInputForIdentification(testInput, result);
            case VERIFICATION:
                return generateNextTestInputForFeedbackChecking(testInput, result);
            default:
                throw new Coffee4JException("Unknown Phase!");
        }
    }

    @Override
    public Optional<int[]> initializeIdentification(int[] testInput, TestResult result) {
        currentPhase = Phase.IDENTIFICATION;
        testInputHasFailed = true;

        resetCombinationsToBeChecked();

        currentlyCheckedFailingTest = Arrays.copyOf(testInput, testInput.length);
        failureInducingCombinationFound = false;

        Optional<int[]> nextTestInput = identificationStrategy.startIdentification(testInput, result);

        reporter.identificationStarted(testGroup, testInput);

        return checkTestInputForIdentification(nextTestInput);
    }

    @Override
    public Optional<int[]> reinitializeIdentification() {
        currentPhase = Phase.IDENTIFICATION;

        resetCombinationsToBeChecked();
        Optional<int[]> nextTestInput = identificationStrategy.restartIdentification();

        return checkTestInputForIdentification(nextTestInput);
    }

    @Override
    public Optional<int[]> initializeFeedbackChecking() {
        // checking phase calls this function until null is returned
        // null is returned if list of combinations to be checked is empty
        currentPhase = Phase.VERIFICATION;

        if (firstCheckingRound) {
             if (noCombinationsToBeCheckedPresent()) {
                 currentPhase = Phase.GENERATION;
                 return Optional.empty();
             }

             determineCombinationsToBeChecked();

            firstCheckingRound = false;
            failureInducingCombinationFound = true;
        } else {
            if (combinationsToCheck.isEmpty()) {
                if (failureInducingCombinationFound) {
                    updateCoverage();
                }

                firstCheckingRound = true;
                currentPhase = Phase.GENERATION;
                return Optional.empty();
            }
        }

        currentlyCheckedPossiblyFailingTuple = combinationsToCheck.remove(0);

        Optional<int[]> nextTestInput = feedbackCheckingStrategy.startFeedbackChecking(currentlyCheckedPossiblyFailingTuple,
                currentlyCheckedFailingTest);

        reporter.checkingStarted(testGroup, currentlyCheckedPossiblyFailingTuple);

        return checkTestInputForFeedbackChecking(nextTestInput);
    }

    @Override
    public void updateCoverage(int[] combination) {
        coverageMap.updateCoverage(combination);
    }

    @Override
    public boolean combinationIdentified() {
        return failureInducingCombinationFound;
    }

    /**
     * Method used when a new test input is needed during Generation-Phase.
     *
     * @return next test input. Null, if all valid t-tuples are covered.
     */
    protected Optional<int[]> generateNextTestInput() {
        Optional<int[]> nextTestInput = Optional.empty();

        if (!coverageMap.allCombinationsCovered() && !testInputHasFailed) {
            nextTestInput = testInputGenerationStrategy.generateNextTestInput();
        }

        if (!nextTestInput.isPresent()) {
            terminateInterleavingGroup();
        }

        return nextTestInput;
    }

    /**
     * Method used when a new test input is needed during Identification-Phase.
     *
     * @param testInput previously executed test input.
     * @param result result of previously executed test input.
     *
     * @return next test input. Null, if possibly failure-inducing combination is identified.
     */
    protected Optional<int[]> generateNextTestInputForIdentification(int[] testInput, TestResult result) {
        Optional<int[]> nextTestInput = identificationStrategy.generateNextTestInputForIdentification(testInput, result);
        return checkTestInputForIdentification(nextTestInput);
    }

    private Optional<int[]> checkTestInputForIdentification(Optional<int[]> nextTestInput) {
        // identification finished: no more test inputs needed
        if (!nextTestInput.isPresent()) {
            terminateIdentification();
        } else {
            reporter.identificationTestInputGenerated(testGroup, nextTestInput.get());
        }

        return nextTestInput;
    }

    /**
     * Method used when a new test input is needed during Feedback-Checking-Phase.
     *
     * @param testInput previously executed test input.
     * @param result result of previously executed test input.
     *
     * @return next test input. Null, if maximum number of checks is executed or combination is not failure-inducing.
     */
    protected Optional<int[]> generateNextTestInputForFeedbackChecking(int[] testInput, TestResult result) {
        // combination is not failure-inducing
        if (result.isSuccessful()) {
            // if checked combination is mistakenly considered as failure-inducing, update number of checks
            falseNegatives.put(new IntArrayList(currentlyCheckedPossiblyFailingTuple), falseNegatives.getOrDefault(new IntArrayList(currentlyCheckedPossiblyFailingTuple), 0) + 1);

            // if number of checks of a combination that is not failure-inducing exceeds a certain threshold, add
            // this combination to set of identified failure-inducing combinations to ensure termination
            //
            // empty combination:  threshold = 30
            // other combinations: threshold = 10
            if (falseNegatives.containsKey(new IntArrayList(currentlyCheckedPossiblyFailingTuple)) &&
                    ((!Arrays.equals(currentlyCheckedPossiblyFailingTuple, CombinationUtil.emptyCombination(currentlyCheckedPossiblyFailingTuple.length))
                            && falseNegatives.get(new IntArrayList(currentlyCheckedPossiblyFailingTuple)) > 10)
                            || falseNegatives.get(new IntArrayList(currentlyCheckedPossiblyFailingTuple)) > 30)) {
                reporter.checkingFinished(testGroup, currentlyCheckedPossiblyFailingTuple, true);
                falseNegatives.remove(new IntArrayList(currentlyCheckedPossiblyFailingTuple));
            } else {
                reporter.checkingFinished(testGroup, currentlyCheckedPossiblyFailingTuple, false);
                failureInducingCombinationFound = false;
            }

            currentPhase = Phase.GENERATION;

            return Optional.empty();
        }

        Optional<int[]> nextTestInput = feedbackCheckingStrategy.generateNextTestInputForChecking(testInput, result);

        return checkTestInputForFeedbackChecking(nextTestInput);
    }

    private Optional<int[]> checkTestInputForFeedbackChecking(Optional<int[]> nextTestInput) {
        if (!nextTestInput.isPresent()) {
            falseNegatives = new HashMap<>();
            reporter.checkingFinished(testGroup, currentlyCheckedPossiblyFailingTuple, true);

            currentPhase = Phase.GENERATION;
        }

        return nextTestInput;
    }

    // add failure-inducing combination to forbidden tuples and update coverage afterwards
    // checker must consider implicit tuples, otherwise this must be implemented here
    void updateCoverageAfterFailureInducingCombinationIsIdentified(Set<int[]> possibleMinimalFailureInducingCombinations) {
        possibleMinimalFailureInducingCombinations.forEach(checker::addConstraint);
        coverageMap.updateCoverage();
    }

    /**
     * Clear the set of combinations for all types of combinations that need to be checked.
     * At least {@link #failureInducingCombinationsToCheck} must be reset.
     */
    protected abstract void resetCombinationsToBeChecked();

    /**
     * Call {@link #updateCoverageAfterFailureInducingCombinationIsIdentified(Set)} for each type of combination.
     * At least {@link #failureInducingCombinationsToCheck} must be used to update.
     */
    protected abstract void updateCoverage();

    /**
     * Add all types of combinations to {@link #combinationsToCheck}.
     * At least {@link #failureInducingCombinationsToCheck} must be added.
     */
    protected abstract void determineCombinationsToBeChecked();

    /**
     * @return true iff no combinations to check are available.
     */
    protected abstract boolean noCombinationsToBeCheckedPresent();

    /**
     * Called when all t-tuples are covered. Post-processing possible.
     * At least {@link #reporter}.interleavingGroupFinished must be called.
     */
    protected abstract void terminateInterleavingGroup();

    /**
     * split found combinations into different sets.
     * At least {@link #failureInducingCombinationsToCheck} must be filled and {@link #reporter}.identificationFinished
     * must be called.
     */
    protected abstract void terminateIdentification();
}
