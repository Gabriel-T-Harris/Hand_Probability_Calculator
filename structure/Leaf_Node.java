package structure;

import java.util.Collection;
import java.util.Iterator;

/**
<b>
Purpose: Also will be used as the leaf node of the tree structure.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23
</b>
*/

public class Leaf_Node<T extends Base_Card, U extends T> extends Base_Node<T>
{
    /**
     * Card to be matched.
     */
    public final U CARD;

    /**
     * Constructor for {@link #Leaf_Node(String, Base_Card)}
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

    @Override
    public boolean rollbackEvaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
        for (T card : hand) {
            if (!card.isReserved() && card.equals(CARD)) {
                card.reserve();
                boolean result = next.call();
                if (result) {
                    return true;
                }
                card.release();
            }
        }
        return fallback.call();
    }
}
