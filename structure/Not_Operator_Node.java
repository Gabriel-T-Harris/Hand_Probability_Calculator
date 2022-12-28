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
import java.util.Collections;
import simulation.special_ability.Game_State;

/**
<b>
Purpose: Not Operator<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Not_Operator_Node extends Base_Node
{
    /**
     * Child which is negated.
     */
    public final Evaluable CHILD;

    /**
     * Constructor. Meaning of operator is the lack of something. Thus looking for NOT A would only be true if A was not unreserved in remaining hand.
     *
     * @param CHILD to be negated.
     */
    public Not_Operator_Node(final Evaluable CHILD)
    {
        super("NOT");
        this.CHILD = CHILD;
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return super.toString() + this.UNIQUE_IDENTIFIER + "->" + this.CHILD.UNIQUE_IDENTIFIER + ";\n";
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Collection<E> HAND, final RollbackCallback NEXT)
    {
        printDebugStep(HAND);
        TestResult result = CHILD.evaluate(HAND, () -> TestResult.NotSuccess);

        return evaluate_subroutine(result, NEXT);
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Game_State<E> GAME_BOARD, final RollbackCallback NEXT)
    {
        TestResult result = CHILD.evaluate(GAME_BOARD, () -> TestResult.NotSuccess);

        return evaluate_subroutine(result, NEXT);
    }

    /**
     * Simple subroutine to ensure that callers line up. Such also removes duplication.
     * 
     * @param RESULT is the value to test against
     * @param NEXT is what is to be done when successful
     * 
     * @return the culmination of all the function calls
     */
    private static TestResult evaluate_subroutine(final TestResult RESULT, final RollbackCallback NEXT)
    {
        if (RESULT == TestResult.NotSuccess)
            return TestResult.Rollback;

        // result should only ever be TestResult.Rollback here
        return NEXT.call();
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return Collections.singletonList(this.CHILD);
    }
}
