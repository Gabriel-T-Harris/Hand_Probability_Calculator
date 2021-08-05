import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;
import parser.Token;
import parser.Tokenizer;
import parser.Tokenizer.Returned_Data;

/**
<b>
Purpose: To be the central part which calls and runs the other parts. With the goal of calculating probility of a given hand.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-30/2021-8-1/2021-8-4
</b>
*/

public class Starting_Point
{
    /**
     * Directory to input source files.
     */
    public final static String DEFAULT_SOURCE_FILE_LOCATION = "Input/";

    /**
     * Directory to output files to.
     */
    public final static String DEFAULT_OUTPUT_FILE_LOCATION = "Output/";

    /**
     * File to default to in case of no command line offsets.
     */
    public final static String DEFAULT_FILE = "Default Configuration.txt";

    /**
     * File extension of source files.
     */
    public final static String SOURCE_FILE_EXTENSION = ".txt";

    /**
     * Extension of file for error malformed Tokens.
     */
    public final static String LEXICAL_OUT_ERROR_FILE_EXTENSION = ".outlexerrors";

    /**
     * Extension of file for properly formed Tokens.
     */
    public final static String LEXICAL_OUT_CORRECT_FILE_EXTENSION = ".outlextokens";

    //TODO: rename syntactical extension stuff
    /**
     * Extension of file for derivation of code by Syntactical_Analyzer.
     */
    public final static String SYNTACTICAL_OUT_DERIVATION_FILE_EXTENSION = ".outderivation";

    /**
     * Extension of file for abstract syntax tree.
     */
    public final static String SYNTACTICAL_OUT_AST_FILE_EXTENSION = ".outast.dot";

    /**
     * Simple pattern that matches using the predefined horizontal whitespace character. Equivalent to "[ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]".
     */
    public final static Pattern HORIZONTAL_WHITESPACE_CHARACTER = Pattern.compile("\\h");

    /**
     * Default size of hand to look at.
     */
    private final static int DEFAULT_HAND_SIZE = 5;

    /**
     * Default number of hands to simulate.
     */
    private final static long DEFAULT_TEST_HAND_COUNT = 1000000;

    public static void main(String[] args) throws IOException
    {
        boolean verbose = false; //for outputting of progress
        boolean error_log = false; //for system errors
        int hand_size = DEFAULT_HAND_SIZE;
        final int FILE_COUNT;
        long test_hands = DEFAULT_TEST_HAND_COUNT;
        String output_location = DEFAULT_OUTPUT_FILE_LOCATION;
        final File[] SOURCE_FILES;

        if (args.length > 0)
        {
            final String ERROR_LOG_FLAG = "--error_log", HELP_FLAG = "--help", VERBOSE_FLAG = "--verbose", INPUT_PARAMETER = "--input", OUTPUT_PARAMETER = "--output",
                         HAND_SIZE_PARAMETER = "--hand_size", TEST_HANDS_PARAMETER = "--test_hands";

            for (int i = 0; i < args.length; ++i)
                if (args[i].equals(ERROR_LOG_FLAG))
                {
                    error_log = true;
                    break;
                }

            final Command_Line_Argument_Parser PARSED_ARGUMENTS = new Command_Line_Argument_Parser(error_log);

            PARSED_ARGUMENTS.add_flags(ERROR_LOG_FLAG, HELP_FLAG, VERBOSE_FLAG);
            PARSED_ARGUMENTS.add_parameters(INPUT_PARAMETER, OUTPUT_PARAMETER, HAND_SIZE_PARAMETER, TEST_HANDS_PARAMETER);
            PARSED_ARGUMENTS.parse(args);

            if (PARSED_ARGUMENTS.flag_seen(HELP_FLAG))
            {
                System.out.println("Program is calculate the probability of a given sceanrio for all scenarios which have display set to true. Configuration files are expected to have the following extension: " +
                                   SOURCE_FILE_EXTENSION + "\n\nCommand line options are none for default file that should be located at: " + DEFAULT_SOURCE_FILE_LOCATION +
                                   DEFAULT_FILE +
                                   ".\n" + HELP_FLAG + ": for this message.\n" + ERROR_LOG_FLAG + ": to show error messages, does not include error files.\n" + VERBOSE_FLAG +
                                   ": for creaion of files showing progress, does include error files.\n" + OUTPUT_PARAMETER + ": is where created files will go.\n" +
                                   INPUT_PARAMETER +
                                   ": is where to look for configuration file. If the value is a file will read only that one, else if is directory, then will read all files in that directory." +
                                   HAND_SIZE_PARAMETER + ": is starting hand size\n" + TEST_HANDS_PARAMETER + ": is the number hands to simulate.");
                return;
            }
            else
            {
                verbose = PARSED_ARGUMENTS.flag_seen(VERBOSE_FLAG);
                final File COMMAND_ARGUMENT;
                {
                    final String INPUT_PATH = PARSED_ARGUMENTS.parameter_value(INPUT_PARAMETER);
                    COMMAND_ARGUMENT = new File(INPUT_PATH != null ? INPUT_PATH : DEFAULT_SOURCE_FILE_LOCATION + DEFAULT_FILE);
                }

                //parse arguments
                {
                    String read_argument = PARSED_ARGUMENTS.parameter_value(OUTPUT_PARAMETER);

                    //output path
                    if (read_argument != null)
                    {
                        if (new File(read_argument).isDirectory())
                            output_location = read_argument;
                        else if (error_log)
                            System.err.println("Requested output path \"" + read_argument + "\" either does not exist or is not a directory. Using default output location: \"" +
                                               output_location + "\".");
                    }

                    //hand_size
                    read_argument = PARSED_ARGUMENTS.parameter_value(HAND_SIZE_PARAMETER);
                    if (read_argument != null)
                    {
                        try
                        {
                            hand_size = Integer.parseInt(read_argument);

                            if (hand_size < 1)
                            {
                                if (error_log)
                                    System.err.println("hand_size was erroneously set to " + hand_size + " which is less then 1. Setting hand_size to proper default value: " +
                                                       DEFAULT_HAND_SIZE + ".");

                                hand_size = DEFAULT_HAND_SIZE;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            if (error_log)
                                System.err.println("Requested hand size could not be parsed to an integer \"" + read_argument + "\". Using default hand size: " + hand_size +
                                                   ".");
                        }
                    }

                    //test_hands
                    read_argument = PARSED_ARGUMENTS.parameter_value(TEST_HANDS_PARAMETER);
                    if (read_argument != null)
                    {
                        try
                        {
                            test_hands = Long.parseLong(read_argument);

                            if (test_hands < 1)
                            {
                                if (error_log)
                                    System.err.println("test_hands was erroneously set to " + test_hands + " which is less then 1. Setting test_hands to proper default value: " +
                                                       DEFAULT_TEST_HAND_COUNT + ".");

                                test_hands = DEFAULT_TEST_HAND_COUNT;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            if (error_log)
                                System.err.println("Requested hand size could not be parsed to an integer \"" + read_argument + "\". Using default hand size of " + hand_size +
                                                   ".");
                        }
                    }
                }

                if (!COMMAND_ARGUMENT.exists())
                {
                    if (error_log)
                        System.err.println("Requested file with path \"" + COMMAND_ARGUMENT.getAbsolutePath() + "\" does not exist.");
                    SOURCE_FILES = new File[]{new File(DEFAULT_SOURCE_FILE_LOCATION, DEFAULT_FILE)};
                }
                else if (COMMAND_ARGUMENT.isFile())
                    SOURCE_FILES = new File[]{COMMAND_ARGUMENT};
                else if (COMMAND_ARGUMENT.isDirectory())
                    SOURCE_FILES = COMMAND_ARGUMENT.listFiles();
                //should never happen
                else
                {
                    if (error_log)
                        System.err.println("Error: requested path exists but is neither a file nor a directory. Should be impossible. Requested path \"" +
                                           COMMAND_ARGUMENT.getAbsolutePath() + "\". Defaulting to default input path and default file.");
                    SOURCE_FILES = new File[]{new File(DEFAULT_SOURCE_FILE_LOCATION, DEFAULT_FILE)};
                }
            }
        }
        else
            SOURCE_FILES = new File[]{new File(DEFAULT_SOURCE_FILE_LOCATION, DEFAULT_FILE)};

        FILE_COUNT = SOURCE_FILES.length;

        create_theoretical_directories(output_location);

        if (verbose)
        {
            for (int i = 0; i < FILE_COUNT; ++i)
                if (SOURCE_FILES[i].exists() && SOURCE_FILES[i].getName().endsWith(SOURCE_FILE_EXTENSION))
                {
                    File lexical_error_output_file;
                    File lexical_correct_output_file;

                    //create files
                    {
                        String placeholder = output_location + SOURCE_FILES[i].getName();
                        lexical_error_output_file = Function_Bank.change_file_extension(true, LEXICAL_OUT_ERROR_FILE_EXTENSION, placeholder);
                        lexical_error_output_file.createNewFile();
                        lexical_correct_output_file = Function_Bank.change_file_extension(true, LEXICAL_OUT_CORRECT_FILE_EXTENSION, placeholder);
                        lexical_correct_output_file.createNewFile();
                    }

                    try (Scanner lexical_analyer_input = new Scanner(SOURCE_FILES[i]); PrintWriter lexical_error_output = new PrintWriter(lexical_error_output_file);
                         PrintWriter lexical_correct_output = new PrintWriter(lexical_correct_output_file))
                    {
                        lexical_analyer_input.useDelimiter(""); //Change scanner to parse by character
                        long line_number = 1; //location in source file
                        String read_character; //Actually a char, current character being looked at.
                        String carry_over = null; //Part of next Token found by current Token.
                        Returned_Data current_token;

                        //parse tokens
                        do
                        {
                            //attempt to use left over data 
                            if (carry_over != null)
                                read_character = carry_over;// Reply on current_token setting next value of carry_over, be it null or something else.
                            else if (lexical_analyer_input.hasNext())
                                read_character = lexical_analyer_input.next();
                            else
                                break;//no more data to tokenize

                            //new line character variations
                            if (read_character.equals("\r"))
                            {
                                ++line_number;

                                //check next character
                                if (lexical_analyer_input.hasNext())
                                {
                                    read_character = lexical_analyer_input.next();

                                    if (read_character.equals("\n"))
                                        lexical_correct_output.print("\r\n");
                                    else
                                    {
                                        lexical_correct_output.print('\r');
                                        carry_over = read_character;
                                        continue;
                                    }
                                }
                                else
                                    lexical_correct_output.print('\r');

                                carry_over = null;
                                continue;
                            }
                            else if (read_character.equals("\n"))
                            {
                                ++line_number;
                                lexical_correct_output.print(read_character);
                                carry_over = null;
                                continue;
                            }
                            else if (HORIZONTAL_WHITESPACE_CHARACTER.matcher(read_character).matches())
                            {
                                lexical_correct_output.print(read_character);
                                carry_over = null;
                                continue;
                            }

                            //lexical analyzer
                            current_token = Tokenizer.tokenize(line_number, read_character, lexical_analyer_input);
                            carry_over = current_token.extra_data;

                            {
                                Token.Lexeme_Types placeholder = current_token.FULLY_FORMED_PART.get_type();
                                //write to lexical error file
                                if (Token.Lexeme_Types.is_error(placeholder))
                                    lexical_error_output.print(current_token.FULLY_FORMED_PART.toString());
                                //write to normal lexical file
                                else
                                {
                                    if (placeholder.equals(Token.Lexeme_Types.LINE_COMMENT))
                                    {
                                        //line comments were accidently eating the new line characters, so this is a pseudo counter balance
                                        lexical_correct_output.println(current_token.FULLY_FORMED_PART.toString());
                                        ++line_number;
                                    }
                                    else
                                    {
                                        final String TOKEN_STRING_FORM = current_token.FULLY_FORMED_PART.toString();
                                        if (placeholder.equals(Token.Lexeme_Types.BLOCK_COMMENT))
                                            line_number += TOKEN_STRING_FORM.length() - TOKEN_STRING_FORM.replace("\n", "").length();

                                        lexical_correct_output.print(TOKEN_STRING_FORM);
                                    }
                                    /*if (placeholder != Token.Lexeme_Types.BLOCK_COMMENT && placeholder != Token.Lexeme_Types.LINE_COMMENT)
                                    {
                                        try
                                        {
                                            abstract_syntax_tree_maker.parse(current_token.fully_formed_part);
                                        }
                                        catch (EmptySemanticStackException | TerminalMatchException ex)
                                        {
                                            syntactical_error_output.println(ex.getMessage());
                                        }
                                    }*/
                                }
                            }
                        } while (true);
                    }
                    catch (FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                        System.err.println("Should never be reached unless a file is modified/deleted during program execuation.");
                    }
                }
                else if (error_log)
                    System.err.println("Chosen file: " + SOURCE_FILES[i].getCanonicalPath() + ", either does not exist or has improper extension.");
        }
        else
        {
            for (int i = 0; i < FILE_COUNT; ++i)
                if (SOURCE_FILES[i].exists() && SOURCE_FILES[i].getName().endsWith(SOURCE_FILE_EXTENSION))
                {
                    try (Scanner lexical_analyer_input = new Scanner(SOURCE_FILES[i]))
                    {
                        lexical_analyer_input.useDelimiter(""); //Change scanner to parse by character
                        long line_number = 1; //location in source file
                        String read_character; //Actually a char, current character being looked at.
                        String carry_over = null; //Part of next Token found by current Token.
                        Returned_Data current_token;

                        //parse tokens
                        do
                        {
                            //attempt to use left over data 
                            if (carry_over != null)
                                read_character = carry_over;// Reply on current_token setting next value of carry_over, be it null or something else.
                            else if (lexical_analyer_input.hasNext())
                                read_character = lexical_analyer_input.next();
                            else
                                break;//no more data to tokenize

                            //new line character variations
                            if (read_character.equals("\r"))
                            {
                                ++line_number;

                                //check next character
                                if (lexical_analyer_input.hasNext())
                                {
                                    read_character = lexical_analyer_input.next();

                                    if (!read_character.equals("\n"))
                                    {
                                        carry_over = read_character;
                                        continue;
                                    }
                                }

                                carry_over = null;
                                continue;
                            }
                            else if (read_character.equals("\n"))
                            {
                                ++line_number;
                                carry_over = null;
                                continue;
                            }
                            else if (HORIZONTAL_WHITESPACE_CHARACTER.matcher(read_character).matches())
                            {
                                carry_over = null;
                                continue;
                            }

                            //lexical analyzer
                            current_token = Tokenizer.tokenize(line_number, read_character, lexical_analyer_input);
                            carry_over = current_token.extra_data;

                            {
                                Token.Lexeme_Types placeholder = current_token.FULLY_FORMED_PART.get_type();
                                //write to normal lexical file
                                if (!Token.Lexeme_Types.is_error(placeholder))
                                {
                                    if (placeholder.equals(Token.Lexeme_Types.LINE_COMMENT))
                                    {
                                        //line comments were accidently eating the new line characters, so this is a pseudo counter balance
                                        ++line_number;
                                    }
                                    else
                                    {
                                        final String TOKEN_STRING_FORM = current_token.FULLY_FORMED_PART.toString();
                                        if (placeholder.equals(Token.Lexeme_Types.BLOCK_COMMENT))
                                            line_number += TOKEN_STRING_FORM.length() - TOKEN_STRING_FORM.replace("\n", "").length();
                                    }
                                    /*if (placeholder != Token.Lexeme_Types.BLOCK_COMMENT && placeholder != Token.Lexeme_Types.LINE_COMMENT)
                                    {
                                        try
                                        {
                                            abstract_syntax_tree_maker.parse(current_token.fully_formed_part);
                                        }
                                        catch (EmptySemanticStackException | TerminalMatchException ex)
                                        {
                                            syntactical_error_output.println(ex.getMessage());
                                        }
                                    }*/
                                }
                            }
                        } while (true);
                    }
                    catch (FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                        System.err.println("Should never be reached unless a file is modified/deleted during program execuation.");
                    }
                }
                else if (error_log)
                    System.err.println("Chosen file: " + SOURCE_FILES[i].getCanonicalPath() + ", either does not exist or has improper extension.");
        }
    }

    /**
     * Makes output directories for subdirectories which do not exist.
     * @param INPUT representing directory path to create
     */
    public static void create_theoretical_directories(final String INPUT)
    {
        File parent_output_path = new File(INPUT);
        if (!parent_output_path.exists())
            parent_output_path.mkdirs();
    }
}
