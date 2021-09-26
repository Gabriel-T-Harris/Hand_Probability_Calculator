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
    public final Lexeme_Types TYPE;

    /**
     * Location of token in source file.
     */
    public final long LINE_NUMBER;

    /**
     * The value of the token.
     */
    public final String LEXEM;

    /**
     * Constructor of Token.
     * 
     * @param TYPE of LEXEM
     * @param LINE_NUMBER location in source file
     * @param LEXEM content of lexem which is then trimmed by {@link String#trim()}
     */
    public Token(final Lexeme_Types TYPE, final long LINE_NUMBER, final String LEXEM)
    {
        this.TYPE = TYPE;
        this.LINE_NUMBER = LINE_NUMBER;
        this.LEXEM = LEXEM.trim();
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
     * @return the {@link #LEXEM}
     */
    public String get_lexem()
    {
        return this.LEXEM;
    }

    /*
     * String version of class.
     */
    public String toString()
    {
        return "[Lexem type: " + this.get_type() + ", lexem: " + get_lexem() + ", line number: " + this.get_line_number() + "]";
    }
}
