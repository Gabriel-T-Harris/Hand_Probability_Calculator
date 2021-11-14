package structure;

import java.util.Collection;

/**
<b>
Purpose: Contain entire tree structure of {@link Base_Node} in a named reusable manner.<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Scenario extends Evaluable
{
    /**
     * Controls whether this should be displayed in final results.
     */
    public final boolean DISPLAY;

    /**
     * Stores the tree representation of this scenario.
     */
    public final Evaluable TREE_CONDITION;

    /**
     * See {@link #Scenario(boolean, String, Evaluable)}.
     * 
     * @param NAME to be called
     * @param TREE_CONDITION to be {@link Evaluable#evaluate(Collection)}
     */
    public Scenario(final String NAME, final Evaluable TREE_CONDITION)
    {
        this(true, NAME, TREE_CONDITION);
    }
    
    /**
     * Fully parameterized constructor.
     * 
     * @param DISPLAY {@link #DISPLAY}
     * @param NAME of scenario being tested
     * @param TREE_CONDITION {@link #TREE_CONDITION}
     */
    public Scenario(final boolean DISPLAY, final String NAME, final Evaluable TREE_CONDITION)
    {
        super(NAME);
        this.DISPLAY = DISPLAY;
        this.TREE_CONDITION = TREE_CONDITION;
    }

    @Override
    public <E extends Reservable> TestResult evaluate(Collection<E> hand, RollbackCallback next) {
        printDebugStep(hand);
        return TREE_CONDITION.evaluate(hand, next);
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return this.UNIQUE_IDENTIFIER + "[label=\"" + this.NAME.replace(">", "\\>").replace("<", "\\<").replace("\"", "\\\"") + "\"];\n";
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return null;
    }
}
