/*
    Copyright (C) 2022 Gabriel Toban Harris

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
package simulation.special_ability;

import simulation.special_ability.Game_State.Locations;

/**
<b>
Purpose: implementation of {@link Special_Ability_Actions#MILL}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-30/2022-1-1
</b>
*/

public class Special_Ability_Mill extends Special_Ability_Transfer
{
    /**
     * Basic constructor.
     *
     * @param TRANSFER_AMOUNT {@link Special_Ability_Transfer#TRANSFER_AMOUNT}
     */
    public Special_Ability_Mill(final int TRANSFER_AMOUNT)
    {
        super(TRANSFER_AMOUNT);
    }

    @Override
    public boolean perform_special_ability(final Game_State<?> CURRENT_STATE)
    {
        return CURRENT_STATE.special_ability_transfer(this.TRANSFER_AMOUNT, Locations.MAIN_DECK, Locations.GRAVEYARD);
    }
}
