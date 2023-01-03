/*
    Copyright (C) 2022 Gabriel Toban Harris

        This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

        This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package simulation.special_ability;

import java.util.ArrayList;
import java.util.HashMap;
import simulation.special_ability.Game_State.Locations;
import structure.Reservable;

/**
<b>
Purpose: manages all special abilities<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-25/2022-1-1/2022-6-18/2022-12-[26, 27]/2023-1-1
</b>
*/

public class Special_Ability_Manager
{
    /**
     * Simple object to hold data for {@link Special_Ability_Manager#super_powers}.
     */
    protected class Card_Effects
    {
        /**
         * Represents the maximum number of times this ability can be used.
         */
        public final int ACTIVATION_LIMIT;

        /**
         * Number of times this card has been used in current {@link Special_Ability_Manager#parse(Game_State)}
         */
        public int times_used = 0;

        /**
         * In order sequence of effects to be carried out for this card.
         */
        public final Special_Ability_Base[] ACTIONS;

        /**
         * Basic constructor. {@link Card_Effects#ACTIVATION_LIMIT} set to max value.
         * 
         * @param ACTIONS {@link Card_Effects#ACTIONS}
         */
        public Card_Effects(final Special_Ability_Base[] ACTIONS)
        {
            this(Integer.MAX_VALUE, ACTIONS); //While a more complex thingy would allow truly infinite uses, {@link Integer#MAX_VALUE} is good enough.
        }

        /**
         * Parameterized constructor.
         *
         * @param MAX_USES {@link Card_Effects#ACTIVATION_LIMIT}, note that 0 is treated as {@link Integer#MAX_VALUE}
         * @param ACTIONS {@link Card_Effects#ACTIONS}
         * 
         * @throws IllegalArgumentException if ACTIONS is empty
         */
        public Card_Effects(final int MAX_USES, final Special_Ability_Base[] ACTIONS)
        {
            if (ACTIONS.length < 1)
                throw new IllegalArgumentException("Error: ACTIONS must not be empty.");

            this.ACTIVATION_LIMIT = (MAX_USES != 0) ? MAX_USES : Integer.MAX_VALUE; //treat 0 as unlimited
            this.ACTIONS = ACTIONS;
        }

        /**
         * To reset all parts which require resetting between calls to {@link #parse(Game_State)}
         */
        public void reset()
        {
            this.times_used = 0;
        }
    }

    /**
     * Stores defined {@link Special_Ability_Base}. Also yes the name is a joke.
     */
    protected HashMap<String, Card_Effects> super_powers = new HashMap<String, Card_Effects>();

    /**
     * Adds card to be both managed and used.
     * 
     * @param NAME of card
     * @param EFFECT of the card
     * 
     * @return true for added and false for not added (likely due to NAME having already been used)
     */
    public boolean add(final String NAME, final Special_Ability_Base EFFECT)
    {
        return this.add(NAME, new Special_Ability_Base[]{EFFECT});
    }

    /**
     * Adds card to be both managed and used.
     * 
     * @param NAME of card
     * @param EFFECTS in order of card
     * 
     * @return true for added and false for not added (likely due to NAME having already been used)
     */
    public boolean add(final String NAME, final Special_Ability_Base[] EFFECTS)
    {
        return this.super_powers.putIfAbsent(NAME, new Card_Effects(EFFECTS)) == null;
    }

    /**
     * Adds card to be both managed and used.
     *
     * @param ACTIVATION_LIMIT is the number of times a card can be used per {@link #parse(Game_State)}
     * @param NAME of card
     * @param EFFECT of the card card
     * 
     * @return true for added and false for not added (likely due to NAME having already been used)
     */
    public boolean add(final int ACTIVATION_LIMIT, final String NAME, final Special_Ability_Base EFFECT)
    {
        return this.add(ACTIVATION_LIMIT, NAME, new Special_Ability_Base[]{EFFECT});
    }

    /**
     * Adds card to be both managed and used.
     *
     * @param ACTIVATION_LIMIT is the number of times a card can be used per {@link #parse(Game_State)}
     * @param NAME of card
     * @param EFFECTS in order of card
     * 
     * @return true for added and false for not added (likely due to NAME having already been used)
     */
    public boolean add(final int ACTIVATION_LIMIT, final String NAME, final Special_Ability_Base[] EFFECTS)
    {
        return this.super_powers.putIfAbsent(NAME, new Card_Effects(ACTIVATION_LIMIT, EFFECTS)) == null;
    }

    /**
     * Simply indicates the number special abilities that are being managed.
     * 
     * @return an integer representing the number of special abilities stored
     */
    public int special_ability_count()
    {
        return this.super_powers.values().size();
    }

    /**
     * Handle all special abilities. Should not be called multiple times one the same {@link Game_State}.
     *
     * @param <R> {@link Reservable}
     * 
     * @param INPUT which is analyzed to then alter it, such that the result is all special abilities having been carried out 
     */
    public <R extends Reservable> void parse(final Game_State<R> INPUT)
    {
        int hand_size = INPUT.get_unmodifiable_cards(Locations.HAND).size();

        //ensure there is a hand to work on
        if (hand_size < 1)
            return;

        {
            boolean legal_card_activation = false;
            int hand_index = 0;
            String key;
            Card_Effects placeholder;
            Game_State<R> restoration_point; //note state before carrying out abilities in case a rewind is required
            ArrayList<R> hand_reference = INPUT.get_cards(Locations.HAND);

            do
            {
                if (this.super_powers.containsKey(key = hand_reference.get(hand_index).get_name()) &&
                    (placeholder = this.super_powers.get(key)).times_used < placeholder.ACTIVATION_LIMIT)
                {
                    if (placeholder.ACTIONS.length > 1)
                    {
                        restoration_point = INPUT.create_semi_shallow_copy();

                        //carry out all effects
                        for (final Special_Ability_Base EFFECT : placeholder.ACTIONS)
                            if (!(legal_card_activation = EFFECT.perform_special_ability(INPUT)))
                            {
                                INPUT.reinitialize(restoration_point); //revert upon failure
                                break; // stop upon failure
                            }
                    }
                    else
                        legal_card_activation = placeholder.ACTIONS[0].perform_special_ability(INPUT);

                    //Check that card was used successfully and then act accordingly.
                    if (legal_card_activation)
                    {
                        //move card away
                        INPUT.special_ability_transfer_subroutine(hand_index, 1, Locations.HAND, Locations.GRAVEYARD); //For now hard code to go to Locations.GRAVEYARD, later maybe have it be definable.
                        ++placeholder.times_used; //update counter

                        hand_size = hand_reference.size(); //in case hand size changed
                    }
                    else
                        ++hand_index;
                }
                else
                    ++hand_index;
            } while (hand_index < hand_size);
        }

        //clean up
        INPUT.remove_main_deck();
        this.reset_all();
    }

    /**
     * Resets number of times each card has been used. 
     */
    private void reset_all()
    {
        this.super_powers.values().forEach(Card_Effects::reset);
    }
}
