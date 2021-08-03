package structure;

import java.util.Collection;

/**
<b>
Purpose: Not Operator<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
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

    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        TestResult result = CHILD.evaluate(hand, () -> TestResult.NotSuccess);
        if (result == TestResult.NotSuccess) {
            return TestResult.Rollback;
        }
        // result should only ever be TestResult.Rollback here
        return next.call();
    }
}
