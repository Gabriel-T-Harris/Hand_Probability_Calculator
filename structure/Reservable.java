package structure;

import java.util.Collection;

/**
<b>
Purpose: Requirement to be a node for evaluation purposes.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-24
</b>
*/

public interface Reservable
{
    void reserve();
    void release();

    boolean isReserved();
}
