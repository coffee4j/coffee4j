package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.OptimalValue;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

/**
 * Identification Strategy using Tuple-Relationship-Trees proposed by Nie et al. in "Identifying Failure-Inducing
 * Combinations Using Tuple Relationship".
 *
 * <p>
 *    This strategy implements the augmented approach without the TRT assumption. This means that there is no
 *    Safe Value Assumption. MAXIMUM_NUMBER_OF_ITERATIONS extra test inputs are generated to ensure that a selected
 *    tuple is faulty and that the failing of the test case is not caused by a new failure. The higher this value,
 *    the higher the probability that the selected tuple is really faulty.
 * </p>
 */
public class TupleRelationshipStrategy implements IdentificationStrategy {
    
    final CoverageMap coverageMap;
    final ConstraintChecker checker;
    final CompleteTestModel testModel;

    // root node of tuple relationship tree for currently processed failing or exceptional-passing test input
    TupleNode root = null;

    // stores all test input that passed in the normal control-flow
    private final Set<IntList> passingTestInputs = new HashSet<>();
    protected int[] currentlyProcessedTestInput;
    private TestResult resultOfCurrentlyProcessedTestInput;

    private TupleNode templateTRT;
    private final List<Set<TupleNode>> templateNodes = new ArrayList<>();
    private final ExecutorService trtBuildExecutorService = Executors.newCachedThreadPool();

    TupleNode currentlySelectedNode;
    List<TupleNode> currentlySelectedLongestPath;
    int head = 0;
    int middle = 0;
    int tail = 0;
    final int numberOfParameters;
    // List of all parameters
    final IntList parameters;

    private List<TupleNode> tempPath;

    // used for the augmented version of the strategy to reduce effect of safe value assumption
    private static final int MAXIMUM_NUMBER_OF_ITERATIONS = 50;
    private int iteration = 1;

    private final List<int[]> alreadyExecutedTests = new ArrayList<>();

    final Map<IntList, CombinationType> possiblyInducingCombinations = new HashMap<>();

    List<Throwable> exceptionsTriggeredByCurrentlySelectedNode;

    private boolean maximumPathFound;
    private static final int MAXIMUM_NUMBER_OF_PARAMETERS_FOR_FULL_TREE = 14;
    private boolean firstRound = true;

    TupleRelationshipStrategy(IdentificationConfiguration configuration) {
        this.coverageMap = configuration.getCoverageMap();
        this.checker = configuration.getConstraintChecker();
        this.testModel = configuration.getTestModel();

        numberOfParameters = testModel.getNumberOfParameters();

        parameters = new IntArrayList(numberOfParameters);
        IntStream.range(0, numberOfParameters).forEach(parameters::add);

        buildTemplateTree();
    }

    private void buildTemplateTree() {
        int size = numberOfParameters;

        // check whether to use reduced tree or not
        if (numberOfParameters > MAXIMUM_NUMBER_OF_PARAMETERS_FOR_FULL_TREE) {
            for (int i = 1; i <= MAXIMUM_NUMBER_OF_PARAMETERS_FOR_FULL_TREE; i++) {
                if (binomialCoefficient(numberOfParameters, i) > 5000) {
                    size = i - 1;
                    break;
                }
            }
        }

        int finalSize = size;

        // start creation of tree in a separate thread
        Runnable task = () ->
                templateTRT = TreeBuilder.createTree(finalSize, numberOfParameters, templateNodes);

        trtBuildExecutorService.execute(task);
        trtBuildExecutorService.shutdown();
    }

    /**
     * @return factory creating Tuple-Relationship-Strategy
     */
    public static IdentificationStrategyFactory tupleRelationshipStrategy() {
        return TupleRelationshipStrategy::new;
    }

    @Override
    public Optional<int[]> startIdentification(int[] testInput, TestResult result) {
        if (!result.getResultValue().isPresent()) {
            throw new Coffee4JException("Cause of Failure must be present!");
        }

        // reset
        currentlyProcessedTestInput = testInput;
        resultOfCurrentlyProcessedTestInput = result;

        possiblyInducingCombinations.clear();
        currentlySelectedNode = null;
        passingTestInputs.addAll(coverageMap.getPassingTestInputs());

        // build trt of currently processed failing test input
        root = buildTupleRelationshipTree(result);

        alreadyExecutedTests.clear();
        alreadyExecutedTests.add(currentlyProcessedTestInput);

        chooseTupleFromCurrentTRT();

        // root node is minimal faulty tuple
        if (currentlySelectedNode == null) {
            return Optional.empty();
        }

        return generateNextTestInputContainingTuple(currentlySelectedNode.getCombination(currentlyProcessedTestInput), alreadyExecutedTests);
    }

    /**
     * @param testInput (partial) test input the new test input must contain ({@link #currentlySelectedNode}
     * @param alreadyExecutedTests contains at least the {@link #currentlyProcessedTestInput}.
     *                             If {@link #currentlySelectedNode} is checked  and the number of checks is less to
     *                             {@link #MAXIMUM_NUMBER_OF_ITERATIONS}, it also contains the previously generated
     *                             and executed test inputs
     *
     * @return next test input. Empty Optional, if there is no valid test input.
     */
    private Optional<int[]> generateNextTestInputContainingTuple(int[] testInput, List<int[]> alreadyExecutedTests) {
        int[] nextTestInput;
        boolean validTestInputFound;

        // try 20 times to find a valid test input
        for (int numberOfTries = 0; numberOfTries < 20; numberOfTries++) {
            Collections.shuffle(parameters);
            nextTestInput = Arrays.copyOf(testInput, testInput.length);

            validTestInputFound = true;

            // iterate over all parameters: select value most dissimilar for all unset parameters
            for (int parameter : parameters) {
                if (nextTestInput[parameter] == -1) {
                    // search for a value for the given parameter and partial test input
                    // most dissimilar of already executed test inputs
                    Optional<ParameterValuePair> optimalValue = OptimalValue.mostDissimilarForParameter(
                            parameter,
                            testModel.getParameterSize(parameter),
                            nextTestInput,
                            alreadyExecutedTests,
                            checker);

                    // no valid value for given parameter and partial test input available
                    if (!optimalValue.isPresent()) {
                        validTestInputFound = false;
                        alreadyExecutedTests.add(nextTestInput);
                        break;
                    }

                    nextTestInput[optimalValue.get().getParameter()] = optimalValue.get().getValue();
                }
            }

            if (validTestInputFound) {
                return Optional.of(nextTestInput);
            }
        }

        return Optional.empty();
    }

    /**
     * selects the next tuple from the longest unknown path to cover as most unknown tuples as possible using binary
     * search.
     * If no path is available, the current TRT is processed and possibly failing- or exception-inducing combinations
     * can be extracted.
     */
    void chooseTupleFromCurrentTRT() {
        // first iteration in identification or longest path processed
        if (currentlySelectedNode == null || tail < head || (currentlySelectedLongestPath.size() == 1 && !currentlySelectedLongestPath.get(0).isUnknown())) {
            currentlySelectedLongestPath = getLongestPath();

            head = middle = 0;
            tail = currentlySelectedLongestPath.size() - 1;
        }

        if (currentlySelectedNode != null) {
            if (currentlySelectedNode.isHealthy()) {
                tail = middle - 1;
            } else if (currentlySelectedNode.isFaulty() || currentlySelectedNode.isExceptionInducingCombination()) {
                head = middle + 1;
            }

            middle = (head + tail) / 2;

        }

        if (!currentlySelectedLongestPath.isEmpty()) {
            currentlySelectedNode = currentlySelectedLongestPath.get(middle);
            exceptionsTriggeredByCurrentlySelectedNode = new ArrayList<>();
        } else {
            // no unknown path available -> all tuples are failing or passing
            // -> possibly failure- or exception-inducing combinations can be returned
            currentlySelectedNode = null;
        }
    }

    /**
     * @return longest unknown path from currently processed tuple relationship tree ({@link #root}).
     * If no path is available, the TRT is processed and an empty path is returned.
     */
    List<TupleNode> getLongestPath() {
        tempPath = new ArrayList<>();
        possiblyInducingCombinations.clear();

        maximumPathFound = false;

        try {
            computeUnknownPaths();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Coffee4JException(e, "Took too long to compute unknown paths!");
        }
        return tempPath;
    }

    /**
     * searches for unknown paths. For every child-node of the {@link #root} of the current TRT, a new thread is started
     * searching for the longest unknown path starting from this child. When all threads are finished, the longest path
     * from all paths returned by the threads is used.
     *
     * This function also collects all possibly minimal failure- or exception-inducing combinations
     * (failing / exceptional-passing tuples whose children are all passing). If no unknown paths are found,
     * the possibly failure-/exception-inducing combinations can be directly returned.
     * If this is the case, the new longest unknown path is set to an empty path.
     *
     * @throws InterruptedException thrown if a thread takes too long to finish searching for an unknown path.
     */
    void computeUnknownPaths() throws InterruptedException {
        // collects all possible fics that are encountered during search
        Set<TupleNode> collectedInducingCombinations = new HashSet<>();
        // stores the longest path for each thread (unknown path starting from a child of the root node)
        Set<List<TupleNode>> longestPaths = new HashSet<>();
        ExecutorService executorService = Executors.newCachedThreadPool();

        if (root.isMinimalInducingTuple()) {
            collectedInducingCombinations.add(root);
        }

        for (TupleNode child : root.getChildren()) {
            List<TupleNode> path = new ArrayList<>();

            if (child.isUnknown()) {
                path.add(child);
            } else if ((child.isFaulty() || child.isExceptionInducingCombination()) && child.isMinimalInducingTuple()) {
                // child is possibly minimal failure- or exception-inducing
                collectedInducingCombinations.add(child);
            }

            if (child.hasChildren()) {
                Runnable task = () -> {
                    List<TupleNode> longestPath = new ArrayList<>();
                    computePathsRecursively(path, child, longestPath, collectedInducingCombinations);
                    // add longest path starting from child or one of its children
                    longestPaths.add(longestPath);
                };

                executorService.execute(task);
            } else if (!path.isEmpty()) {
                longestPaths.add(path);
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        while(!executorService.isTerminated());

        if (!longestPaths.isEmpty()) {
            tempPath = longestPaths.stream().max(Comparator.comparing(List::size)).orElse(new ArrayList<>());
        } else {
            tempPath = new ArrayList<>();
        }

        if (tempPath.isEmpty() && !collectedInducingCombinations.isEmpty()) {
            collectedInducingCombinations.forEach(fic -> possiblyInducingCombinations.put(new IntArrayList(fic.getCombination(currentlyProcessedTestInput)), fic.isExceptionInducingCombination() ? CombinationType.EXCEPTION_INDUCING : CombinationType.FAILURE_INDUCING));
        }
    }

    private void computePathsRecursively(List<TupleNode> list, TupleNode node, List<TupleNode> longestPath, Set<TupleNode> collectedInducingCombinations) {
        if (maximumPathFound) {
            return;
        }
        // no children -> check whether path is a better solution or not
        if (!node.hasChildren()) {
            if (list.size() > longestPath.size()) {
                longestPath.clear();
                longestPath.addAll(list);

                if (list.size() == templateNodes.size() - 1) {
                    maximumPathFound = true;
                    return;
                }
            }

            if (node.isMinimalInducingTuple()) {
                collectedInducingCombinations.add(node);
            }
        } else {
            for (TupleNode child : node.getChildren()) {
                if (maximumPathFound) {
                    return;
                }

                // child unknown -> add to current path
                if (child.isUnknown()) {
                    List<TupleNode> path = new ArrayList<>(list);
                    path.add(child);
                    computePathsRecursively(path, child, longestPath, collectedInducingCombinations);
                } else {
                    // child known -> path ends -> check whether path is a better solution or not
                    if (!list.isEmpty() && list.size() > longestPath.size()) {
                        longestPath.clear();
                        longestPath.addAll(list);

                        if (list.size() == templateNodes.size() - 1) {
                            maximumPathFound = true;
                            return;
                        }
                    }

                    // if it is failure- or exception-inducing, there may be unknown paths starting from one of its child nodes
                    if (child.isFaulty() || child.isExceptionInducingCombination()) {
                        if (child.isMinimalInducingTuple()) {
                            collectedInducingCombinations.add(child);
                        } else {
                            computePathsRecursively(new ArrayList<>(), child, longestPath, collectedInducingCombinations);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param result test result of the root node (processed test input)
     *
     * @return tuple relationship tree with processed test input (containing failure- or exception-inducing combinations)
     * as root node
     */
    TupleNode buildTupleRelationshipTree(TestResult result) {
        while (!trtBuildExecutorService.isTerminated());

        if (firstRound) {
            firstRound = false;
            ExecutorService es = Executors.newCachedThreadPool();

            for (int i = 0; i < templateNodes.size() - 1; i++) {
                int finalI = i;
                Runnable collectChildNodes = () ->
                        templateNodes.get(finalI).forEach(TupleNode::getChildren);

                es.execute(collectChildNodes);
            }

            for (int i = templateNodes.size() - 1; i > 0; i--) {
                int finalI = i;
                Runnable collectParentNodes = () ->
                        templateNodes.get(finalI).forEach(TupleNode::getParents);

                es.execute(collectParentNodes);
            }

            es.shutdown();
        }

        TupleNode trt = new TupleNode(templateTRT);

        updateKnownPassingTuples(templateNodes);

        Optional<Throwable> optCause = result.getResultValue();
        if (!optCause.isPresent()) {
            throw new Coffee4JException("Cause for TestResult must not be empty!");
        } else if (optCause.get() instanceof ErrorConstraintException) {
            trt.setAsExceptionInducingCombination();
        } else {
            trt.setFaulty();
        }

        return trt;
    }

    void updateKnownPassingTuples(List<Set<TupleNode>> nodes) {
        // iterate over all tuples of the generated trt and set tuple as healthy if it is contained in a passing test
        // input
        for (Set<TupleNode> nodeSet : nodes) {
            for (TupleNode node : nodeSet) {
                node.setAsUnknown();

                for (IntList passingTestInput : passingTestInputs) {
                    if (CombinationUtil.contains(passingTestInput.toIntArray(), node.getCombination(currentlyProcessedTestInput))) {
                        node.setHealthy();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Optional<int[]> restartIdentification() {
        return startIdentification(currentlyProcessedTestInput, resultOfCurrentlyProcessedTestInput);
    }

    @Override
    public Optional<int[]> generateNextTestInputForIdentification(int[] testInput, TestResult testResult) {
        // currently selected node and all child nodes are healthy
        if (testResult.isSuccessful()) {
            passingTestInputs.add(new IntArrayList(testInput));

            currentlySelectedNode.setHealthy();
            if (currentlySelectedNode.hasChildren())
                currentlySelectedNode.getChildren().forEach(this::updateChildren);

            iteration = 1;
            alreadyExecutedTests.clear();
            alreadyExecutedTests.add(currentlyProcessedTestInput);
            // currently selected node and all parent nodes are faulty
        } else {
            // further test cases need to classify currently processed tuple
            if (iteration < MAXIMUM_NUMBER_OF_ITERATIONS) {
                iteration++;
                alreadyExecutedTests.add(testInput);
                exceptionsTriggeredByCurrentlySelectedNode.add(testResult.getResultValue().orElseGet(ErrorConstraintException::new));
                return generateNextTestInputContainingTuple(currentlySelectedNode.getCombination(currentlyProcessedTestInput), alreadyExecutedTests);
            // currently processed tuple is most likely failure- ore exception-inducing
            } else {
                long errorExceptions = exceptionsTriggeredByCurrentlySelectedNode.stream().filter(exception -> exception instanceof ErrorConstraintException).count();
                long failures = exceptionsTriggeredByCurrentlySelectedNode.size() - errorExceptions;
                TupleStatus status;

                if (errorExceptions > failures) {
                    currentlySelectedNode.setAsExceptionInducingCombination();
                    status = TupleStatus.EXCEPTIONAL_COMBINATION;
                } else {
                    currentlySelectedNode.setFaulty();
                    status = TupleStatus.FAULTY;
                }

                if (currentlySelectedNode.hasParents())
                    currentlySelectedNode.getParents().forEach(parent -> updateParents(parent, status));

                iteration = 1;
                alreadyExecutedTests.clear();
                alreadyExecutedTests.add(currentlyProcessedTestInput);
            }
        }
        // there are unknown tuples in tree -> generate next test input
        chooseTupleFromCurrentTRT();

        if (currentlySelectedNode == null)
            return Optional.empty();

        return generateNextTestInputContainingTuple(currentlySelectedNode.getCombination(currentlyProcessedTestInput), alreadyExecutedTests);

    }

    /**
     * @param node node and all its children are set to healthy
     */
    private void updateChildren(TupleNode node) {
        if (node.isHealthy()) {
            return;
        }

        node.setHealthy();
        if (node.hasChildren())
            node.getChildren().forEach(this::updateChildren);
    }

    /**
     * @param node node and all its parent nodes are set to faulty
     */
    private void updateParents(TupleNode node, TupleStatus status) {
        if(node.getStatus() == status) {
            return;
        }

        if (status == TupleStatus.EXCEPTIONAL_COMBINATION) {
            node.setAsExceptionInducingCombination();
        } else if (status == TupleStatus.FAULTY) {
            node.setFaulty();
        } else {
            throw new Coffee4JException("status must be FAULTY or EXCEPTIONAL_COMBINATION!");
        }

        if (node.hasParents())
            node.getParents().forEach(parent -> updateParents(parent, status));
    }

    @Override
    public Map<IntList, CombinationType> getIdentifiedCombinations() {
        return possiblyInducingCombinations;
    }

    @Override
    public String toString() {
        return "TupleRelationshipStrategy";
    }
}
