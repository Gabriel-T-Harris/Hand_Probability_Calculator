package parser;

/**
<b>
Purpose: Types to be used to identify token.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-23 (date extracted from {@link Token})
</b>
*/

/*SPECIAL_SEPERATOR is an enum solely to divide the properly formed tokens from the error tokens. Thus should not be used outside of this enum.
Thus < SPECIAL_SEPERATOR is properly formed, > SPECIAL_SEPERATOR is error token, and == SPECIAL_SEPERATOR is itself.*/
public enum Lexeme_Types
{
    DECK_START, ID, SEMI_COLON, PROBABILITY_START, SENTINEL_START, SENTINEL_END, ASSIGN, TREE_START, NOT, CONDITION_CARD_START, CONDITION_CARD_END, CONDITION_SCENARIO_START,
    CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END, OR, AND, XOR, DISPLAY_START, TRUE, FALSE, LINE_COMMENT, BLOCK_COMMENT, SPECIAL_SEPERATOR,
    UNKNOWN_CHARACTER_ERROR, ID_ERROR, BLOCK_COMMENT_ERROR;

    /**
     * Compares SPECIAL_SEPERATOR with INPUT. Expecting it to be -1, so that value < 0 is true.
     * 
     * @param INPUT value to be compared against, expected not to be {Lexeme_Types.SPECIAL_SEPERATOR}
     * 
     * @return comparison < 0
     */
    public static boolean is_error(final Lexeme_Types INPUT)
    {
        return Lexeme_Types.SPECIAL_SEPERATOR.compareTo(INPUT) < 0;
    }
}
