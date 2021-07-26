package structure;

/**
<b>
Purpose: Base of the binary operators.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

//TODO: consider converting to at least 2
public abstract class Binary_Operator_Node<T> extends Base_Node<T>
{
    /**
     * Left operand.
     */
    public final Evaluatable<T> LEFT_CHILD;

    /**
     * Right operand.
     */
    public final Evaluatable<T> RIGHT_CHILD;

    /**
     * Constructor.
     * 
     * @param NAME of the node
     * @param LEFT_CHILD is the left operand of the operator
     * @param RIGHT_CHILD is the right operand of the operator
     */
    public Binary_Operator_Node(String NAME, final Evaluatable<T> LEFT_CHILD, final Evaluatable<T> RIGHT_CHILD)
    {
        super(NAME);
        this.LEFT_CHILD = LEFT_CHILD;
        this.RIGHT_CHILD = RIGHT_CHILD;
    }

    /**
     * Resets self and children for next hand.
     */
    @Override
    public void reset()
    {
        super.reset();
        this.LEFT_CHILD.reset();
        this.RIGHT_CHILD.reset();
    }
}
