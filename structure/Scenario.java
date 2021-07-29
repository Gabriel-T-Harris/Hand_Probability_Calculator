package structure;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
<b>
Purpose: Contain entire tree structure of {@link Base_Node} in a named reusable manner.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/
//TODO: finish.
public class Scenario<T> implements Evaluatable<T>
{
    /**
     * Controls whether this should be displayed in final results.
     */
    public boolean display = true;
    
    /**
     * Name of scenario being tested.
     */
    public final String NAME;

    /**
     * Stores the tree representation of this scenario.
     */
    public final Evaluatable<T> TREE_CONDITION;

    /**
     * See {@link #Scenario(boolean, String, Evaluatable)}.
     */
    public Scenario(final String NAME, final Evaluatable<T> TREE_CONDITION)
    {
        this(true, NAME, TREE_CONDITION);
    }
    
    /**
     * Fully parameterized constructor.
     * 
     * @param display {@link #display}
     * @param NAME {@link #NAME}
     * @param TREE_CONDITION {@link #TREE_CONDITION}
     */
    public Scenario(final boolean display, final String NAME, final Evaluatable<T> TREE_CONDITION)
    {
        this.display = display;
        this.NAME = NAME;
        this.TREE_CONDITION = TREE_CONDITION;
    }

    @Override
    public <E extends Collection<T>> boolean evaluate(E hand)
    {
        return this.TREE_CONDITION.evaluate(hand);
    }

    @Override
    public void reset()
    {
        this.TREE_CONDITION.reset();
    }

    @Override
    public boolean rollbackEvaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        return TREE_CONDITION.rollbackEvaluate(hand, next, fallback);
    }
}
