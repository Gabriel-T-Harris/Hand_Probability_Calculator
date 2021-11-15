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
package parser;

import java.util.Scanner;

/**
<b>
Purpose: custom return type to allow returning extra data. Said extra data, when not null, is the start of the next {@link Token}.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-23 (date of extracted from {@link Tokenizer})
</b>
*/

public class Returned_Data
{
    /**
     * Data which was read but not part of the current {@link #FULLY_FORMED_PART}. It should be feed directly back into {@link Tokenizer#tokenize(long, String, Scanner)}.
     */
    public final String EXTRA_DATA;

    /**
     * Created {@link Token} from {@link Tokenizer#tokenize(long, String, Scanner)}.
     */
    public final Token FULLY_FORMED_PART;

    /**
     * @param FULLY_FORMED_PART {@link #FULLY_FORMED_PART}
     * 
     * @see #Returned_Data(String, Token)
     */
    public Returned_Data(final Token FULLY_FORMED_PART)
    {
        this(null, FULLY_FORMED_PART);
    }

    /**
     * Fully parameterized constructor.
     * 
     * @param EXTRA_DATA {@link #EXTRA_DATA}
     * @param FULLY_FORMED_PART {@link #FULLY_FORMED_PART}
     */
    public Returned_Data(final String EXTRA_DATA, final Token FULLY_FORMED_PART)
    {
        this.EXTRA_DATA = EXTRA_DATA;
        this.FULLY_FORMED_PART = FULLY_FORMED_PART;
    }
}
