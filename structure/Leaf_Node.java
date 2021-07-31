package structure;

import java.util.Collection;
import java.util.Iterator;

/**
<b>
Purpose: Also will be used as the leaf node of the tree structure.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
Date: 2021-07-23
</b>
*/

public class Leaf_Node<T extends Reservable, U extends T> extends Base_Node<T>
{
    /**
     * Card to be matched.
     */
    public final U CARD;

    /**
     * Constructor for Reservable
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
    public boolean evaluate(Collection<T> hand, RollbackCallback next, RollbackCallback fallback) {
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
