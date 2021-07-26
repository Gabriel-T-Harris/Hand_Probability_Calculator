package structure;

import java.util.Collection;

/**
<b>
Purpose: <br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

public class Xor_Operator_Node<T> extends Binary_Operator_Node<T>
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluatable, Evaluatable)}.
     */
    public Xor_Operator_Node(final Evaluatable<T> LEFT_CHILD, final Evaluatable<T> RIGHT_CHILD)
    {
        super("XOR", LEFT_CHILD, RIGHT_CHILD);
    }

    @Override
    public <E extends Collection<T>> boolean evaluate(E hand)
    {
        if (!super.evaluated)
        {
            super.result = super.LEFT_CHILD.evaluate(hand) ^ super.RIGHT_CHILD.evaluate(hand);
            super.evaluated = true;
        }

        return super.result;
    }
}
