package parser;

import java.util.regex.Pattern;

/**
<b>
Purpose: Perform tokenization on input files, such that later stages may read a stream of tokens one by one.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-27
</b>
*/

public class Tokenizer
{
    /**
     * Special marker of section starts.
     */
    public final static char SENTINEL_START = '{';

    /**
     * Special marker of section end.
     */
    public final static char SENTINEL_END = '}';

    /**
     * Special marker of the start of card in expression.
     */
    public final static char CONDITION_CARD_START = '[';

    /**
     * Special marker of the end of card in expression.
     */
    public final static char CONDITION_CARD_END = ']';

    /**
     * Special marker of section starts.
     */
    public final static char CONDITION_SCENARIO_START = '<';

    /**
     * Special marker of section end.
     */
    public final static char CONDITION_SCENARIO_END = '>';

    /**
     * Special marker of the start of a subexpression.
     */
    public final static char CONDITION_EXPR_START = '(';

    /**
     * Special marker of the end of a subexpression.
     */
    public final static char CONDITION_EXPR_END = ')';

    /**
     * Representation of unary operator not.
     */
    public final static String NOT = "NOT";

    /**
     * Representation of binary operator and.
     */
    public final static String AND = "AND";

    /**
     * Representation of binary operator or.
     */
    public final static String OR = "OR";

    /**
     * Representation of binary operator xor.
     */
    public final static String XOR = "XOR";

    /**
     * Pattern indicating the definition of the DECK_START token.
     */
    public final static Pattern DECK_START = Pattern.compile("\\s*deck list:\\s*");

    /**
     * Pattern indicating the definition of the PROBABILITY_START token.
     */
    public final static Pattern PROBABILITY_START = Pattern.compile("\\s*scenarios:\\s*");

    /**
     * Pattern indicating the definition of the TREE_START token.
     */
    public final static Pattern TREE_START = Pattern.compile("\\s*scenario\\s*");

    /**
     * Pattern indicating the definition of the DISPLAY_START Token.
     */
    public final static Pattern DISPLAY_START = Pattern.compile("\\s*display\\s*");

    /**
     * Definition of the ID Token.
     */
    public final static Pattern ID = Pattern.compile("[^;=" + SENTINEL_START + SENTINEL_END + CONDITION_CARD_START + CONDITION_CARD_END + CONDITION_SCENARIO_START +
                                                     CONDITION_SCENARIO_END + CONDITION_EXPR_START + CONDITION_EXPR_END + "]+");
}
