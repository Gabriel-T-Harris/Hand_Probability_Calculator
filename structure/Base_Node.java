/*
    Copyright (C) 2021 Gabriel Toban Harris

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
package structure;

import java.util.Collection;

/**
<b>
Purpose: To be the base node which others will extend.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-23/2021-8-3/2021-8-8/2021-8-10/2021-8-19
</b>
*/

public abstract class Base_Node extends Evaluable
{
    /**
     * Constructor of {@link Base_Node}.
     * 
     * @param NAME the name to be given to this {@link Base_Node}
     */
    public Base_Node(final String NAME)
    {
        super(NAME);
    }

    /**
     * for dot format
     */
    public String toString()
    {
        return this.UNIQUE_IDENTIFIER + "[label=\"" + this.NAME.replace(">", "\\>").replace("<", "\\<").replace("\"", "\\\"") + "\"];\n";
    }

    @Override
    protected Collection<? extends Evaluable> continue_breath_search()
    {
        return null;
    }
}
