package structure;

/**
<b>
Purpose: To be the base node which others will extend.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23
</b>
*/

//TODO: add toString() methods with some reference to outputting as dot file format.
//TODO: figure out edge cases in evaluation for both duplication (maybe paint) and overlap (maybe attempt condition evaluation in different combinations).
public abstract class Base_Node<T> implements Evaluatable<T>
{
    /**
     * Unique identifier for this node.
     */
    public final int UNIQUE_IDENTIFIER;

    /**
     * The name of this node.
     */
    public final String NAME;

    /**
     * Used to prevent repeated evaluations.
     */
    protected boolean evaluated = false;

    /**
     * Used to store result.
     */
    protected boolean result;

    /**
     * Used to set {@link #UNIQUE_IDENTIFIER}
     */
    private static int CREATED_NODES_COUNT = 0;

    /**
     * Constructor of {@link #Base_Node}.
     * @param NAME the name to be given to this {@link #Base_Node}
     */
    public Base_Node(final String NAME)
    {
        this.NAME = NAME;
        this.UNIQUE_IDENTIFIER = ++CREATED_NODES_COUNT;
    }
}
