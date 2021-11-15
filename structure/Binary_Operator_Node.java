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

import java.util.Arrays;
import java.util.Collection;

/**
<b>
Purpose: Base of the binary operators.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24/2021-8-3/2021-8-10/2021-8-19
</b>
*/

public abstract class Binary_Operator_Node extends Base_Node
{
    /**
     * Left operand.
     */
    public final Evaluable LEFT_CHILD;

    /**
     * Right operand.
     */
    public final Evaluable RIGHT_CHILD;

    /**
     * Constructor.
     * 
     * @param NAME of the node
     * @param LEFT_CHILD is the left operand of the operator
     * @param RIGHT_CHILD is the right operand of the operator
     */
    public Binary_Operator_Node(String NAME, final Evaluable LEFT_CHILD, final Evaluable RIGHT_CHILD)
    {
        super(NAME);
        this.LEFT_CHILD = LEFT_CHILD;
        this.RIGHT_CHILD = RIGHT_CHILD;
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return super.toString() + this.UNIQUE_IDENTIFIER + "->" + this.LEFT_CHILD.UNIQUE_IDENTIFIER + ";\n" + this.UNIQUE_IDENTIFIER + "->" + this.RIGHT_CHILD.UNIQUE_IDENTIFIER +
               ";\n";
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return Arrays.asList(this.LEFT_CHILD, this.RIGHT_CHILD);
    }
}
