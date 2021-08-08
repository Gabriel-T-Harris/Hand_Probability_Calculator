package structure;

import java.util.Collection;

/**
<b>
Purpose: To be the base node which others will extend.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23/2021-8-3/2021-8-8
</b>
*/

public abstract class Base_Node<T> extends Evaluable<T>
{
    /**
     * Constructor of {@link #Base_Node}.
     * @param NAME the name to be given to this {@link #Base_Node}
     */
    public Base_Node(final String NAME)
    {
        super(NAME);
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return this.UNIQUE_IDENTIFIER + "[label=\"" + this.NAME.replace(">", "\\>").replace("<", "\\<").replace("\"", "\\\"") + "\"];\n";
    }

    @Override
    protected Collection<? extends Evaluable<T>> continue_breath_search()
    {
        return null;
    }
}
