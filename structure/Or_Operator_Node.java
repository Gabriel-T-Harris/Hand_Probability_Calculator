package structure;

import java.util.Collection;

/**
<b>
Purpose: Or operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn
</b>
*/

public class Or_Operator_Node extends Binary_Operator_Node
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluable, Evaluable)}.
     * 
     * @param LEFT_CHILD is the left operand
     * @param RIGHT_CHILD is the right operand 
     */
    public Or_Operator_Node(final Evaluable LEFT_CHILD, final Evaluable RIGHT_CHILD)
    {
        super("OR", LEFT_CHILD, RIGHT_CHILD);
    }

    // If LEFT_CHILD can take card(s), then continue evaluation
    // If LEFT_CHILD rolls back, then evaluate the RIGHT CHILD
    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        TestResult result = LEFT_CHILD.evaluate(hand, next);
        if (result == TestResult.Rollback)
            return RIGHT_CHILD.evaluate(hand, next);
        return result;
    }
}
