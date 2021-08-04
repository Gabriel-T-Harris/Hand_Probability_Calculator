package structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
</b>
*/

public abstract class Evaluable<T>
{
    //TODO: add javadoc
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
    static boolean debugMode = false;

    /**
     * Unique identifier for this node.
     */
    public final int UNIQUE_IDENTIFIER;

    /**
     * Used to set {@link #UNIQUE_IDENTIFIER}
     */
    private static int CREATED_NODES_COUNT = 0;

    public Evaluable()
    {
        this.UNIQUE_IDENTIFIER = ++CREATED_NODES_COUNT;
    }

    /**
     * Function used to evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * @param hand to be checked {@link Collection}
     * @param next function to call when a leaf node takes a card from the hand
     * @return a {@link TestResult} used as a signal on what action to preform next
     */
    abstract <E extends Reservable> TestResult evaluate(final Collection<E> hand, final RollbackCallback next);

    /**
     * Function used to deprecated_evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * Default entry point where the success callback returns true and the failure callback returns false
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
     * Output whole tree in dot file format.
     * 
     * @param START of breath first search
     */
    public static String print_whole_subtree(final Evaluable<?> START)
    {
        StringBuilder output = new StringBuilder();
        Evaluable<?> placeholder;
        Collection<? extends Evaluable<?>> children;
        ArrayList<Evaluable<?>> traverse_nodes = new ArrayList<Evaluable<?>>();
        traverse_nodes.add(START);

        output.append("digraph {\nnode [shape=record];\nnode [fontname=Sans];charset=\"UTF-8\" splines=true splines=spline rankdir =LR\n");

        //children
        do
        {
            placeholder = traverse_nodes.get(0);
            output.append(placeholder.toString()); // print out top node

            children = placeholder.continue_breath_search();
            if (children != null)
                traverse_nodes.addAll(children); // add children

            traverse_nodes.remove(0); // remove first
        } while (!traverse_nodes.isEmpty());

        output.append('}');

        return output.toString();
    }

    /**
     * Expected to be defined to pass along children for {@link #print_whole_subtree}.
     * 
     * @return null or children
     */
    abstract protected Collection<? extends Evaluable<T>> continue_breath_search();

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
