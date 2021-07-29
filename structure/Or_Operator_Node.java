package structure;

import java.util.Collection;

/**
<b>
Purpose: Or operator<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

public class Or_Operator_Node<T> extends Binary_Operator_Node<T>
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluatable, Evaluatable)}.
     */
    public Or_Operator_Node(final Evaluatable<T> LEFT_CHILD, final Evaluatable<T> RIGHT_CHILD)
    {
        super("OR", LEFT_CHILD, RIGHT_CHILD);
    }

    @Override
    public <E extends Collection<T>> boolean evaluate(E hand)
    {
        if (!super.evaluated)
        {
            super.result = super.LEFT_CHILD.evaluate(hand) || super.RIGHT_CHILD.evaluate(hand);
            super.evaluated = true;
        }

        return super.result;
    }

    @Override
    public boolean rollbackEvaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        return LEFT_CHILD.rollbackEvaluate(
                hand,
                next,
                () -> RIGHT_CHILD.rollbackEvaluate(hand, next, fallback)
        );
    }
}
