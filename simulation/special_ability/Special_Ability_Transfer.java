package simulation.special_ability;

/**
<b>
Purpose: base implementation of {@link Special_Ability_Actions} which involve transferring cards between locations<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-30/2022-1-1
</b>
*/

public abstract class Special_Ability_Transfer implements Special_Ability_Base
{
    /**
     * Number of cards to draw from deck.
     */
    public final int TRANSFER_AMOUNT;

    /**
     * Basic constructor.
     *
     * @param TRANSFER_AMOUNT {@link #TRANSFER_AMOUNT}
     */
    public Special_Ability_Transfer(final int TRANSFER_AMOUNT)
    {
        this.TRANSFER_AMOUNT = TRANSFER_AMOUNT;
    }
}
