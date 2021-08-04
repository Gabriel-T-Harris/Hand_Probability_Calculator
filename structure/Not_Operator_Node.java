package structure;

import java.util.ArrayList;
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

    /**
     * for dot format
     */
    public String toString()
    {
        StringBuilder output = new StringBuilder(128);

        output.append(super.toString()); //call standard part

        //child
        output.append(this.UNIQUE_IDENTIFIER);
        output.append("->");
        output.append(this.CHILD.UNIQUE_IDENTIFIER);
        output.append(";\n");

        return output.toString();
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        Collection<Evaluable<T>> to_return = new ArrayList<Evaluable<T>>();
        to_return.add(this.CHILD);
        return to_return;
    }
}
