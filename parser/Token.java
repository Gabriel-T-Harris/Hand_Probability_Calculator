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

/**
<b>
Purpose: Types of Tokens to be created by {@link Tokenizer}.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-26/2021-7-30/2021-8-23
</b>
*/

public class Token
{
    /**
     * Type of Token this is.
     */
    private final Lexeme_Types TYPE;

    /**
     * Location of token in source file.
     */
    private final long LINE_NUMBER;

    /**
     * The value of the token.
     */
    private final String LEXEME;

    /**
     * Constructor of Token.
     * 
     * @param TYPE of LEXEME
     * @param LINE_NUMBER location in source file
     * @param LEXEME content of lexeme which is then trimmed by {@link String#trim()}
     */
    public Token(final Lexeme_Types TYPE, final long LINE_NUMBER, final String LEXEME)
    {
        this.TYPE = TYPE;
        this.LINE_NUMBER = LINE_NUMBER;
        this.LEXEME = LEXEME.trim();
    }

    //getters
    /**
     * @return the {@link #TYPE}
     */
    public Lexeme_Types get_type()
    {
        return this.TYPE;
    }

    /**
     * @return the {@link #LINE_NUMBER}
     */
    public long get_line_number()
    {
        return this.LINE_NUMBER;
    }

    /**
     * @return the {@link #LEXEME}
     */
    public String get_lexeme()
    {
        return this.LEXEME;
    }

    /*
     * String version of class.
     */
    public String toString()
    {
        return "[Lexeme type: " + this.get_type() + ", lexeme: " + this.get_lexeme() + ", line number: " + this.get_line_number() + "]";
    }
}
