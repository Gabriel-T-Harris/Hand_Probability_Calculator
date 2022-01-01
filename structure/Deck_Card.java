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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
<b>
Purpose: To be used as the representation of cards in a deck.<br>
Programmer: Gabriel Toban Harris, Alexander Oxorn
</b>
*/

public class Deck_Card extends Base_Card implements Reservable
{
    /**
     * For routines declared in {@link Reservable}
     */
    protected boolean reserved;

    /**
     * See {@link Base_Card#Base_Card(String)}
     */
    public Deck_Card(final String NAME)
    {
        super(NAME);
    }

    /**
     * Copy constructor.
     * 
     * @param INPUT to be copied.
     */
    public Deck_Card(final Deck_Card INPUT)
    {
        super(INPUT);
        this.reserved = false;
    }

    /**
     * Makes a deep copy of a container. 
     * 
     * @param <E> incoming collection type
     * 
     * @param INPUT container of {@link Deck_Card} to be duplicated.
     * 
     * @return the deep copied values wrapped in an {@link ArrayList}
     */
    public static <E extends Collection<Deck_Card>> ArrayList<Deck_Card> deep_copy(final E INPUT)
    {
        return INPUT.stream().map(Deck_Card::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean isReserved() {
        return reserved;
    }

    @Override
    public String get_name()
    {
        return this.NAME;
    }

    @Override
    public void reserve() {
        reserved = true;
    }

    @Override
    public void release() {
        reserved = false;
    }
}
