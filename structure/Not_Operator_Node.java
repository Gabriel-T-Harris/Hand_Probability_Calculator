package structure;

import java.util.Collection;

/**
<b>
Purpose: Not Operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
Date: 2021-07-24
</b>
*/

public class Not_Operator_Node<T> extends Base_Node<T>
{
    /**
     * Child which is negated.
     */
    public final Evaluable<T> CHILD;

    public Not_Operator_Node(final Evaluable<T> CHILD)
    {
        super("NOT");
        this.CHILD = CHILD;
    }

    // If the CHILD can take card(s) then fallback
    // If the CHILD can't take card(s), continue evaluation
    @Override
    public boolean evaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        return CHILD.evaluate(hand, fallback, next);
    }
}
