package structure;

import java.util.Collection;

/**
<b>
Purpose: And operator<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class And_Operator_Node extends Binary_Operator_Node
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluable, Evaluable)}.
     * 
     * @param LEFT_CHILD is the left operand
     * @param RIGHT_CHILD is the right operand
     */
    public And_Operator_Node(final Evaluable LEFT_CHILD, final Evaluable RIGHT_CHILD)
    {
        super("AND", LEFT_CHILD, RIGHT_CHILD);
    }

    // If LEFT_CHILD can take card(s), then make sure the RIGHT CHILD can also take card(s)
    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        return LEFT_CHILD.evaluate(
                hand,
                () -> RIGHT_CHILD.evaluate(hand, next)
        );
    }
}
