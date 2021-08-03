package structure;

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

public interface Evaluable<T>
{
    boolean debugMode = false;

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

    interface RollbackCallback {
        TestResult call();
    }

    /**
     * Function used to evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * @param hand to be checked {@link Collection}
     * @param next function to call when a leaf node takes a card from the hand
     * @return a {@link TestResult} used as a signal on what action to preform next
     */
    <E extends Reservable> TestResult evaluate(final Collection<E> hand, final RollbackCallback next);

    /**
     * Function used to deprecated_evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * Default entry point where the success callback returns true and the failure callback returns false
     *
     * @param hand to be checked {@link Collection}
     * @return If the hand meets a condition
     */
    default <E extends Reservable> boolean evaluate(final Collection<E> hand)
    {
        TestResult result = evaluate(hand, () -> TestResult.Success);
        return result == TestResult.Success;
    }

    /**
     * If debugMode is set, print current debug details about the currently executing node
     *
     * @param hand to be checked {@link Collection}
     */
    default <E extends Reservable> void printDebugStep(final Collection<E> hand) {
        if (debugMode) {
            System.out.printf("%s ", this);
            Map<Boolean, List<E>> hand_partition = hand.stream().collect(Collectors.partitioningBy(Reservable::isReserved));
            System.out.printf("Used Cards: [%s] ", hand_partition.get(true).stream().map(Object::toString).collect(Collectors.joining(",")));
            System.out.printf("Unused Cards: [%s]\n", hand_partition.get(false).stream().map(Object::toString).collect(Collectors.joining(",")));
        }
    }
}
