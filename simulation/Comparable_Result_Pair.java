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
package simulation;

/**
<b>
Purpose: Underlying value representing number of successful matches.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-21 (date of extraction from {@link Simulation})/2021-8-24
</b>
 */

public class Comparable_Result_Pair implements Comparable<Comparable_Result_Pair>
{
    /**
     * Underlying value representing number of successful matches.
     */
    public final Float PERCENTAGE;

    /**
     * Name of this pair.
     */
    public final String NAME;

    /**
     * Basic constructor.
     * 
     * @param PERCENTAGE percentage of number of times evaluation succeeded
     * @param NAME of pair
     */
    public Comparable_Result_Pair(final Float PERCENTAGE, final String NAME)
    {
        this.PERCENTAGE = PERCENTAGE;
        this.NAME = NAME;
    }

    @Override
    public int compareTo(final Comparable_Result_Pair OTHER)
    {
        final int FLOAT_COMPARISON = this.PERCENTAGE.compareTo(OTHER.PERCENTAGE);

        if (FLOAT_COMPARISON == 0)
            return -NAME.compareTo(OTHER.NAME); //negative to have its order reversed
        else
            return FLOAT_COMPARISON;
    }
}
