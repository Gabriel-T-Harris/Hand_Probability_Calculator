package parser;

/**
<b>
Purpose: Semantic actions to be stored and used by {@link Tree_Assembler}}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04/2021-8-8
</b>
*/

public enum Semantic_Actions
{
    START, DECK, DECK_START, DECK_LIST, MORE_CARDS, CARD, CARD_NAME, PROBABILITY, PROBABILITY_START, SCENARIO_LIST, MORE_SCENARIOS, SCENARIO, SCENARIO_NAME, TREE, TREE_START, EXPR,
    UNARY_EXPR, UNARY_OPERATOR, PRIMARY_EXPR, CONDITION_CARD_START, CONDITION_CARD_END, CONDITION_SCENARIO_START, CONDITION_SCENARIO_END, CONDITION_EXPR_START, CONDITION_EXPR_END,
    BINARY_EXPR, BINARY_OPERATOR, DISPLAY, DISPLAY_START, DISPLAY_VALUE, SENTINEL_START, SENTINEL_END, SEMI_COLON, ASSIGN, DECK_CARD_POP, SCENARIO_POP, CONDITION_CARD_POP,
    CONDITION_SCENARIO_POP,
    BINARY_POP_3,
}
