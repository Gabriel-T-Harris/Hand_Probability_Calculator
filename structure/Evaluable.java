package structure;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Evaluable
{
    enum TestResult {
        /**
         * The evaluation succeeded
         */
        Success,
        /**
         * The evaluation of the child of NOT succeeded
         */
        NotSuccess,
        /**
         * Evaluation failed given reserved cards
         */
        Rollback
    }

    /**
     * Lambda for {@link Evaluable#evaluate(Collection, RollbackCallback)}.
     */
    interface RollbackCallback {
        TestResult call();
    }

    //TODO: add javadoc
    public final static boolean debugMode = false;

    /**
     * Name of this object.
     */
    public final String NAME;

    /**
     * Unique identifier for this node.
     */
    public final int UNIQUE_IDENTIFIER;

    /**
     * Used to set {@link #UNIQUE_IDENTIFIER}
     */
    private static int CREATED_NODES_COUNT = 0;

    /**
     * Constructor to force unified id among all subclasses.
     *
     * @param NAME {@link #NAME}
     */
    public Evaluable(final String NAME)
    {
        this.NAME = NAME;
        this.UNIQUE_IDENTIFIER = ++Evaluable.CREATED_NODES_COUNT;
    }

    /**
     * Output whole tree in dot file format.
     *
     * @param START of breath first search
     *
     * @return representation of whole structure in dot file format
     */
    public static String print_whole_subtree(final Evaluable START)
    {
        final StringBuilder OUTPUT = new StringBuilder(2048); //large output
        final HashSet<Integer> SEEN_NODES = new HashSet<Integer>(); //prevent nodes from being dealt with multiple times, mainly only affects Scenarios
        final Queue<Evaluable> TRAVERSE_NODES = new ArrayDeque<Evaluable>();

        OUTPUT.append("digraph {\nnode [shape=record];\nnode [fontname=Sans];charset=\"UTF-8\" splines=true splines=spline rankdir =LR\n");

        //children
        for (Evaluable placeholder = START; placeholder != null; placeholder = TRAVERSE_NODES.poll())
        {
            if (!SEEN_NODES.contains(placeholder.UNIQUE_IDENTIFIER))
            {
                SEEN_NODES.add(placeholder.UNIQUE_IDENTIFIER);
                OUTPUT.append(placeholder); // print out top node

                Collection<? extends Evaluable> children = placeholder.continue_breath_search();
                if (children != null)
                    TRAVERSE_NODES.addAll(children); // add children
            }
        }

        return OUTPUT.append('}').toString();
    }

    /**
     * Allows for hand to be in an arbitrary order. Default entry point where the success callback returns true and the failure callback returns false
     *
     * @param <E> anything that is {@link Reservable} will do
     *
     * @param hand to be checked {@link Collection}
     *
     * @return If the hand meets a condition
     */
    public <E extends Reservable> boolean evaluate(final Collection<E> hand)
    {
        TestResult result = evaluate(hand, () -> TestResult.Success);
        return result == TestResult.Success;
    }

    /**
     * Function used to evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * @param HAND to be checked {@link Collection}
     * @param NEXT function to call when a leaf node takes a card from the hand
     * 
     * @return a {@link TestResult} used as a signal on what action to preform next
     */
    protected <E extends Reservable> TestResult evaluate(final Collection<E> HAND, final RollbackCallback NEXT)
    {
        throw new UnsupportedOperationException("Child failed to override me.");
    }

    /**
     * Expected to be defined to pass along children for {@link #print_whole_subtree}.
     * 
     * @return null (for skip this one) or children
     */
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        throw new UnsupportedOperationException("Child failed to override me.");
    }


    /**
     * @return a {@link List} of two {@link StringJoiner}s with ", " delimiter and [ ] prefix and suffix
     */
    private static List<StringJoiner> StringJoinerListGenerator() {
        return Arrays.asList(
                new StringJoiner(", ", "[", "]"),
                new StringJoiner(", ", "[", "]")
        );
    }

    /**
     * Takes a {@link Reservable} and adds it to the first {@link StringJoiner} if its reserved and the second
     * otherwise
     *
     * @param partialSum the current state of the two StringJoiners
     * @param nextElement the next Reservable to add to the StringJoiner
     */
    private static void StringJoinerListPartialAdder(List<StringJoiner> partialSum, Reservable nextElement) {
        partialSum.get(nextElement.isReserved() ? 0 : 1).add(nextElement.toString());
    }

    /**
     * Takes two {@link List}s of {@link StringJoiner}s and merges them together index wise
     *
     * @param partialSum1 List of StringJoiner to be merged to
     * @param partialSum2 List of StringJoiner to be merged with
     * @return partialSum1 after having their elements be merged with partialSum2's
     */
    private static List<StringJoiner> StringJoinerListJoiner(List<StringJoiner> partialSum1, List<StringJoiner> partialSum2) {
        partialSum1.get(0).merge(partialSum2.get(0));
        partialSum1.get(1).merge(partialSum2.get(1));
        return partialSum1;
    }

    /**
     * Takes a {@link List} of {@link StringJoiner}s returns a {@link List} of their final {@link String}
     *
     * @param finalSum StringJoiner after all of the elements have been added
     * @return The {@link String} representing the joined elements
     */
    private static List<String> StringJoinerToStringList(List<StringJoiner> finalSum) {
        return finalSum.stream().map(StringJoiner::toString).collect(Collectors.toList());
    }

    /**
     * If debugMode is set, print current debug details about the currently executing node
     *
     * @param hand to be checked {@link Collection}
     */
    <E extends Reservable> void printDebugStep(final Collection<E> hand)
    {
        if (debugMode) {
            System.out.print(this + " ");
            List<String> usedAndUnusedCards = hand.stream().collect(Collector.of(
                    Evaluable::StringJoinerListGenerator,
                    Evaluable::StringJoinerListPartialAdder,
                    Evaluable::StringJoinerListJoiner,
                    Evaluable::StringJoinerToStringList
            ));
            System.out.printf("Used Cards: %s ", usedAndUnusedCards.get(0));
            System.out.printf("Unused Cards: %s\n", usedAndUnusedCards.get(1));
        }
    }
}
