package parser;

import java.util.Scanner;
import java.util.regex.Pattern;
import parser.Token.Lexeme_Types;

/**
<b>
Purpose: Perform tokenization on input files, such that later stages may read a stream of tokens one by one.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-[27, 28], 2021-7-30
</b>
*/

public class Tokenizer
{
    /**
     * Inner class representing custom return type to allow returning extra data. Like a pair.
     * Said extra data, when not null, is the start of the next {@link Token}.
     */
    public static class Returned_Data
    {
        /**
         * Data which was read but not part of the current {@link #FULLY_FORMED_PART}. It should be feed directly back into {@link Tokenizer#tokenize(long, String, Scanner)}.
         */
        public String extra_data;

        /**
         * Created {@link Token} from {@link Tokenizer#tokenize(long, String, Scanner)}.
         */
        public final Token FULLY_FORMED_PART;

        /**
         * @see #Returned_Data(String, Token)
         */
        public Returned_Data(final Token FULLY_FORMED_PART)
        {
            this(null, FULLY_FORMED_PART);
        }

        /**
         * Fully parameterized constructor.
         * 
         * @param EXTRA_DATA {@link #extra_data}
         * @param FULLY_FORMED_PART {@link #FULLY_FORMED_PART}
         */
        public Returned_Data(final String EXTRA_DATA, final Token FULLY_FORMED_PART)
        {
            this.extra_data = EXTRA_DATA;
            this.FULLY_FORMED_PART = FULLY_FORMED_PART;
        }
    }

    /**
     * Special marker of section starts.
     */
    public final static String SENTINEL_START = "{";

    /**
     * Special marker of section end.
     */
    public final static String SENTINEL_END = "}";

    /**
     * Special marker of the start of card in expression.
     */
    public final static String CONDITION_CARD_START = "[";

    /**
     * Special marker of the end of card in expression.
     */
    public final static String CONDITION_CARD_END = "]";

    /**
     * Special marker of section starts.
     */
    public final static String CONDITION_SCENARIO_START = "<";

    /**
     * Special marker of section end.
     */
    public final static String CONDITION_SCENARIO_END = ">";

    /**
     * Special marker of the start of a subexpression.
     */
    public final static String CONDITION_EXPR_START = "(";

    /**
     * Special marker of the end of a subexpression.
     */
    public final static String CONDITION_EXPR_END = ")";

    /**
     * Simple concatenation of chars which are not allowed to be a part of any keyword, ID, or special marker of a part.
     */
    public final static String RESTRICTED_CHARS = ";=\n" + SENTINEL_START + SENTINEL_END + CONDITION_CARD_START + CONDITION_CARD_END + CONDITION_SCENARIO_START +
                                                  CONDITION_SCENARIO_END + CONDITION_EXPR_START + CONDITION_EXPR_END;

    /**
     * Representation of unary operator not.
     */
    public final static Pattern NOT = Pattern.compile("\\s*NOT\\s*");

    /**
     * Representation of binary operator and.
     */
    public final static Pattern AND = Pattern.compile("\\s*AND\\s*");

    /**
     * Representation of binary operator or.
     */
    public final static Pattern OR = Pattern.compile("\\s*OR\\s*");

    /**
     * Representation of binary operator xor.
     */
    public final static Pattern XOR = Pattern.compile("\\s*XOR\\s*");

    /**
     * Simply the the predefined class \s.
     */
    public final static Pattern WHITE_SPACE_CHAR = Pattern.compile("\\s");

    /**
     * Pattern indicating the definition of the {@link Lexeme_Types#DECK_START} {@link Token}.
     */
    public final static Pattern DECK_START = Pattern.compile("\\s*deck list:\\s*");

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
     * Definition of valid char that an the ID lexeme can have.
     */
    public final static Pattern ID_CHAR_SET = Pattern.compile("[^" + RESTRICTED_CHARS.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]") + "]");

    /**
     * Performs tokenization, after which other functions are called internally.
     * 
     * @param LINE_NUMBER of source file being read
     * @param START starting string, expected to be length 1
     * @param INPUT source to be read from
     * @return newly created {@link Returned_Data} object
     */
    public static Returned_Data tokenize(final long LINE_NUMBER, final String START, final Scanner INPUT)
    {
        final StringBuilder LEXEME = new StringBuilder(32); //32 feels like a good starting number, possibly go up to 256 (byte) as that should be a limit.
        LEXEME.append(START);

        //deal with 1 char lexeme first
        switch (START)
        {
            //deal with single char tokens first
            case SENTINEL_START:
                return new Returned_Data(new Token(Token.Lexeme_Types.SENTINEL_START, LINE_NUMBER, START));
            case SENTINEL_END:
                return new Returned_Data(new Token(Token.Lexeme_Types.SENTINEL_END, LINE_NUMBER, START));
            case CONDITION_CARD_START:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_CARD_START, LINE_NUMBER, START));
            case CONDITION_CARD_END:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_CARD_END, LINE_NUMBER, START));
            case CONDITION_SCENARIO_START:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_SCENARIO_START, LINE_NUMBER, START));
            case CONDITION_SCENARIO_END:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_SCENARIO_END, LINE_NUMBER, START));
            case CONDITION_EXPR_START:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_EXPR_START, LINE_NUMBER, START));
            case CONDITION_EXPR_END:
                return new Returned_Data(new Token(Token.Lexeme_Types.CONDITION_EXPR_END, LINE_NUMBER, START));
            case ";":
                return new Returned_Data(new Token(Token.Lexeme_Types.SEMI_COLON, LINE_NUMBER, START));
            case "=":
                return new Returned_Data(new Token(Token.Lexeme_Types.ASSIGN, LINE_NUMBER, START));
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

                        return new Returned_Data(new Token(Token.Lexeme_Types.LINE_COMMENT, LINE_NUMBER, LEXEME.toString()));
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
                                        return new Returned_Data(new Token(Token.Lexeme_Types.BLOCK_COMMENT, LINE_NUMBER, LEXEME.toString()));
                                }
                                else
                                    break;
                            }
                        }

                        //block comment lacking closing symbols
                        return new Returned_Data(new Token(Token.Lexeme_Types.BLOCK_COMMENT_ERROR, LINE_NUMBER, LEXEME.toString()));
                    }
                }

                //defer to ID_CHAR_SET
                return new Returned_Data(new Token(Token.Lexeme_Types.ID, LINE_NUMBER, LEXEME.toString()));
            }
            //parse multichar sequences
            default:
                return gather_keyword_chars(LINE_NUMBER, LEXEME, INPUT);
        }
    }

    /**
     * Function to attempt to find keywords, other wise defers to {@link #gather_ID_chars(long, StringBuilder, Scanner)}
     * 
     * @param LINE_NUMBER of source file being read
     * @param LEXEME_START is the lexem formed thus far
     * @param INPUT source to be read from
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data gather_keyword_chars(final long LINE_NUMBER, final StringBuilder LEXEME_START, final Scanner INPUT)
    {
        String placeholder;

        while (INPUT.hasNext())
        {
            placeholder = INPUT.next();
            
            //check for keyword end
            if (WHITE_SPACE_CHAR.matcher(placeholder).matches())
            {
                final String LEXEM = LEXEME_START.toString();
                
                if (AND.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.AND, LINE_NUMBER, LEXEM));
                else if (OR.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.OR, LINE_NUMBER, LEXEM));
                else if (NOT.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.NOT, LINE_NUMBER, LEXEM));
                else if (XOR.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.XOR, LINE_NUMBER, LEXEM));
                else
                    return gather_ID_chars(LINE_NUMBER, LEXEME_START.append(placeholder), INPUT);
            }
            //check for restricted char
            else if (RESTRICTED_CHARS.contains(placeholder))
            {
                final String LEXEM = LEXEME_START.toString();
                
                if (AND.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.AND, LINE_NUMBER, LEXEM));
                else if (OR.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.OR, LINE_NUMBER, LEXEM));
                else if (NOT.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.NOT, LINE_NUMBER, LEXEM));
                else if (XOR.matcher(LEXEM).matches())
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.XOR, LINE_NUMBER, LEXEM));
                else
                    return new Returned_Data(placeholder, new Token(Token.Lexeme_Types.ID, LINE_NUMBER, LEXEM));
            }
            //keep building
            else
                LEXEME_START.append(placeholder);
        }
        
        return new Returned_Data(new Token(Token.Lexeme_Types.ID, LINE_NUMBER, LEXEME_START.toString()));
    }

    /**
     * Subroutine to obtain a sequence which may be an ID.
     * 
     * @param LINE_NUMBER of source file being read
     * @param LEXEME_START is the lexem formed thus far
     * @param INPUT source to be read from
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data gather_ID_chars(final long LINE_NUMBER, final StringBuilder LEXEME_START, final Scanner INPUT)
    {
        String placeholder;

        while (INPUT.hasNext())
        {
            placeholder = INPUT.next();

            if (ID_CHAR_SET.matcher(placeholder).matches())
                LEXEME_START.append(placeholder);
            else
                return parse_potentional_ID(LINE_NUMBER, placeholder, LEXEME_START.toString());
        }

        return parse_potentional_ID(LINE_NUMBER, null, LEXEME_START.toString());
    }

    /**
     * Determines which {@link Token} should be formed given the lexeme provided.
     * 
     * @param LINE_NUMBER of source file being read
     * @param REMAINDER which is the extra data that should be feed back into {@link #tokenize(long, String, Scanner)}
     * @param COMPLETE_LEXEME which is fully formed
     * @return the created {@link Token} wrapped in a {@link Returned_Data}
     */
    private static Returned_Data parse_potentional_ID(final long LINE_NUMBER, final String REMAINDER, final String COMPLETE_LEXEME)
    {
        //Test lexeme for special sequences, if all fail then is in fact ID.
        if (TREE_START.matcher(COMPLETE_LEXEME).matches())
            return new Returned_Data(REMAINDER, new Token(Token.Lexeme_Types.TREE_START, LINE_NUMBER, COMPLETE_LEXEME));
        else if (DISPLAY_START.matcher(COMPLETE_LEXEME).matches())
            return new Returned_Data(REMAINDER, new Token(Token.Lexeme_Types.DISPLAY_START, LINE_NUMBER, COMPLETE_LEXEME));
        else if (PROBABILITY_START.matcher(COMPLETE_LEXEME).matches())
            return new Returned_Data(REMAINDER, new Token(Token.Lexeme_Types.PROBABILITY_START, LINE_NUMBER, COMPLETE_LEXEME));
        else if (DECK_START.matcher(COMPLETE_LEXEME).matches())
            return new Returned_Data(REMAINDER, new Token(Token.Lexeme_Types.DECK_START, LINE_NUMBER, COMPLETE_LEXEME));
        else
            return new Returned_Data(REMAINDER, new Token(Token.Lexeme_Types.ID, LINE_NUMBER, COMPLETE_LEXEME));
    }
}
