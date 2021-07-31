package structure;

import java.util.Collection;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
Date: 2021-07-24
</b>
*/

public interface Evaluable<T>
{
    interface RollbackCallback {
        boolean call();
    }

    /**
     * Function used to deprecated_evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * @param hand to be checked {@link Collection}
     * @param next function to call when a leaf node takes a card from the hand
     * @param fallback function to call when there is no card that a leaf node can take
     * @return If the hand meets a condition
     */
    boolean evaluate(final Collection<T> hand, final RollbackCallback next, final RollbackCallback fallback);

    /**
     * Function used to deprecated_evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * Default entry point where the success callback returns true and the failure callback returns false
     *
     * @param hand to be checked {@link Collection}
     * @return If the hand meets a condition
     */
    default boolean evaluate(final Collection<T> hand)
    {
        return evaluate(hand, () -> true, () -> false);
    }
}
