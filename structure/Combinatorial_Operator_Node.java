package structure;

import java.util.Collection;
import simulation.special_ability.Game_State;

/**
<b>
Purpose: Combinatorial operator<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-10-10/2022-10-13/2022-10-31/2022-12-23
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
     * @param NAME of the node
     * @param FROM_K_OPTIONS are the values to form combinations with
     */
    public Combinatorial_Operator_Node(final int CHOOSE_N, final String NAME, final Evaluable[] FROM_K_OPTIONS)
    {
        super(NAME + " choose " + CHOOSE_N + " from " + FROM_K_OPTIONS.length);
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
        this.CHILDREN.reset_combinatorial_algorithm(); //reset every time for reuse

        final int COMBINATION_SIZE_MINUS_ONE = this.CHILDREN.get_choice_count() - 1;
        TestResult result = this.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, HAND, NEXT);

        //Continue looking at combinations until successful or non
        while ((result == TestResult.Rollback || result == TestResult.NotSuccess) && !this.CHILDREN.done())
        {
            this.CHILDREN.next_combincation();
            result = this.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, HAND, NEXT);
        }

        return result;
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(final Game_State<E> GAME_BOARD, final RollbackCallback NEXT)
    {
        this.CHILDREN.reset_combinatorial_algorithm(); //reset every time for reuse

        final int COMBINATION_SIZE_MINUS_ONE = this.CHILDREN.get_choice_count() - 1;
        TestResult result = this.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, GAME_BOARD, NEXT);

        //Continue looking at combinations until successful or non
        while ((result == TestResult.Rollback || result == TestResult.NotSuccess) && !this.CHILDREN.done())
        {
            this.CHILDREN.next_combincation();
            result = this.recursive_evaluate_subroutine(0, COMBINATION_SIZE_MINUS_ONE, GAME_BOARD, NEXT);
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
     *
     * @param <E> {@link Reservable} stuff to be usable for grand structure
     * 
     * @return the result of testing the {@link Combinatorial_Iteration#get_current_combination()}
     */
    private <E extends Reservable> TestResult recursive_evaluate_subroutine(final int CURRENT_INDEX, final int SIZE_MINUS_ONE, final Collection<E> HAND,
                                                                            final RollbackCallback NEXT)
    {
        if (CURRENT_INDEX == SIZE_MINUS_ONE)
            return this.CHILDREN.get_current_combination().get(CURRENT_INDEX).evaluate(HAND, NEXT);
        else
            return this.CHILDREN.get_current_combination().get(CURRENT_INDEX).evaluate(HAND,
                                                                                       () -> this.recursive_evaluate_subroutine(CURRENT_INDEX + 1, SIZE_MINUS_ONE, HAND, NEXT));
    }

    /**
     * Subroutine to test current combination of {@link #CHILDREN}. 
     * 
     * @param CURRENT_INDEX is expected to start off at 0, represents the condition being looked at currently
     * @param SIZE_MINUS_ONE is expected to be {@link Combinatorial_Iteration#get_choice_count()} - 1 to work properly
     * @param GAME_BOARD being evaluated
     * @param NEXT is what is to be evaluated in event current is successful, is also the accumulator of the recursive function
     *
     * @param <E> {@link Reservable} stuff to be usable for grand structure
     * 
     * @return the result of testing the {@link Combinatorial_Iteration#get_current_combination()}
     */
    private <E extends Reservable> TestResult recursive_evaluate_subroutine(final int CURRENT_INDEX, final int SIZE_MINUS_ONE, final Game_State<E> GAME_BOARD,
                                                                            final RollbackCallback NEXT)
    {
        if (CURRENT_INDEX == SIZE_MINUS_ONE)
            return this.CHILDREN.get_current_combination().get(CURRENT_INDEX).evaluate(GAME_BOARD, NEXT);
        else
            return this.CHILDREN.get_current_combination().get(CURRENT_INDEX)
                                .evaluate(GAME_BOARD, () -> this.recursive_evaluate_subroutine(CURRENT_INDEX + 1, SIZE_MINUS_ONE, GAME_BOARD, NEXT));
    }
}
