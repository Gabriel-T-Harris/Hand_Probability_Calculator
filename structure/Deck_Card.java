package structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
<b>
Purpose: To be used as the representation of cards in a deck.<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn
</b>
*/

public class Deck_Card extends Base_Card implements Reservable
{
    /**
     * For routines declared in {@link Reservable}
     */
    protected boolean reserved;

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
        this.reserved = false;
    }

    /**
     * Makes a deep copy of a container. 
     * 
     * @param <E> incoming collection type
     * 
     * @param INPUT container of {@link Deck_Card} to be duplicated.
     * 
     * @return the deep copied values wrapped in an {@link ArrayList}
     */
    public static <E extends Collection<Deck_Card>> ArrayList<Deck_Card> deep_copy(final E INPUT)
    {
        return INPUT.stream().map(Deck_Card::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean isReserved() {
        return reserved;
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
