package simulation.special_ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import structure.Reservable;

/**
<b>
Purpose: Holds the locations of all the cards that are being checked.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-25/2021-12-30/2021-12-31/2022-1-1/2022-12-[26, 27]
</b>
* @param <R> is the value of the contents being stored
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
    private final HashMap<Locations, ArrayList<R>> STORAGE = new HashMap<Locations, ArrayList<R>>(Locations.values().length + 1, 1f);

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
     * Constructor meant to be used when {@link Special_Ability_Base} are used.
     *
     * @param HAND represents the cards in hand
     * @param MAIN_DECK represents the cards in main deck.
     */
    public Game_State(final ArrayList<R> HAND, final ArrayList<R> MAIN_DECK)
    {
        this(HAND);
        MAIN_DECK.trimToSize();
        this.STORAGE.put(Locations.MAIN_DECK, MAIN_DECK);
    }

    /**
     * Constructor that is meant to be used when manually constructing this custom Object.
     */
    private Game_State()
    {
    }

    //getters
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
    /**
     * Universal getter to access underlying stored cards.
     * 
     * @param WHERE the cards one would like are located
     * 
     * @return a {@link Collection} of the requested cards, empty in the event that no cards are there
     */
    ArrayList<R> get_cards(final Locations WHERE)
    {
        return this.STORAGE.getOrDefault(WHERE, new ArrayList<R>());
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
     * {@link #set_cards(Locations, ArrayList)}
     */
    @SuppressWarnings("javadoc")
    public boolean set_cards(final Locations WHERE, final List<R> CARDS)
    {
        return this.set_cards(WHERE, new ArrayList<R>(CARDS));
    }

    @Override
    public String toString()
    {
        return "Game_State [STORAGE=" + STORAGE + "]";
    }

    /**
     * Implementation of transferring cards from one location to another in a reusable way.
     *
     * @param AMOUNT to transfer
     * @param FROM is where stuff is coming from
     * @param TO is where it is going to
     *
     * @return true for carried out and false for could not be carried out
     */
    public boolean special_ability_transfer(final int AMOUNT, final Locations FROM, final Locations TO)
    {
        final ArrayList<R> SOURCE = this.STORAGE.get(FROM);

        if (SOURCE != null && SOURCE.size() >= AMOUNT)
        {
            this.special_ability_transfer_subroutine(AMOUNT, FROM, TO);

            return true;
        }
        else
            return false;
    }

    /**
     * Creates a copy of {@link Game_State#STORAGE}'s values' containers, such that they are new (deep copy) containers, but with the same (shallow copy) values.
     * Idea is that the underlying values can be moved around, while having a copy of their original arrangement. Meant to work with {@link Game_State#reinitialize(Game_State)}.
     * 
     * @return a copy as described in this function's description.
     */
    Game_State<R> create_semi_shallow_copy()
    {
        final Game_State<R> TO_RETURN = new Game_State<R>();

        for (final Entry<Locations, ArrayList<R>> MAPPING : this.STORAGE.entrySet())
            TO_RETURN.STORAGE.put(MAPPING.getKey(), new ArrayList<R>(MAPPING.getValue()));

        return TO_RETURN;
    }

    /**
     * Essentially a reverse copy constructor. Sets internal data members to match argument. Note, such is completely shallow.
     * 
     * @param COPY_FROM is what this object will look like after this method call
     */
    void reinitialize(Game_State<R> COPY_FROM)
    {
        this.STORAGE.clear();

        for (final Entry<Locations, ArrayList<R>> MAPPING : COPY_FROM.STORAGE.entrySet())
            this.STORAGE.put(MAPPING.getKey(), MAPPING.getValue());
    }

    /**
     * Core of {@link #special_ability_transfer(int, Locations, Locations)} that bypasses checks on FROM.
     * 
     * @param AMOUNT to transfer
     * @param FROM is where stuff is coming from
     * @param TO is where it is going to
     */
    void special_ability_transfer_subroutine(final int AMOUNT, final Locations FROM, final Locations TO)
    {
        this.special_ability_transfer_subroutine(0, AMOUNT, FROM, TO);
    }

    /**
     * Core of {@link #special_ability_transfer(int, Locations, Locations)} that bypasses checks on FROM.
     * 
     * @param STARTING_INDEX of transfer
     * @param AMOUNT to transfer
     * @param FROM is where stuff is coming from
     * @param TO is where it is going to
     */
    void special_ability_transfer_subroutine(final int STARTING_INDEX, final int AMOUNT, final Locations FROM, final Locations TO)
    {
        //move cards from source to destination
        final ArrayList<R> DESTINATION = this.STORAGE.get(TO);
        final List<R> TRANSFER = this.STORAGE.get(FROM).subList(STARTING_INDEX, STARTING_INDEX + AMOUNT);

        if (DESTINATION != null)
            DESTINATION.addAll(TRANSFER);
        else
            this.set_cards(TO, TRANSFER);

        TRANSFER.clear(); //remove from source
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
     * Removes the main deck part which is unused once the a hand has been fully drawn. Should be done to reduce overall size.
     */
    public void remove_main_deck()
    {
        this.STORAGE.remove(Locations.MAIN_DECK);
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
