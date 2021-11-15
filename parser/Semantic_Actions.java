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
Purpose: Semantic actions to be stored and used by {@link Tree_Assembler}}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04/2021-8-8/2021-8-10
</b>
*/

public enum Semantic_Actions
{
    START, DECK, DECK_START, DECK_LIST, MORE_CARDS, CARD, CARD_NAME, PROBABILITY, PROBABILITY_START, SCENARIO_LIST, MORE_SCENARIOS, SCENARIO, SCENARIO_NAME, TREE, TREE_START, EXPR,
    UNARY_EXPR, UNARY_OPERATOR, PRIMARY_EXPR, CONDITION_CARD_START, CONDITION_CARD_END, CONDITION_SCENARIO_START, CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END,
    BINARY_EXPR, BINARY_OPERATOR, DISPLAY, DISPLAY_START, DISPLAY_VALUE, SENTINEL_START, SENTINEL_END, SEMI_COLON, ASSIGN, DECK_CARD_POP, SCENARIO_POP, UNARY_POP,
    CONDITION_CARD_POP, CONDITION_SCENARIO_POP, BINARY_POP_3;
}
