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

/**
<b>
Purpose: base implementation of {@link Special_Ability_Actions} which involve transferring cards between locations<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-30/2022-1-1
</b>
*/

public abstract class Special_Ability_Transfer implements Special_Ability_Base
{
    /**
     * Number of cards to draw from deck.
     */
    public final int TRANSFER_AMOUNT;

    /**
     * Basic constructor.
     *
     * @param TRANSFER_AMOUNT {@link #TRANSFER_AMOUNT}
     */
    public Special_Ability_Transfer(final int TRANSFER_AMOUNT)
    {
        this.TRANSFER_AMOUNT = TRANSFER_AMOUNT;
    }
}
