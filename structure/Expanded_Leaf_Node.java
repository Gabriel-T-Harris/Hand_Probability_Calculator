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
