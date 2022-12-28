package simulation.special_ability;

import java.util.ArrayList;
import simulation.special_ability.Game_State.Locations;

/**
<b>
Purpose: implementation of {@link Special_Ability_Actions#REASONING}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-30/2021-12-31/2022-1-1
</b>
*/

public class Special_Ability_Reasoning implements Special_Ability_Base
{
    /**
     * Possible stopping points of the milling.
     */
    public final ArrayList<?> STOPPING_POINTS;

    /**
     * Basic constructor.
     *
     * @param STOPPING_POINTS {@link #STOPPING_POINTS}
     *
     * @throws IllegalArgumentException when STOPPING_POINTS either is null or {@link ArrayList#isEmpty()}
     */
    public Special_Ability_Reasoning(final ArrayList<?> STOPPING_POINTS)
    {
        //STOPPING_POINTS must have a value
        if (STOPPING_POINTS == null)
            throw new IllegalArgumentException("STOPPING_POINTS must not be null.");
        else if (STOPPING_POINTS.isEmpty())
            throw new IllegalArgumentException("STOPPING_POINTS must not be empty.");

        this.STOPPING_POINTS = STOPPING_POINTS;
    }

    @Override
    public boolean perform_special_ability(final Game_State<?> CURRENT_STATE)
    {
        int mill_count = 0;
        final int DECK_SIZE;
        final ArrayList<?> MAIN_DECK = CURRENT_STATE.get_cards(Locations.MAIN_DECK);
        DECK_SIZE = MAIN_DECK.size();

        while (mill_count < DECK_SIZE)
        {
            if (this.STOPPING_POINTS.contains(MAIN_DECK.get(mill_count)))
                break;
            ++mill_count;
        }

        if (mill_count < DECK_SIZE)
        {
            //mill, then summon
            CURRENT_STATE.special_ability_transfer_subroutine(mill_count, Locations.MAIN_DECK, Locations.GRAVEYARD);
            CURRENT_STATE.special_ability_transfer_subroutine(1, Locations.MAIN_DECK, Locations.FIELD);

            return true;
        }
        else
            return false;
    }
}
