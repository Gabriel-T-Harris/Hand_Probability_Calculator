package structure;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn
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
     * @param NAME {@link #NAME}
     */
    public Evaluable(final String NAME)
    {
        this.NAME = NAME;
        this.UNIQUE_IDENTIFIER = ++CREATED_NODES_COUNT;
    }

    /**
     * Output whole tree in dot file format.
     * 
     * @param START of breath first search
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

        OUTPUT.append('}');

        return OUTPUT.toString();
    }

    /**
     * Allows for hand to be in an arbitrary order  
     *
     * Default entry point where the success callback returns true and the failure callback returns false
     * @param <E> anything that is {@link Reservable} will do
     *
     * @param hand to be checked {@link Collection}
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
     * If debugMode is set, print current debug details about the currently executing node
     *
     * @param hand to be checked {@link Collection}
     */
    <E extends Reservable> void printDebugStep(final Collection<E> hand)
    {
        if (debugMode) {
            System.out.printf("%s ", this);
            Map<Boolean, List<E>> hand_partition = hand.stream().collect(Collectors.partitioningBy(Reservable::isReserved));
            System.out.printf("Used Cards: [%s] ", hand_partition.get(true).stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.printf("Unused Cards: [%s]\n", hand_partition.get(false).stream().map(Object::toString).collect(Collectors.joining(",")));
        }
    }
}
