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
