package structure;

import java.util.Collection;

/**
<b>
Purpose: Xor Operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn
</b>
*/

public class Xor_Operator_Node extends Binary_Operator_Node
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluable, Evaluable)}.
     * 
     * @param LEFT_CHILD is the left operand
     * @param RIGHT_CHILD is the right operand
     */
    public Xor_Operator_Node(final Evaluable LEFT_CHILD, final Evaluable RIGHT_CHILD)
    {
        super("XOR", LEFT_CHILD, RIGHT_CHILD);
    }

    // If LEFT_CHILD can take card(s), then make sure the RIGHT CHILD can't take card(s)
    // If LEFT_CHILD can't take card(s), then make sure the RIGHT CHILD can take card(s)
    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        TestResult result = LEFT_CHILD.evaluate(
                hand,
                () -> RIGHT_CHILD.evaluate(hand, () -> TestResult.NotSuccess)
        );
        if (result == TestResult.NotSuccess) {
            return TestResult.Rollback;
        }
        // Result should only ever be TestResult.Rollback here
        return RIGHT_CHILD.evaluate(hand, next);
    }
}
