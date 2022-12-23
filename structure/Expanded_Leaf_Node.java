package structure;

import simulation.special_ability.Game_State;

/**
<b>
Purpose: Exentension of {@link Leaf_Node} such that {@link simulation.special_ability.Game_State} is supported without having the costs for when it is not used.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-12-23
</b>
* @param <T> is the type of card to hold, suggestion of {@link Base_Card}.
*/

public class Expanded_Leaf_Node<T> extends Leaf_Node<T>
{
    /**
     * Where to look.
     */
    public final Game_State.Locations WHERE;

    /**
     * Constructor for Reserverable that supports {@link simulation.special_ability.Game_State} to the extent of using {@link simulation.special_ability.Game_State.Locations}.
     * 
     * @param NAME of the node
     * @param WHERE is the place to look
     * @param CARD to be matched
     */
    public Expanded_Leaf_Node(final String NAME, Game_State.Locations WHERE, final T CARD)
    {
        super(NAME, CARD);
        this.WHERE = WHERE;
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Game_State<E> GAME_BOARD, final RollbackCallback NEXT)
    {
        return super.evaluate(GAME_BOARD.get_unmodifiable_cards(WHERE), NEXT);
    }
}
