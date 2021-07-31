package structure;

import java.util.Collection;

/**
<b>
Purpose: And operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
Date: 2021-07-24
</b>
*/

public class And_Operator_Node<T> extends Binary_Operator_Node<T>
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluable, Evaluable)}.
     */
    public And_Operator_Node(final Evaluable<T> LEFT_CHILD, final Evaluable<T> RIGHT_CHILD)
    {
        super("AND", LEFT_CHILD, RIGHT_CHILD);
    }

    // If LEFT_CHILD can take card(s), then make sure the RIGHT CHILD can also take card(s)
    // If LEFT_CHILD can't take card(s), then fallback
    @Override
    public boolean evaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        return LEFT_CHILD.evaluate(
                hand,
                () -> RIGHT_CHILD.evaluate(hand, next, fallback),
                fallback
        );
    }
}
