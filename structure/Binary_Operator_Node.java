package structure;

import java.util.Arrays;
import java.util.Collection;

/**
<b>
Purpose: Base of the binary operators.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24/2021-8-3
</b>
*/

public abstract class Binary_Operator_Node<T> extends Base_Node<T>
{
    /**
     * Left operand.
     */
    public final Evaluable<T> LEFT_CHILD;

    /**
     * Right operand.
     */
    public final Evaluable<T> RIGHT_CHILD;

    /**
     * Constructor.
     * 
     * @param NAME of the node
     * @param LEFT_CHILD is the left operand of the operator
     * @param RIGHT_CHILD is the right operand of the operator
     */
    public Binary_Operator_Node(String NAME, final Evaluable<T> LEFT_CHILD, final Evaluable<T> RIGHT_CHILD)
    {
        super(NAME);
        this.LEFT_CHILD = LEFT_CHILD;
        this.RIGHT_CHILD = RIGHT_CHILD;
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return super.toString() + this.UNIQUE_IDENTIFIER + "->" + this.LEFT_CHILD.UNIQUE_IDENTIFIER + ";\n" + this.UNIQUE_IDENTIFIER + "->" + this.RIGHT_CHILD.UNIQUE_IDENTIFIER +
               ";\n";
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        return Arrays.asList(this.LEFT_CHILD, this.RIGHT_CHILD);
    }
}
