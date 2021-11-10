package parser;

/**
<b>
Purpose: Types to be used to identify token.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-23 (date extracted from {@link Token})
</b>
*/

/*_SPECIAL_SEPARATOR is an enum solely to divide the properly formed tokens from the error tokens. Thus _SPECIAL_SEPARATOR should not be used outside of this enum.
Thus < _SPECIAL_SEPARATOR is properly formed, > _SPECIAL_SEPARATOR is error token, and == _SPECIAL_SEPARATOR is itself.*/
public enum Lexeme_Types
{
    DECK_START, ID, SEMI_COLON, PROBABILITY_START, SENTINEL_START, SENTINEL_END, ASSIGN, TREE_START, NOT, CONDITION_CARD_START, CONDITION_CARD_END, CONDITION_SCENARIO_START,
    CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END, OR, AND, XOR, DISPLAY_START, TRUE, FALSE, LINE_COMMENT, BLOCK_COMMENT, _SPECIAL_SEPARATOR,
    UNKNOWN_CHARACTER_ERROR, ID_ERROR, BLOCK_COMMENT_ERROR;

    /**
     * Compares _SPECIAL_SEPARATOR with INPUT. Expecting it to be -1, so that value < 0 is true.
     * 
     * @param INPUT value to be compared against, expected not to be {Lexeme_Types._SPECIAL_SEPARATOR}
     * 
     * @return comparison < 0
     */
    public static boolean is_error(final Lexeme_Types INPUT)
    {
        return Lexeme_Types._SPECIAL_SEPARATOR.compareTo(INPUT) < 0;
    }
}
