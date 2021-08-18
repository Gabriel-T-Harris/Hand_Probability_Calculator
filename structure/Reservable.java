package structure;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Alexander Herman Oxorn, Gabriel Toban Harris <br>
Date: 2021-07-29/2021-8-17
</b>
*/

public interface Reservable
{
    /**
     * Checks reservedness of this card.
     */
    boolean isReserved();

    /**
     * Reserves this card.
     */
    void reserve();

    /**
     * Frees this card.
     */
    void release();
}
