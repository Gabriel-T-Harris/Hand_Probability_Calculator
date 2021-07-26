package structure;

import java.util.jar.Attributes.Name;

/**
<b>
Purpose: Representation of a card, to be extended when stored in decks.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-25
</b>
*/

public class Base_Card
{
    /**
     * Represents the name of this card.
     */
    public final String NAME;

    /**
     * Value should be {@link Name#hashCode()}.
     */
    protected final int HASH_CODE;

    /**
     * Constructor.
     * 
     * @param NAME of the card.
     */
    public Base_Card(final String NAME)
    {
        this.NAME = NAME;
        this.HASH_CODE = this.NAME.hashCode();
    }

    /**
     * Copy constructor.
     * 
     * @param CARD to be copied.
     */
    public Base_Card(final Base_Card CARD)
    {
        this.NAME = CARD.NAME;
        this.HASH_CODE = CARD.HASH_CODE;
    }

    @Override
    public boolean equals(final Object INPUT)
    {
        if (INPUT instanceof Base_Card)
            return this.equals(INPUT);
        else
            return false;
    }

    /**
     * Function to check card equality.
     * 
     * @param <T> anything which either is or extends, {@link Base_Card}.
     * @param CARD to be compared
     * @return true for equivalent, otherwise false
     */
    public <T extends Base_Card> boolean equals(final T CARD)
    {
        return this.HASH_CODE == CARD.HASH_CODE;
    }

    @Override
    public int hashCode()
    {
        return this.HASH_CODE;
    }

    /**
     * The toString function of this class.
     */
    @Override
    public String toString()
    {
        return "Card name: " + this.NAME;
    }
}
