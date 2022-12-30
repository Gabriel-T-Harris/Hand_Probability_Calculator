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
Purpose: acts as the base special ability that others descend from for polymorphic special ability handling.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-25/2021-12-30/2022-1-1
</b>
*/

public interface Special_Ability_Base
{
    /**
     * Carry out the implemented special ability.
     * 
     * @param CURRENT_STATE which will be internally modified
     * 
     * @return true for carried out and false for could not be carried out (no changes performed)
     */
    public abstract boolean perform_special_ability(final Game_State<?> CURRENT_STATE);
}
