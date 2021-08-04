package structure;

import java.util.ArrayList;
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
        StringBuilder output = new StringBuilder(128);

        output.append(super.toString()); //call standard part

        //left child
        output.append(this.UNIQUE_IDENTIFIER);
        output.append("->");
        output.append(this.LEFT_CHILD.UNIQUE_IDENTIFIER);
        output.append(";\n");

        //right child
        output.append(this.UNIQUE_IDENTIFIER);
        output.append("->");
        output.append(this.RIGHT_CHILD.UNIQUE_IDENTIFIER);
        output.append(";\n");

        return output.toString();
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        Collection<Evaluable<T>> to_return = new ArrayList<Evaluable<T>>();
        to_return.add(this.LEFT_CHILD);
        to_return.add(this.RIGHT_CHILD);
        return to_return;
    }
}
