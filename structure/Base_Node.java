package structure;

import java.util.Collection;

/**
<b>
Purpose: To be the base node which others will extend.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23/2021-8-3
</b>
*/

public abstract class Base_Node<T> extends Evaluable<T>
{
    /**
     * The name of this node.
     */
    public final String NAME;

    /**
     * Constructor of {@link #Base_Node}.
     * @param NAME the name to be given to this {@link #Base_Node}
     */
    public Base_Node(final String NAME)
    {
        this.NAME = NAME;
    }

    /**
     * for dot format
     */
    public String toString()
    {
        StringBuilder output = new StringBuilder(64);

        output.append(this.UNIQUE_IDENTIFIER);
        output.append("[label=\"");
        output.append(this.NAME.replace(">", "\\>").replace("<", "\\<").replace("\"", "\\\""));//escape certain characters
        output.append("\"];\n");

        return output.toString();
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        return null;
    }
}
