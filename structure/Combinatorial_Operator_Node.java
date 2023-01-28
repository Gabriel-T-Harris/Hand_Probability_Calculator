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
package structure;

import java.util.Collection;
import simulation.special_ability.Game_State;

/**
<b>
Purpose: Combinatorial operator<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-10-10/2022-10-13/2022-10-31/2022-12-23/2023-1-22/2023-1-28
</b>
*/

public class Combinatorial_Operator_Node extends Base_Node
{

    /**
     * Contains both pieces of data to form combinations with and generates combinations as well.
     */
    public final Combinatorial_Iteration<Evaluable> CHILDREN;

    /**
     * Constructor.
     * 
     * @param CHOOSE_N is the size of each combination
     * @param FROM_K_OPTIONS are the values to form combinations with
     */
    public Combinatorial_Operator_Node(final int CHOOSE_N, final Evaluable[] FROM_K_OPTIONS)
    {
        super("COMBINATORIC choose " + CHOOSE_N + " from " + FROM_K_OPTIONS.length);
        this.CHILDREN = new Combinatorial_Iteration<Evaluable>(CHOOSE_N, FROM_K_OPTIONS);
    }

    /**
     * for dot format
     */
    public String toString()
    {
        final StringBuilder CHILDREN_DOT_FORMAT = new StringBuilder(256); //256 felt like a good starting number
        final Collection<Evaluable> OPTIONS = this.CHILDREN.get_options();

        for (Evaluable child : OPTIONS)
        {
            CHILDREN_DOT_FORMAT.append(this.UNIQUE_IDENTIFIER);
            CHILDREN_DOT_FORMAT.append("->");
            CHILDREN_DOT_FORMAT.append(child.UNIQUE_IDENTIFIER);
            CHILDREN_DOT_FORMAT.append(";\n");
        }

        return super.toString() + CHILDREN_DOT_FORMAT.toString();
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Collection<E> HAND, final RollbackCallback NEXT)
    {
        printDebugStep(HAND);
        final int COMBINATION_SIZE_MINUS_ONE = this.CHILDREN.get_choice_count() - 1;
        //TODO: turn into object pool
        final Combinatorial_Iteration<Evaluable>.Combinatorial_Iteration_Save_Point SAVE_POINT = this.CHILDREN.new Combinatorial_Iteration_Save_Point(); //make a new one each time

        TestResult result = Combinatorial_Operator_Node.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, HAND, NEXT, SAVE_POINT);
        //Continue looking at combinations until successful or non
        while ((result == TestResult.Rollback || result == TestResult.NotSuccess) && !SAVE_POINT.done())
        {
            this.CHILDREN.next_combincation(SAVE_POINT);
            result = Combinatorial_Operator_Node.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, HAND, NEXT, SAVE_POINT);
        }

        return result;
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Game_State<E> GAME_BOARD, final RollbackCallback NEXT)
    {
        final int COMBINATION_SIZE_MINUS_ONE = this.CHILDREN.get_choice_count() - 1;
        //TODO: turn into object pool
        final Combinatorial_Iteration<Evaluable>.Combinatorial_Iteration_Save_Point SAVE_POINT = this.CHILDREN.new Combinatorial_Iteration_Save_Point(); //make a new one each time

        TestResult result = Combinatorial_Operator_Node.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, GAME_BOARD, NEXT, SAVE_POINT);

        //Continue looking at combinations until successful or non
        while ((result == TestResult.Rollback || result == TestResult.NotSuccess) && SAVE_POINT.done())
        {
            this.CHILDREN.next_combincation(SAVE_POINT);
            result = Combinatorial_Operator_Node.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, GAME_BOARD, NEXT, SAVE_POINT);
        }

        return result;
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return this.CHILDREN.get_options();
    }

    /**
     * Subroutine to test current combination of {@link #CHILDREN}. 
     * 
     * @param CURRENT_INDEX is expected to start off at 0, represents the condition being looked at currently
     * @param SIZE_MINUS_ONE is expected to be {@link Combinatorial_Iteration#get_choice_count()} - 1 to work properly
     * @param HAND being evaluated
     * @param NEXT is what is to be evaluated in event current is successful, is also the accumulator of the recursive function
     * @param SAVE_POINT {@link Combinatorial_Iteration.Combinatorial_Iteration_Save_Point}
     *
     * @param <E> {@link Reservable} stuff to be usable for grand structure
     * 
     * @return the result of testing the {@link Combinatorial_Iteration#get_current_combination()}
     */
    private static <E extends Reservable> TestResult recursive_evaluate_subroutine(final int CURRENT_INDEX, final int SIZE_MINUS_ONE, final Collection<E> HAND,
                                                                                   final RollbackCallback NEXT,
                                                                                   final Combinatorial_Iteration<Evaluable>.Combinatorial_Iteration_Save_Point SAVE_POINT)
    {
        if (CURRENT_INDEX == SIZE_MINUS_ONE)
            return SAVE_POINT.get_current_combination().get(CURRENT_INDEX).evaluate(HAND, NEXT);
        else
            return SAVE_POINT.get_current_combination().get(CURRENT_INDEX).evaluate(HAND, () -> Combinatorial_Operator_Node.recursive_evaluate_subroutine(CURRENT_INDEX + 1, SIZE_MINUS_ONE, HAND, NEXT, SAVE_POINT));
    }

    /**
     * Subroutine to test current combination of {@link #CHILDREN}. 
     * 
     * @param CURRENT_INDEX is expected to start off at 0, represents the condition being looked at currently
     * @param SIZE_MINUS_ONE is expected to be {@link Combinatorial_Iteration#get_choice_count()} - 1 to work properly
     * @param GAME_BOARD being evaluated
     * @param NEXT is what is to be evaluated in event current is successful, is also the accumulator of the recursive function
     * @param SAVE_POINT {@link Combinatorial_Iteration.Combinatorial_Iteration_Save_Point}
     * 
     * @param <E> {@link Reservable} stuff to be usable for grand structure
     * 
     * @return the result of testing the {@link Combinatorial_Iteration#get_current_combination()}
     */
    private static <E extends Reservable> TestResult recursive_evaluate_subroutine(final int CURRENT_INDEX, final int SIZE_MINUS_ONE, final Game_State<E> GAME_BOARD,
                                                                                   final RollbackCallback NEXT,
                                                                                   final Combinatorial_Iteration<Evaluable>.Combinatorial_Iteration_Save_Point SAVE_POINT)
    {
        if (CURRENT_INDEX == SIZE_MINUS_ONE)
            return SAVE_POINT.get_current_combination().get(CURRENT_INDEX).evaluate(GAME_BOARD, NEXT);
        else
            return SAVE_POINT.get_current_combination().get(CURRENT_INDEX).evaluate(GAME_BOARD, () -> Combinatorial_Operator_Node.recursive_evaluate_subroutine(CURRENT_INDEX + 1, SIZE_MINUS_ONE, GAME_BOARD, NEXT, SAVE_POINT));
    }
}
