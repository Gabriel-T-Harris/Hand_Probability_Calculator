package simulation.special_ability;

import simulation.special_ability.Game_State.Locations;

/**
<b>
Purpose: implementation of {@link Special_Ability_Actions#DRAW}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-30/2022-1-1
</b>
*/

public class Special_Ability_Draw extends Special_Ability_Transfer
{

    /**
     * Basic constructor.
     *
     * @param TRANSFER_AMOUNT {@link Special_Ability_Transfer#TRANSFER_AMOUNT}
     */
    public Special_Ability_Draw(final int TRANSFER_AMOUNT)
    {
        super(TRANSFER_AMOUNT);
    }

    @Override
    public boolean perform_special_ability(final Game_State<?> CURRENT_STATE)
    {
        return CURRENT_STATE.special_ability_transfer(this.TRANSFER_AMOUNT, Locations.MAIN_DECK, Locations.HAND);
    }
}
