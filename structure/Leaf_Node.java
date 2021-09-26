package structure;

import java.util.Collection;

/**
<b>
Purpose: Also will be used as the leaf node of the tree structure.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn
</b>

* @param <T> is the type of card to hold, suggestion of {@link Base_Card}.
*/

public class Leaf_Node<T> extends Base_Node
{
    /**
     * Card to be matched.
     */
    public final T CARD;

    /**
     * Constructor for Reservable
     * 
     * @param NAME of the node
     * @param CARD to be matched
     */
    public Leaf_Node(String NAME, final T CARD)
    {
        super(NAME);
        this.CARD = CARD;
    }

    /*
      For each card that matches, reserve it, and try to evaluate the rest of the condition and return the result
      If a NOT condition was met, release all card part of that condition
      If a Rollback signal was received, release the card and look for the next valid candidate
      If no valid options, throw a Rollback signal
     */
    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        if (Evaluable.debugMode) {
            System.out.printf("Trying to match %s\n", CARD);
            printDebugStep(hand);
        }
        for (E card : hand) {
            if (!card.isReserved() && card.equals(CARD)) {
                if (Evaluable.debugMode) {
                    System.out.printf("Taking card %s\n", card);
                }
                card.reserve();
                TestResult result = next.call();
                if (result == TestResult.Rollback || result == TestResult.NotSuccess) {
                    if (Evaluable.debugMode) {
                        System.out.printf("Releasing card %s\n", card);
                    }
                    card.release();
                }
                if (result == TestResult.Success || result == TestResult.NotSuccess) {
                    return result;
                }
            }
        }
        if (Evaluable.debugMode) {
            System.out.printf("No Options for %s... rolling back\n", CARD);
        }
        return TestResult.Rollback;
    }
}
