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
Purpose: Or operator<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Or_Operator_Node extends Binary_Operator_Node
{
    /**
     * Constructor, refer to {@link Binary_Operator_Node#Binary_Operator_Node(String, Evaluable, Evaluable)}.
     * 
     * @param LEFT_CHILD is the left operand
     * @param RIGHT_CHILD is the right operand 
     */
    public Or_Operator_Node(final Evaluable LEFT_CHILD, final Evaluable RIGHT_CHILD)
    {
        super("OR", LEFT_CHILD, RIGHT_CHILD);
    }

    // If LEFT_CHILD can take card(s), then continue evaluation
    // If LEFT_CHILD rolls back, then evaluate the RIGHT CHILD
    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        TestResult result = LEFT_CHILD.evaluate(hand, next);
        if (result == TestResult.Rollback)
            return RIGHT_CHILD.evaluate(hand, next);
        return result;
    }
}
