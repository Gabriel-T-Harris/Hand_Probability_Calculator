package structure;

import java.util.Collection;

/**
<b>
Purpose: Not Operator<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

public class Not_Operator_Node<T> extends Base_Node<T>
{
    /**
     * Child which is negated.
     */
    public final Evaluatable<T> CHILD;

    public Not_Operator_Node(final Evaluatable<T> CHILD)
    {
        super("NOT");
        this.CHILD = CHILD;
    }

    /*
     * Refer to parent.
     */
    @Override
    public <E extends Collection<T>> boolean evaluate(E hand)
    {
        if (!super.evaluated)
        {
            super.result = !this.CHILD.evaluate(hand);
            super.evaluated = true;
        }
        return super.result;
    }

    /**
     * Resets self and child for next hand.
     */
    @Override
    public void reset()
    {
        super.reset();
        this.CHILD.reset();
    }

    @Override
    public boolean rollbackEvaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        return CHILD.rollbackEvaluate(hand, fallback, next);
    }
}
