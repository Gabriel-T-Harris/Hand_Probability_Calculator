package structure;

import java.util.Collection;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Alexander Herman Oxorn <br>
Date: 2021-07-29
</b>
*/

public interface Reservable
{
    void reserve();
    void release();

    boolean isReserved();
}
