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
Purpose: Types to be used to identify token.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-23 (date extracted from {@link Token})/2022-6-3
</b>
*/

/*_SPECIAL_SEPARATOR is an enum solely to divide the properly formed tokens from the error tokens. Thus _SPECIAL_SEPARATOR should not be used outside of this enum.
Thus < _SPECIAL_SEPARATOR is properly formed, > _SPECIAL_SEPARATOR is error token, and == _SPECIAL_SEPARATOR is itself.*/
public enum Lexeme_Types
{
    DECK_START, ID, SEMI_COLON, PROBABILITY_START, SENTINEL_START, SENTINEL_END, ASSIGN, TREE_START, NOT, CONDITION_HAND_CARD_START, CONDITION_HAND_CARD_END, CONDITION_SCENARIO_START,
    CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END, OR, AND, XOR, DISPLAY_START, TRUE, FALSE, LINE_COMMENT, BLOCK_COMMENT, NON_NEGATIVE_INTEGER, DRAW, MILL, BANISH, REASONING,
    COMBINATORIC, SPECIAL_ABILITY_START, ACTIVATION_LIMITATION_START, CONDITION_FIELD_CARD, CONDITION_GY_CARD, CONDITION_BANISH_CARD, SPECIAL_ABILITY_BODY_START,
    _SPECIAL_SEPARATOR, UNKNOWN_CHARACTER_ERROR, ID_ERROR, BLOCK_COMMENT_ERROR;

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
