package structure;

import java.util.Collection;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

public interface Evaluatable<T>
{
    interface RollbackCallback {
        boolean call();
    }
    /**
     * Function used to evaluate a node's condition.
     * 
     * @param <E> extends {@link Collection}
     * @param hand to be checked
     * @return {@link #result}
     */
    public abstract <E extends Collection<T>> boolean evaluate(E hand);

    /**
     * Reset internal value for next hand.
     */
    public void reset();

    /**
     * Function used to evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * @param <E> extends {@link Collection}
     * @param hand to be checked
     * @param next function to call when partial match is successful
     * @param fallback function to call when partial match is unsuccessful
     * @return {@link #result}
     */
    boolean rollbackEvaluate(final Collection<T> hand, final RollbackCallback next, final RollbackCallback fallback);

    /**
     * Function used to evaluate a node's condition using a rollback evaluation implementation.
     * Allows for hand to be in an arbitrary order
     *
     * Default entry point where the success callback returns true and the failure callback returns false
     *
     * @param <E> extends {@link Collection}
     * @param hand to be checked
     * @return {@link #result}
     */
    default boolean rollbackEvaluate(final Collection<T> hand)
    {
        return rollbackEvaluate(hand, () -> true, () -> false);
    };
}
