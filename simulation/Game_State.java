package simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import structure.Reservable;

/**
<b>
Purpose: Holds the locations of all the cards that are being checked.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-25
</b>

* @param <R> is the value of the contents being stored.
*/

public class Game_State<R extends Reservable>
{
    /**
     * Represents either the place to look or the place something is stored (depending on context), with respect to {@link Game_State}. 
     */
    public static enum Locations
    {
        HAND, GRAVEYARD, BANISH, FIELD, EXTRA_DECK, MAIN_DECK;
    }

    /**
     * Stores all the cards and where they are.
     */
    protected final HashMap<Locations, ArrayList<R>> STORAGE = new HashMap<Locations, ArrayList<R>>(Locations.values().length + 1, 1f);//TODO: consider renaming

    /**
     * Basic constructor. Note that parameter is shallowly copied to minimize costs.
     * 
     * @param HAND represents the cards in hand.
     */
    public Game_State(final ArrayList<R> HAND)
    {
        HAND.trimToSize();
        this.STORAGE.put(Locations.HAND, HAND);
    }

    /**
     * Constructor meant to be used when //TODO link special abilities, are used.
     * 
     * @param HAND represents the cards in hand.
     * @param GRAVEYARD represents the cards in graveyard.
     */
    public Game_State(final ArrayList<R> HAND, final ArrayList<R> GRAVEYARD)
    {
        this(HAND);
        GRAVEYARD.trimToSize();
        this.STORAGE.put(Locations.GRAVEYARD, GRAVEYARD);
    }

    //getter
    /**
     * Universal getter to access underlying stored cards.
     * 
     * @param WHERE the cards one would like are located
     * 
     * @return a {@link Collection} of the requested cards, empty in the event that no cards are there
     */
    private ArrayList<R> get_cards(final Locations WHERE)
    {
        return this.STORAGE.getOrDefault(WHERE, new ArrayList<R>());
    }

    /**
     * Universal getter to access underlying stored cards. Returned cards are {@link Collections#unmodifiableCollection(Collection)}. However they are backed my their internal values.
     * 
     * @param WHERE the cards one would like are located
     * 
     * @return a {@link Collection} of the requested cards, empty in the event that no cards are there
     */
    public Collection<R> get_unmodifiable_cards(final Locations WHERE)
    {
        return Collections.unmodifiableCollection(this.STORAGE.getOrDefault(WHERE, new ArrayList<R>()));
    }

    //setter
    /**
     * Sets cards to specified location in internal storage. However each location can only have cards set once.
     * 
     * @param WHERE cards should be set
     * @param CARDS to be set shallowly
     * 
     * @return true for CARDS were set and false for CARDS were not set
     */
    public boolean set_cards(final Locations WHERE, final ArrayList<R> CARDS)
    {
        CARDS.trimToSize();
        return this.STORAGE.putIfAbsent(WHERE, CARDS) == null;
    }

    /**
     * Convenience method. {@link structure.Reservable#release()} all cards in hand. {@link Reservable#release()}
     * 
     * @param <R> the type of cards in the deck
     * 
     * @param HAND to be reset
     */
    public static <R extends Reservable> void reset_hand(final ArrayList<R> HAND)
    {
        for (final R CARD : HAND)
            CARD.release();
    }

    /**
     * Convenience method. For {@link structure.Reservable#release()} all internal values.
     */
    public void reset_all()
    {
        for (final ArrayList<R> STUFF : this.STORAGE.values())
            Game_State.reset_hand(STUFF);
    }

    /**
     * Convenience method. Calls {@link #reset_hand(ArrayList)} on cards in specified location.
     * 
     * @param WHERE the cards to be reset are.
     */
    public void reset_location(final Locations WHERE)
    {
        Game_State.reset_hand(this.get_cards(WHERE));
    }
}
