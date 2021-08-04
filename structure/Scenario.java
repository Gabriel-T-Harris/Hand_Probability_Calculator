package structure;

import java.util.Collection;

/**
<b>
Purpose: Contain entire tree structure of {@link Base_Node} in a named reusable manner.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
</b>
*/

public class Scenario<T> extends Evaluable<T>
{
    /**
     * Controls whether this should be displayed in final results.
     */
    public final boolean display;
    
    /**
     * Name of scenario being tested.
     */
    public final String NAME;

    /**
     * Stores the tree representation of this scenario.
     */
    public final Evaluable<T> TREE_CONDITION;

    /**
     * See {@link #Scenario(boolean, String, Evaluable)}.
     */
    public Scenario(final String NAME, final Evaluable<T> TREE_CONDITION)
    {
        this(true, NAME, TREE_CONDITION);
    }
    
    /**
     * Fully parameterized constructor.
     * 
     * @param display {@link #display}
     * @param NAME {@link #NAME}
     * @param TREE_CONDITION {@link #TREE_CONDITION}
     */
    public Scenario(final boolean display, final String NAME, final Evaluable<T> TREE_CONDITION)
    {
        this.display = display;
        this.NAME = NAME;
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
        StringBuilder output = new StringBuilder(128);

        output.append(this.UNIQUE_IDENTIFIER);
        output.append("[label=\"");
        output.append(this.NAME.replace(">", "\\>").replace("<", "\\<").replace("\"", "\\\""));//escape certain characters
        output.append("\"];\n");

        //child
        output.append(this.UNIQUE_IDENTIFIER);
        output.append("->");
        output.append(this.TREE_CONDITION.UNIQUE_IDENTIFIER);
        output.append(";\n");

        return output.toString();
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        return null;//TODO: consider changing to adding TREE_CONDITION.
    }
}
