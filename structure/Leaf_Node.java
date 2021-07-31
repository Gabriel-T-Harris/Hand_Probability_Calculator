package structure;

import java.util.Collection;

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

    /*
      For each card that matches, reserve it, and try to evaluate the rest of the condition
      If the rest of the condition evaluation fails, look for the next valid card to take and repeat
      If the rest of the condition evaluation is successful, then return true
      If there is no more valid cards, call the fallback evaluation
     */
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
