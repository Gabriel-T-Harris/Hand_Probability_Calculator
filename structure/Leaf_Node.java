/*
    Copyright (C) 2021 Gabriel Toban Harris

        This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

        This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package structure;

import java.util.Collection;

/**
<b>
Purpose: Also will be used as the leaf node of the tree structure.<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
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
                if (result == TestResult.NotSuccess || result == TestResult.Success) {
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
