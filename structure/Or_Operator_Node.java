package structure;

import java.util.Collection;

/**
<b>
Purpose: Or operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
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
    public boolean evaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        // If LEFT_CHILD can take card(s), then continue evaluation
        // If LEFT_CHILD can't take card(s), then make sure the RIGHT CHILD can take card(s)
        return LEFT_CHILD.evaluate(
                hand,
                next,
                () -> RIGHT_CHILD.evaluate(hand, next, fallback)
        );
    }
}
