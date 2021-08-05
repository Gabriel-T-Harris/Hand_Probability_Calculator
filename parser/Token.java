package parser;

/**
<b>
Purpose: Types of Tokens to be created by {@link Tokenizer}.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-26, 2021-7-30
</b>
*/

public class Token
{
    /**
     * Types to be used to identify token.
     * 
     * SPECIAL_SEPERATOR is an enum solely to divide the properly formed tokens from the error tokens. Thus should not be used outside of this enum.
     * Thus < SPECIAL_SEPERATOR is properly formed, > SPECIAL_SEPERATOR is error token, and == SPECIAL_SEPERATOR is itself.
     * 
     */
    public static enum Lexeme_Types
    {
        DECK_START, ID, SEMI_COLON, PROBABILITY_START, SENTINEL_START, SENTINEL_END, ASSIGN, TREE_START, NOT, CONDITION_CARD_START, CONDITION_CARD_END,
        CONDITION_SCENARIO_START, CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END, OR, AND, XOR, DISPLAY_START, TRUE, FALSE, LINE_COMMENT, BLOCK_COMMENT,
        SPECIAL_SEPERATOR, UNKNOWN_CHARACTER_ERROR, ID_ERROR, BLOCK_COMMENT_ERROR;

        /**
         * Compares SPECIAL_SEPERATOR with INPUT. Expecting it to be -1, so that value < 0 is true.
         * 
         * @param INPUT value to be compared against, expected not to be {Lexeme_Types.SPECIAL_SEPERATOR}
         * @return comparison < 0
         */
        public static boolean is_error(final Lexeme_Types INPUT)
        {
            return Lexeme_Types.SPECIAL_SEPERATOR.compareTo(INPUT) < 0;
        }

    }

    private final Token.Lexeme_Types TYPE;
    private final long LINE_NUMBER;
    private final String LEXEME;

    /**
     * Constructor of Token.
     * 
     * @param TYPE of LEXEME
     * @param LINE_NUMBER location in source file
     * @param LEXEME content of lexeme which is then trimmed by {@link String#trim()}
     */
    public Token(final Token.Lexeme_Types TYPE, final long LINE_NUMBER, final String LEXEME)
    {
        this.TYPE = TYPE;
        this.LINE_NUMBER = LINE_NUMBER;
        this.LEXEME = LEXEME.trim();
    }

    //getters
    /**
     * @return the LEXEME TYPE
     */
    public Token.Lexeme_Types get_type()
    {
        return this.TYPE;
    }

    /**
     * @return the LINE_NUMBER
     */
    public long get_line_number()
    {
        return this.LINE_NUMBER;
    }

    /**
     * @return the LEXEME
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
        return "[Lexeme TYPE: " + this.get_type() + ", LEXEME: " + get_lexeme() + ", line number: " + this.get_line_number() + "]";
    }
}
