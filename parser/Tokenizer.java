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

import java.util.Scanner;
import java.util.regex.Pattern;

/**
<b>
Purpose: Perform tokenization on input files, such that later stages may read a stream of tokens one by one.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-[27, 28], 2021-7-30/2021-8-9/2021-8-23/2022-6-3
</b>
*/

public class Tokenizer
{
    /**
     * Special marker of section starts. Definition of {@link Lexeme_Types#SENTINEL_START} {@link Token}.
     */
    public final static String SENTINEL_START = "{";

    /**
     * Special marker of section end. Definition of {@link Lexeme_Types#SENTINEL_END} {@link Token}.
     */
    public final static String SENTINEL_END = "}";

    /**
     * Special marker of the start of a hand card in expression. Definition of {@link Lexeme_Types#CONDITION_HAND_CARD_START} {@link Token}.
     */
    public final static String CONDITION_HAND_CARD_START = "[";

    /**
     * Special marker of the end of a hand card in expression. Definition of {@link Lexeme_Types#CONDITION_HAND_CARD_END} {@link Token}.
     */
    public final static String CONDITION_HAND_CARD_END = "]";

    /**
     * Special marker of section starts. Definition of {@link Lexeme_Types#CONDITION_SCENARIO_START} {@link Token}.
     */
    public final static String CONDITION_SCENARIO_START = "<";

    /**
     * Special marker of section end. Definition of {@link Lexeme_Types#CONDITION_SCENARIO_END} {@link Token}.
     */
    public final static String CONDITION_SCENARIO_END = ">";

    /**
     * Special marker of the start of a subexpression. Definition of {@link Lexeme_Types#CONDITION_EXPR_START} {@link Token}.
     */
    public final static String CONDITION_EXPR_START = "(";

    /**
     * Special marker of the end of a subexpression. Definition of {@link Lexeme_Types#CONDITION_EXPR_END} {@link Token}.
     */
    public final static String CONDITION_EXPR_END = ")";

    /**
     * Special marker used in combination with others to identify parts. Definition of {@link Lexeme_Types#ASSIGN} {@link Token}.
     */
    public final static String ASSIGN = "=";

    /**
     * Special marker of the end of various parts. Definition of {@link Lexeme_Types#SEMI_COLON} {@link Token}.
     */
    public final static String SEMI_COLON = ";";

    /**
     * Special marker of both the start and end of a field card in an expression. Definition of {@link Lexeme_Types#CONDITION_FIELD_CARD} {@link Token}.
     */
    public final static String CONDITION_FIELD_CARD = "*";

    /**
     * Special marker of both the start and end of a graveyard card in an expression. Definition of {@link Lexeme_Types#CONDITION_GY_CARD} {@link Token}.
     */
    public final static String CONDITION_GY_CARD = "_";

    /**
     * Special marker of both the start and end of a banished card in an expression. Definition of {@link Lexeme_Types#CONDITION_BANISH_CARD} {@link Token}.
     */
    public final static String CONDITION_BANISH_CARD = "~";

    /**
     * Simple concatenation of chars which are not allowed to be a part of any keyword, ID, or special marker of a part.
     */
    public final static String RESTRICTED_CHARS = "\n" + Tokenizer.SEMI_COLON + Tokenizer.ASSIGN + Tokenizer.SENTINEL_START + Tokenizer.SENTINEL_END +
                                                  Tokenizer.CONDITION_HAND_CARD_START + Tokenizer.CONDITION_HAND_CARD_END + Tokenizer.CONDITION_SCENARIO_START +
                                                  Tokenizer.CONDITION_SCENARIO_END + Tokenizer.CONDITION_EXPR_START + Tokenizer.CONDITION_EXPR_END +
                                                  Tokenizer.CONDITION_FIELD_CARD + Tokenizer.CONDITION_GY_CARD + Tokenizer.CONDITION_BANISH_CARD;

    /**
     * Representation of unary operator not. Definition of {@link Lexeme_Types#NOT} {@link Token}.
     */
    public final static Pattern NOT = Pattern.compile("\\s*NOT\\s*");

    /**
     * Representation of binary operator and. Definition of {@link Lexeme_Types#AND} {@link Token}.
     */
    public final static Pattern AND = Pattern.compile("\\s*AND\\s*");

    /**
     * Representation of binary operator or. Definition of {@link Lexeme_Types#OR} {@link Token}.
     */
    public final static Pattern OR = Pattern.compile("\\s*OR\\s*");

    /**
     * Representation of binary operator xor. Definition of {@link Lexeme_Types#XOR} {@link Token}.
     */
    public final static Pattern XOR = Pattern.compile("\\s*XOR\\s*");

    /**
     * Simply the the predefined class \s.
     */
    public final static Pattern WHITE_SPACE_CHAR = Pattern.compile("\\s");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#DECK_START} {@link Token}.
     */
    public final static Pattern DECK_START = Pattern.compile("\\s*decklist:\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#PROBABILITY_START} {@link Token}.
     */
    public final static Pattern PROBABILITY_START = Pattern.compile("\\s*scenarios:\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#TREE_START} {@link Token}.
     */
    public final static Pattern TREE_START = Pattern.compile("\\s*scenario\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#DISPLAY_START} {@link Token}.
     */
    public final static Pattern DISPLAY_START = Pattern.compile("\\s*display\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#TRUE} {@link Token}.
     */
    public final static Pattern TRUE = Pattern.compile("\\s*true\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#FALSE} {@link Token}.
     */
    public final static Pattern FALSE = Pattern.compile("\\s*false\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#NON_NEGATIVE_INTEGER} {@link Token}.
     */
    public final static Pattern NON_NEGATIVE_INTEGER = Pattern.compile("[0-9]+");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#DRAW} {@link Token}.
     */
    public final static Pattern DRAW = Pattern.compile("\\s*DRAW\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#MILL} {@link Token}.
     */
    public final static Pattern MILL = Pattern.compile("\\s*MILL\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#BANISH} {@link Token}.
     */
    public final static Pattern BANISH = Pattern.compile("\\s*BANISH\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#REASONING} {@link Token}.
     */
    public final static Pattern REASONING = Pattern.compile("\\s*REASONING\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#COMBINATORIC} {@link Token}.
     */
    public final static Pattern COMBINATORIC = Pattern.compile("\\s*COMBINATORIC\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#USES} {@link Token}.
     */
    public final static Pattern USES = Pattern.compile("\\s*uses\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#SPECIAL_ABILITY_START} {@link Token}.
     */
    public final static Pattern SPECIAL_ABILITY_START = Pattern.compile("\\s*special_abilities:\\s*");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#SPECIAL_ABILITY_BODY_START} {@link Token}.
     */
    public final static Pattern SPECIAL_ABILITY_BODY_START = Pattern.compile("\\s*special_ability\\s*");

    /**
     * Definition of valid char that an ID lexeme can have.
     */
    public final static Pattern ID_CHAR_SET = Pattern.compile("[^" + Tokenizer.RESTRICTED_CHARS.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]") +
                                                              "]");

    /**
     * Performs tokenization, after which other functions are called internally.
     * 
     * @param LINE_NUMBER of source file being read
     * @param START starting string, expected to be length 1
     * @param INPUT source to be read from
     * 
     * @return newly created {@link Returned_Data}
     */
    public static Returned_Data tokenize(final long LINE_NUMBER, final String START, final Scanner INPUT)
    {
        final StringBuilder LEXEME = new StringBuilder(32); //32 feels like a good starting number, possibly go up to 256 (byte) as that should be a limit.
        LEXEME.append(START);

        switch (START)
        {
            //deal with single char tokens first
            case Tokenizer.SENTINEL_START:
                return new Returned_Data(new Token(Lexeme_Types.SENTINEL_START, LINE_NUMBER, START));
            case Tokenizer.SENTINEL_END:
                return new Returned_Data(new Token(Lexeme_Types.SENTINEL_END, LINE_NUMBER, START));
            case Tokenizer.CONDITION_HAND_CARD_START:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_HAND_CARD_START, LINE_NUMBER, START));
            case Tokenizer.CONDITION_HAND_CARD_END:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_HAND_CARD_END, LINE_NUMBER, START));
            case Tokenizer.CONDITION_SCENARIO_START:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_SCENARIO_START, LINE_NUMBER, START));
            case Tokenizer.CONDITION_SCENARIO_END:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_SCENARIO_END, LINE_NUMBER, START));
            case Tokenizer.CONDITION_EXPR_START:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_EXPR_START, LINE_NUMBER, START));
            case Tokenizer.CONDITION_EXPR_END:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_EXPR_END, LINE_NUMBER, START));
            case Tokenizer.ASSIGN:
                return new Returned_Data(new Token(Lexeme_Types.ASSIGN, LINE_NUMBER, START));
            case Tokenizer.SEMI_COLON:
                return new Returned_Data(new Token(Lexeme_Types.SEMI_COLON, LINE_NUMBER, START));
            case Tokenizer.CONDITION_FIELD_CARD:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_FIELD_CARD, LINE_NUMBER, START));
            case Tokenizer.CONDITION_GY_CARD:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_GY_CARD, LINE_NUMBER, START));
            case Tokenizer.CONDITION_BANISH_CARD:
                return new Returned_Data(new Token(Lexeme_Types.CONDITION_BANISH_CARD, LINE_NUMBER, START));
            //comments
            case "/":
            {
                if (INPUT.hasNext())
                {
                    String placeholder = INPUT.next();
                    LEXEME.append(placeholder);

                    //line comment
                    if (placeholder.equals("/"))
                    {
                        if (INPUT.hasNext())
                            LEXEME.append(INPUT.nextLine());

                        return new Returned_Data(new Token(Lexeme_Types.LINE_COMMENT, LINE_NUMBER, LEXEME.toString()));
                    }
                    //block comment
                    else if (placeholder.equals("*"))
                    {
                        while (INPUT.hasNext())
                        {
                            LEXEME.append(placeholder = INPUT.next());

                            if (placeholder.equals("*"))
                            {
                                if (INPUT.hasNext())
                                {
                                    LEXEME.append(placeholder = INPUT.next());

                                    if (placeholder.equals("/"))
                                        return new Returned_Data(new Token(Lexeme_Types.BLOCK_COMMENT, LINE_NUMBER, LEXEME.toString()));
                                }
                                else
                                    break;
                            }
                        }

                        //block comment lacking closing symbols
                        return new Returned_Data(new Token(Lexeme_Types.BLOCK_COMMENT_ERROR, LINE_NUMBER, LEXEME.toString()));
                    }
                }

                //defer to ID_CHAR_SET
                return new Returned_Data(new Token(Lexeme_Types.ID, LINE_NUMBER, LEXEME.toString()));
            }
            //parse multichar sequences
            default:
                return Tokenizer.gather_keyword_chars(LINE_NUMBER, LEXEME, INPUT);
        }
    }

    /**
     * Function to attempt to find keywords, other wise defers to {@link #gather_ID_chars(long, StringBuilder, Scanner)}
     * 
     * @param LINE_NUMBER of source file being read
     * @param LEXEME_START is the lexeme formed thus far
     * @param INPUT source to be read from
     * 
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data gather_keyword_chars(final long LINE_NUMBER, final StringBuilder LEXEME_START, final Scanner INPUT)
    {
        boolean has_white_space_char; //to avoid having to repeat regex evaluation
        String placeholder;

        while (INPUT.hasNext())
        {
            placeholder = INPUT.next();

            //check for either keyword end or restricted char
            if ((has_white_space_char = Tokenizer.WHITE_SPACE_CHAR.matcher(placeholder).matches()) || Tokenizer.RESTRICTED_CHARS.contains(placeholder))
            {
                final String LEXEME = LEXEME_START.toString();

                if (Tokenizer.AND.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.AND, LINE_NUMBER, LEXEME));
                else if (Tokenizer.OR.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.OR, LINE_NUMBER, LEXEME));
                else if (Tokenizer.NOT.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.NOT, LINE_NUMBER, LEXEME));
                else if (Tokenizer.XOR.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.XOR, LINE_NUMBER, LEXEME));
                else if (Tokenizer.TREE_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.TREE_START, LINE_NUMBER, LEXEME));
                else if (Tokenizer.DISPLAY_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.DISPLAY_START, LINE_NUMBER, LEXEME));
                else if (Tokenizer.FALSE.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.FALSE, LINE_NUMBER, LEXEME));
                else if (Tokenizer.TRUE.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.TRUE, LINE_NUMBER, LEXEME));
                else if (Tokenizer.PROBABILITY_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.PROBABILITY_START, LINE_NUMBER, LEXEME));
                else if (Tokenizer.DECK_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.DECK_START, LINE_NUMBER, LEXEME));
                else if (Tokenizer.NON_NEGATIVE_INTEGER.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.NON_NEGATIVE_INTEGER, LINE_NUMBER, LEXEME));
                else if (Tokenizer.COMBINATORIC.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.COMBINATORIC, LINE_NUMBER, LEXEME));
                else if (Tokenizer.USES.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.USES, LINE_NUMBER, LEXEME));
                else if (Tokenizer.SPECIAL_ABILITY_BODY_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.SPECIAL_ABILITY_BODY_START, LINE_NUMBER, LEXEME));
                else if (Tokenizer.DRAW.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.DRAW, LINE_NUMBER, LEXEME));
                else if (Tokenizer.MILL.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.MILL, LINE_NUMBER, LEXEME));
                else if (Tokenizer.BANISH.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.BANISH, LINE_NUMBER, LEXEME));
                else if (Tokenizer.REASONING.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.REASONING, LINE_NUMBER, LEXEME));
                else if (Tokenizer.SPECIAL_ABILITY_START.matcher(LEXEME).matches())
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.SPECIAL_ABILITY_START, LINE_NUMBER, LEXEME));
                else if (has_white_space_char)
                    return Tokenizer.gather_ID_chars(LINE_NUMBER, LEXEME_START.append(placeholder), INPUT);
                else
                    return new Returned_Data(placeholder, new Token(Lexeme_Types.ID, LINE_NUMBER, LEXEME));
            }
            //keep building
            else
                LEXEME_START.append(placeholder);
        }

        return new Returned_Data(new Token(Lexeme_Types.ID, LINE_NUMBER, LEXEME_START.toString()));
    }

    /**
     * Subroutine to obtain a sequence which may be an ID.
     * 
     * @param LINE_NUMBER of source file being read
     * @param LEXEME_START is the lexeme formed thus far
     * @param INPUT source to be read from
     * 
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data gather_ID_chars(final long LINE_NUMBER, final StringBuilder LEXEME_START, final Scanner INPUT)
    {
        String placeholder;

        while (INPUT.hasNext())
        {
            placeholder = INPUT.next();

            if (Tokenizer.ID_CHAR_SET.matcher(placeholder).matches())
                LEXEME_START.append(placeholder);
            else
                return Tokenizer.parse_potential_ID(LINE_NUMBER, placeholder, LEXEME_START.toString());
        }

        return Tokenizer.parse_potential_ID(LINE_NUMBER, null, LEXEME_START.toString());
    }

    /**
     * Determines which {@link Token} should be formed given the lexeme provided. For lexeme which may contain {@link #WHITE_SPACE_CHAR}.
     * 
     * @param LINE_NUMBER of source file being read
     * @param REMAINDER which is the extra data that should be feed back into {@link #tokenize(long, String, Scanner)}
     * @param COMPLETE_LEXEME which is fully formed
     * 
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data parse_potential_ID(final long LINE_NUMBER, final String REMAINDER, final String COMPLETE_LEXEME)
    {
        //Test lexeme for special sequences, if all fail then is in fact ID. No such sequences currently exist.
        return new Returned_Data(REMAINDER, new Token(Lexeme_Types.ID, LINE_NUMBER, COMPLETE_LEXEME));
    }
}
