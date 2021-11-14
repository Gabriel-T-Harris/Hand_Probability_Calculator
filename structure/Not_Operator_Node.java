package structure;

import java.util.Collection;
import java.util.Collections;

/**
<b>
Purpose: Not Operator<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Not_Operator_Node extends Base_Node
{
    /**
     * Child which is negated.
     */
    public final Evaluable CHILD;

    /**
     * Constructor. Meaning of operator is the lack of something. Thus looking for NOT A would only be true if A was not unreserved in remaining hand.
     *
     * @param CHILD to be negated.
     */
    public Not_Operator_Node(final Evaluable CHILD)
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

    /**
     * for dot format
     */
    public String toString()
    {
        return super.toString() + this.UNIQUE_IDENTIFIER + "->" + this.CHILD.UNIQUE_IDENTIFIER + ";\n";
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return Collections.singletonList(this.CHILD);
    }
}
