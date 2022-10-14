package structure;

import java.util.Collection;

/**
<b>
Purpose: Combinatorial operator<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-10-10/2022-10-13
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

    /**
     * Subroutine to test current combination of {@link #CHILDREN}.
     * 
     * @return the result of testing the {@link Combinatorial_Iteration#get_current_combination()}
     */
    public TestResult evaluate_subroutine()
    {
        //evaluate function should not return a litteral value besides TestResult.Rollback.
        //TestResult.NotSuccess is typically only returned from RollbackCallback's next argument
        //TestResult.Success only comes from inital Evaluable.evaluate(Collection<E>)
        //recursion? next is subsequent index
    }

    @Override
    protected <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next)
    {
        printDebugStep(hand);

        //FIXME: feels wrong, loop use
        TestResult result = this.evaluate_subroutine();

        //TODO: TestResult.NotSuccess should be handled
        while (result != TestResult.Rollback)
            result = !this.CHILDREN.done() ? TestResult.Rollback : this.evaluate_subroutine();

        //current over and, else rollback loop next combination until done. Then not success

        return result; //looks wrong, next is not used, it is pass of success, next should be passed some how. Next goes with last index condition.
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return this.CHILDREN.get_options();
    }
}
