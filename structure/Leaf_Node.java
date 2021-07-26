package structure;

import java.util.Collection;

/**
<b>
Purpose: Also will be used as the leaf node of the tree structure.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23
</b>
*/

public class Leaf_Node<T, U extends T> extends Base_Node<T>
{
    /**
     * Card to be matched.
     */
    public final U CARD;

    /**
     * Constructor for {@link #Leaf_Node(String, Object)}
     * 
     * @param NAME of the node
     * @param CARD to be matched
     */
    public Leaf_Node(String NAME, final U CARD)
    {
        super(NAME);
        this.CARD = CARD;
    }

    @Override
    public <E extends Collection<T>> boolean evaluate(E hand)
    {
        if (!super.evaluated)
        {
            super.result = hand.contains(this.CARD);
            super.evaluated = true;
        }

        return super.result;
    }
}
