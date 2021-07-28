package structure;

/**
<b>
Purpose: To be used as the representation of cards in a deck.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-25
</b>
*/

public class Deck_Card extends Base_Card
{
    /**
     * For classes in {@link structure} to use to know that a card has already been allocated.
     */
    public boolean used;

    /**
     * See {@link Base_Card#Base_Card(String)}
     */
    public Deck_Card(final String NAME)
    {
        super(NAME);
    }

    /**
     * Copy constructor.
     * 
     * @param INPUT to be copied.
     */
    public Deck_Card(final Deck_Card INPUT)
    {
        super(INPUT);
    }

    /**
     * Calls {@link #equals(Base_Card)} and then sets {@link #used} to that value. Meant to be convenience method for {@link structure}.
     */
    public <T extends Base_Card> boolean convenience_comparison(final T CARD)
    {
        return this.used = super.equals(CARD);
    }
}
