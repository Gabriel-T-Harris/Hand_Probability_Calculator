package structure;

/**
<b>
Purpose: To be used as the representation of cards in a deck.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn <br>
</b>
*/

public class Deck_Card extends Base_Card implements Reservable
{
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

    @Override
    public boolean isReserved() {
        return reserved;
    }

    /**
     * Calls {@link #equals(Base_Card)} and then sets {@link #reserved} to that value. Meant to be convenience method for {@link structure}.
     */
    public <T extends Base_Card> boolean convenience_comparison(final T CARD)
    {
        return this.reserved = this.equals(CARD);
    }

    @Override
    public void reserve() {
        reserved = true;
    }

    @Override
    public void release() {
        reserved = false;
    }
}
