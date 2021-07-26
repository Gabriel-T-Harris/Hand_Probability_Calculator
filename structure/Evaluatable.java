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
}
