package structure;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Alexander Herman Oxorn <br>
Date: 2021-07-29
</b>
*/

public interface Reservable
{

    /**
     * States the reservedness of a card.
     * @return true for reserved and false for not reserved.
     */
    boolean isReserved();

    /**
     * Reserves a card.
     */
    void reserve();

    /**
     * Unreserves a card.
     */
    void release();
}
