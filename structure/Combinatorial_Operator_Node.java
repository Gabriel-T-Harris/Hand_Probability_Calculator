package structure;

import java.util.Collection;

/**
<b>
Purpose: Combinatorial operator<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-10-10
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
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next)
    {
        printDebugStep(hand);

        TestResult result;

        //current over and, else rollback loop next combination until done. Then not success

        return result;
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return this.CHILDREN.get_options();
    }
}
