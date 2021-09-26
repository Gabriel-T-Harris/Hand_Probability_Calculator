package starting_point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import com.gth.miscellaneous.Command_Line_Argument_Parser;
import parser.EmptySemanticStackException;
import parser.Lexeme_Types;
import parser.Returned_Data;
import parser.Tokenizer;
import parser.Tree_Assembler;
import simulation.Simulation;

/**
<b>
Purpose: To be the central part which calls and runs the other parts. With the goal of calculating probability of a given hand.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-30/2021-8-1/2021-8-4/2021-8-[11, 13]/2021-8-17/2021-8-19/2021-8-21/2021-8-23
</b>
*/

public class Starting_Point
{
    /**
     * Default size of hand to look at.
     */
    public final static int DEFAULT_HAND_SIZE = 5;

    /**
     * Default number of hands to simulate.
     */
    public final static int DEFAULT_TEST_HAND_COUNT = 1500000;

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

    /**
     * Extension of file for syntactical errors.
     */
    public final static String SYNTACTICAL_OUT_ERROR_FILE_EXTENSION = ".outsyntaxerrors";

    /**
     * Extension of file for derivation of code by {@link Tree_Assembler}.
     */
    public final static String SYNTACTICAL_OUT_DERIVATION_FILE_EXTENSION = ".outderivation";

    /**
     * Extension of files representing outputted decklist.
     */
    public final static String DECKLIST_EXTENSION = ".decklist.txt";

    /**
     * Extension of file for scenarios.
     */
    public final static String SYNTACTICAL_OUT_SCENARIO_FILE_EXTENSION = ".outscenario.dot";

    /**
     * File extension for the results of the simulation.
     */
    public final static String SIMULATION_RESULTS_EXTENSION = ".results";

    /**
     * Name of the authors of this program and where it can be downloaded from
     */
    public final static String AUTHORS = "Created by both Gabriel Toban Harris and Alexander Herman Oxorn. " +
                                         "Most recent releases can be found at \"https://github.com/Gabriel-T-Harris/Hand_Probability_Calculator\".";

    /**
     * Simple pattern that matches using the predefined horizontal whitespace character. Equivalent to "[ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]".
     */
    public final static Pattern HORIZONTAL_WHITESPACE_CHARACTER = Pattern.compile("\\h");

    /**
     * for system errors
     */
    private static boolean error_reporting = false;

    /**
     * for output decklist and scenarios
     */
    private static boolean scenario_output = false;

    /**
     * true for create file for results, otherwise output to console
     */
    private static boolean simulation_results_console = false;

    /**
     * true for jumping straight to sequential simulation rather then letting the program decide
     */
    private static boolean force_sequential = false;

    /**
     * size of hands to draw during simulation
     */
    private static int hand_size = Starting_Point.DEFAULT_HAND_SIZE;

    /**
     * number of hands to test
     */
    private static int test_hands = Starting_Point.DEFAULT_TEST_HAND_COUNT;

    /**
     * location to output files to
     */
    private static String output_location = Starting_Point.DEFAULT_OUTPUT_FILE_LOCATION;

    public static void main(String[] args)
    {
        boolean verbose = false; //for outputting of progress, does include error files
        final File[] SOURCE_FILES; //files to be parsed

        if (args.length > 0)
        {
            final String ERROR_REPORTING_FLAG = "--error_reporting", HELP_FLAG = "--help", VERBOSE_FLAG = "--verbose", SCENARIO_OUTPUT_FLAG = "--scenario_output_flag",
                         SIMULATION_RESULTS_CONSOLE_FLAG = "--simulation_results_console", FORCE_SEQUENTIAL_FLAG = "--force_sequential", INPUT_PARAMETER = "--input",
                         OUTPUT_PARAMETER = "--output", HAND_SIZE_PARAMETER = "--hand_size", TEST_HANDS_PARAMETER = "--test_hands";


            for (int i = 0; i < args.length; ++i)
                if (args[i].equals(ERROR_REPORTING_FLAG))
                {
                    Starting_Point.error_reporting = true;
                    break;
                }

            final Command_Line_Argument_Parser PARSED_ARGUMENTS = new Command_Line_Argument_Parser(Starting_Point.error_reporting);

            PARSED_ARGUMENTS.add_flags(ERROR_REPORTING_FLAG, HELP_FLAG, VERBOSE_FLAG, SCENARIO_OUTPUT_FLAG, SIMULATION_RESULTS_CONSOLE_FLAG, FORCE_SEQUENTIAL_FLAG);
            PARSED_ARGUMENTS.add_parameters(INPUT_PARAMETER, OUTPUT_PARAMETER, HAND_SIZE_PARAMETER, TEST_HANDS_PARAMETER);
            PARSED_ARGUMENTS.parse(args);

            if (PARSED_ARGUMENTS.flag_seen(HELP_FLAG))
            {
                System.out.println(Starting_Point.AUTHORS + " Program to calculate the probability of scenarios given at runtime which have display set to true. Configuration files are expected to have the following extension: " +
                                   Starting_Point.SOURCE_FILE_EXTENSION + "\n\nCommand line options are none for default file that should be located at: " +
                                   Starting_Point.DEFAULT_SOURCE_FILE_LOCATION + Starting_Point.DEFAULT_FILE + ".\n" +
                                   HELP_FLAG + ": for this message.\n" +
                                   ERROR_REPORTING_FLAG + ": to show error messages, does not include error files.\n" +
                                   VERBOSE_FLAG + ": for creation of files showing progress, does include error files.\n" +
                                   SCENARIO_OUTPUT_FLAG + ": to output read decklist and scenarios in dot file format.\n" +
                                   SIMULATION_RESULTS_CONSOLE_FLAG + ": to have the simulation results be output to console instead of in a created file.\n" +
                                   FORCE_SEQUENTIAL_FLAG + ": to force the program to perform the simulation sequentially rather then allowing the program to pick sequential or parallel.\n" +
                                   OUTPUT_PARAMETER + ": is where created files will go.\n" +
                                   INPUT_PARAMETER + ": is where to look for configuration file. If the value is a file will read only that one, else if is directory, then will read all files in that directory." +
                                   HAND_SIZE_PARAMETER + ": is starting hand size.\n" +
                                   TEST_HANDS_PARAMETER + ": is the number hands to simulate.");
                return;
            }
            else
            {
                verbose = PARSED_ARGUMENTS.flag_seen(VERBOSE_FLAG);
                Starting_Point.scenario_output = PARSED_ARGUMENTS.flag_seen(SCENARIO_OUTPUT_FLAG);
                Starting_Point.simulation_results_console = PARSED_ARGUMENTS.flag_seen(SIMULATION_RESULTS_CONSOLE_FLAG);
                Starting_Point.force_sequential = PARSED_ARGUMENTS.flag_seen(FORCE_SEQUENTIAL_FLAG);
                final File COMMAND_ARGUMENT;
                {
                    final String INPUT_PATH = PARSED_ARGUMENTS.parameter_value(INPUT_PARAMETER);
                    COMMAND_ARGUMENT = new File(INPUT_PATH != null ? INPUT_PATH : Starting_Point.DEFAULT_SOURCE_FILE_LOCATION + Starting_Point.DEFAULT_FILE);
                }

                //parse arguments
                {
                    String read_argument = PARSED_ARGUMENTS.parameter_value(OUTPUT_PARAMETER);

                    //output path
                    if (read_argument != null)
                    {
                        if (new File(read_argument).isDirectory())
                            Starting_Point.output_location = read_argument;
                        else if (Starting_Point.error_reporting)
                            System.err.println("Requested output path \"" + read_argument + "\" either does not exist or is not a directory. Using default output location: \"" +
                                               Starting_Point.output_location + "\".");
                    }

                    //hand_size
                    read_argument = PARSED_ARGUMENTS.parameter_value(HAND_SIZE_PARAMETER);
                    if (read_argument != null)
                    {
                        try
                        {
                            Starting_Point.hand_size = Integer.parseInt(read_argument);

                            if (Starting_Point.hand_size < 1)
                            {
                                if (Starting_Point.error_reporting)
                                    System.err.println("hand_size was erroneously set to " + Starting_Point.hand_size +
                                                       " which is less then 1. Setting hand_size to proper default value: " + Starting_Point.DEFAULT_HAND_SIZE + ".");

                                Starting_Point.hand_size = Starting_Point.DEFAULT_HAND_SIZE;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            if (Starting_Point.error_reporting)
                                System.err.println("Requested hand size could not be parsed to an integer \"" + read_argument + "\". Using default hand size: " +
                                                   Starting_Point.hand_size + ".");
                        }
                    }

                    //test_hands
                    read_argument = PARSED_ARGUMENTS.parameter_value(TEST_HANDS_PARAMETER);
                    if (read_argument != null)
                    {
                        try
                        {
                            Starting_Point.test_hands = Integer.parseInt(read_argument);

                            if (Starting_Point.test_hands < 1)
                            {
                                if (Starting_Point.error_reporting)
                                    System.err.println("test_hands was erroneously set to " + Starting_Point.test_hands +
                                                       " which is less then 1. Setting test_hands to proper default value: " + Starting_Point.DEFAULT_TEST_HAND_COUNT + ".");

                                Starting_Point.test_hands = Starting_Point.DEFAULT_TEST_HAND_COUNT;
                            }
                        }
                        catch (NumberFormatException ex)
                        {
                            if (Starting_Point.error_reporting)
                                System.err.println("Requested number of hands to test with could not be parsed to an integer \"" + read_argument +
                                                   "\". Using default number of test hands, " + Starting_Point.test_hands + ".");
                        }
                    }
                }

                if (!COMMAND_ARGUMENT.exists())
                {
                    if (Starting_Point.error_reporting)
                        System.err.println("Requested file with path \"" + COMMAND_ARGUMENT.getAbsolutePath() + "\" does not exist.");
                    SOURCE_FILES = new File[]{new File(Starting_Point.DEFAULT_SOURCE_FILE_LOCATION, Starting_Point.DEFAULT_FILE)};
                }
                else if (COMMAND_ARGUMENT.isFile())
                    SOURCE_FILES = new File[]{COMMAND_ARGUMENT};
                else if (COMMAND_ARGUMENT.isDirectory())
                    SOURCE_FILES = COMMAND_ARGUMENT.listFiles();
                //should never happen
                else
                {
                    if (Starting_Point.error_reporting)
                        System.err.println("Error: requested path exists but is neither a file nor a directory. Should be impossible. Requested path \"" +
                                           COMMAND_ARGUMENT.getAbsolutePath() + "\". Defaulting to default input path and default file.");
                    SOURCE_FILES = new File[]{new File(Starting_Point.DEFAULT_SOURCE_FILE_LOCATION, Starting_Point.DEFAULT_FILE)};
                }
            }
        }
        else
            SOURCE_FILES = new File[]{new File(Starting_Point.DEFAULT_SOURCE_FILE_LOCATION, Starting_Point.DEFAULT_FILE)};

        Starting_Point.create_theoretical_directories(Starting_Point.output_location);

        if (SOURCE_FILES.length == 1)
            Starting_Point.handle_file(verbose, SOURCE_FILES[0]);
        else
        {
            final boolean VERBOSE_COPY = verbose; //for lambda to have final
            Arrays.stream(SOURCE_FILES).parallel().forEach(input -> Starting_Point.handle_file(VERBOSE_COPY, input));
        }
    }

    /**
     * Removes file extension.
     * 
     * @param FILE_NAME to have extension removed
     * 
     * @return result
     */
    public static String remove_file_name_extension(final String FILE_NAME)
    {
        return FILE_NAME.substring(0, FILE_NAME.lastIndexOf('.'));
    }

    /**
     * Removes illegal char (humane readable version): [/<>:"\|?*].
     * 
     * @param FILE_NAME to be cleaned
     * 
     * @return result
     */
    public static String remove_illegal_char_file_name(final String FILE_NAME)
    {
        return FILE_NAME.replaceAll("[/<>:\"\\\\|?*]", "");
    }

    /**
     * Convenience function, calls {@link #remove_file_name_extension(String)} and then {@link #remove_illegal_char_file_name(String)}.
     * 
     * @param FILE_NAME to be cleaned
     * 
     * @return result of cleaning
     */
    public static String sanatize_file_name(final String FILE_NAME)
    {
        return Starting_Point.remove_illegal_char_file_name(Starting_Point.remove_file_name_extension(FILE_NAME));
    }

    /**
     * Adds file extension while optionally dating it as well. Also removes the following illegal char for file name: 
     * 
     * @param DATED If true, then will add current date-time, else will not.
     * @param NEW_EXTENSION that is add to extracated_name, which is then used to make a new file.
     * @param extracted_name that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it.
     * 
     * @return new File.
     */
    public static File add_file_extension(final boolean DATED, final String NEW_EXTENSION, String extracted_name)
    {
        if (DATED)
            extracted_name += " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("u-M-d_k-m"));

        return new File(extracted_name + NEW_EXTENSION);
    }

    /**
     * Makes output directories for subdirectories which do not exist.
     * 
     * @param INPUT representing directory path to create
     */
    public static void create_theoretical_directories(final String INPUT)
    {
        File parent_output_path = new File(INPUT);
        if (!parent_output_path.exists())
            parent_output_path.mkdirs();
    }

    /**
     * Performs/calls all operations relating to handling files.
     * 
     * @param verbose for outputting of progress, does include error files
     * @param INPUT_FILE to be parsed and actted on
     */
    public static void handle_file(boolean verbose, final File INPUT_FILE)
    {
        if (INPUT_FILE.exists() && INPUT_FILE.getName().endsWith(Starting_Point.SOURCE_FILE_EXTENSION))
        {
            String partial_output_directory = Starting_Point.output_location + Starting_Point.sanatize_file_name(INPUT_FILE.getName());
            File lexical_error_output_file = null, lexical_correct_output_file = null, syntactical_error_output_file = null, syntactical_derivation_output_file = null;
            final Simulation simulator; //does the simulation

            //create files
            if (verbose)
            {
                try
                {
                    lexical_error_output_file = Starting_Point.add_file_extension(true, Starting_Point.LEXICAL_OUT_ERROR_FILE_EXTENSION, partial_output_directory);
                    lexical_error_output_file.createNewFile();
                    lexical_correct_output_file = Starting_Point.add_file_extension(true, Starting_Point.LEXICAL_OUT_CORRECT_FILE_EXTENSION, partial_output_directory);
                    lexical_correct_output_file.createNewFile();
                    syntactical_error_output_file = Starting_Point.add_file_extension(true, Starting_Point.SYNTACTICAL_OUT_ERROR_FILE_EXTENSION, partial_output_directory);
                    syntactical_error_output_file.createNewFile();
                    syntactical_derivation_output_file = Starting_Point.add_file_extension(true, Starting_Point.SYNTACTICAL_OUT_DERIVATION_FILE_EXTENSION, partial_output_directory);
                    syntactical_derivation_output_file.createNewFile();
                }
                catch (IOException ex)
                {
                    System.err.println("Error, could not create output files relating to the verbose flag. Switching verbose to false for this file whose name is \"" +
                                       INPUT_FILE.getName() + "\".\n" + ex.getMessage());
                    verbose = false;
                }
            }

            try (final Scanner LEXICAL_ANALYER_INPUT = new Scanner(INPUT_FILE);
                 final PrintWriter LEXICAL_ERROR_OUTPUT = (verbose) ? new PrintWriter(lexical_error_output_file) : null;
                 final PrintWriter LEXICAL_CORRECT_OUTPUT = (verbose) ? new PrintWriter(lexical_correct_output_file) : null;
                 final PrintWriter SYNTACTICAL_ERROR_OUTPUT = (verbose) ? new PrintWriter(syntactical_error_output_file) : null;
                 final PrintWriter SYNTACTICAL_DERIVATION_OUTPUT = (verbose) ? new PrintWriter(syntactical_derivation_output_file) : null;)
            {
                LEXICAL_ANALYER_INPUT.useDelimiter(""); //Change scanner to parse by character
                long line_number = 1; //location in source file
                String read_character; //Actually a char, current character being looked at.
                String carry_over = null; //Part of next Token found by current Token.
                Returned_Data current_token; //result of latest tokenization
                final Tree_Assembler GROW_FOREST = (verbose) ? new Tree_Assembler(40, 10, SYNTACTICAL_ERROR_OUTPUT, SYNTACTICAL_DERIVATION_OUTPUT) : new Tree_Assembler(); //parses created tokens

                //parse tokens
                do
                {
                    //attempt to use left over data 
                    if (carry_over != null)
                        read_character = carry_over;// Reply on current_token setting next value of carry_over, be it null or something else.
                    else if (LEXICAL_ANALYER_INPUT.hasNext())
                        read_character = LEXICAL_ANALYER_INPUT.next();
                    else
                        break;//no more data to tokenize

                    //new line character variations
                    if (read_character.equals("\r"))
                    {
                        ++line_number;

                        //check next character
                        if (LEXICAL_ANALYER_INPUT.hasNext())
                        {
                            read_character = LEXICAL_ANALYER_INPUT.next();

                            if (read_character.equals("\n"))
                            {
                                if (verbose)
                                    LEXICAL_CORRECT_OUTPUT.print("\r\n");
                            }
                            else
                            {
                                if (verbose)
                                    LEXICAL_CORRECT_OUTPUT.print('\r');
                                carry_over = read_character;
                                continue;
                            }
                        }
                        else if (verbose)
                            LEXICAL_CORRECT_OUTPUT.print('\r');

                        carry_over = null;
                        continue;
                    }
                    else if (read_character.equals("\n"))
                    {
                        ++line_number;
                        if (verbose)
                            LEXICAL_CORRECT_OUTPUT.print(read_character);
                        carry_over = null;
                        continue;
                    }
                    else if (Starting_Point.HORIZONTAL_WHITESPACE_CHARACTER.matcher(read_character).matches())
                    {
                        if (verbose)
                            LEXICAL_CORRECT_OUTPUT.print(read_character);
                        carry_over = null;
                        continue;
                    }

                    //lexical analyzer
                    current_token = Tokenizer.tokenize(line_number, read_character, LEXICAL_ANALYER_INPUT);
                    carry_over = current_token.EXTRA_DATA;

                    {
                        final Lexeme_Types CURRENT_TOKEN_TYPE = current_token.FULLY_FORMED_PART.get_type();
                        //write to lexical error file
                        if (Lexeme_Types.is_error(CURRENT_TOKEN_TYPE))
                        {
                            if (verbose)
                                LEXICAL_ERROR_OUTPUT.print(current_token.FULLY_FORMED_PART.toString());
                        }
                        //write to normal lexical file
                        else
                        {
                            if (CURRENT_TOKEN_TYPE.equals(Lexeme_Types.LINE_COMMENT))
                            {
                                //line comments were accidently eating the new line characters, so this is a pseudo counter balance
                                if (verbose)
                                    LEXICAL_CORRECT_OUTPUT.println(current_token.FULLY_FORMED_PART.toString());
                                ++line_number;
                            }
                            else
                            {
                                if (CURRENT_TOKEN_TYPE.equals(Lexeme_Types.BLOCK_COMMENT))
                                {
                                    final String TOKEN_STRING_FORM = current_token.FULLY_FORMED_PART.toString();
                                    line_number += TOKEN_STRING_FORM.length() - TOKEN_STRING_FORM.replace("\n", "").length();
                                }
                                else
                                {
                                    if (verbose)
                                        LEXICAL_CORRECT_OUTPUT.print(current_token.FULLY_FORMED_PART.toString());

                                    //not a line comment, block comment, nor error at this point
                                    try
                                    {
                                        GROW_FOREST.parse(current_token.FULLY_FORMED_PART);
                                    }
                                    catch (EmptySemanticStackException ex)
                                    {
                                        System.err.println(ex.getMessage() + "\nParsing of current file: " + INPUT_FILE.getName() + " is dubiously assumed to be complete.\n");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } while (true);

                //finish syntactical operations
                if (!GROW_FOREST.check_stacks_are_empty())
                    throw new IllegalStateException("Fatal error: result of Tree_Assembler after parsing all tokens having non-empty stacks. " +
                                                    "Said stacks are expected to be empty. Thus configuration file is so erroneous that it is not worth continuing with. " +
                                                    "Check error files for more details (be sure to explicitly enable their existence with the --verbose flag).");

                if (Starting_Point.scenario_output)
                    GROW_FOREST.print_out_results(partial_output_directory); //Output decklist and all scenarios in dot format.

                simulator = GROW_FOREST.create_result(); //create simulation from parsed tokens
            }
            catch (FileNotFoundException ex)
            {
                System.err.println("Should never be reached unless a file is modified/deleted during program execution.\nSkipping current file: " + INPUT_FILE.getName() + "\n" +
                                   ex.getMessage() + "\n");
                return;
            }
            catch (IllegalStateException | IllegalArgumentException ex)
            {
                System.err.println("Skipping current file: " + INPUT_FILE.getName() + "\n" + ex.getMessage() + "\n");
                return;
            }

            //perform simulation
            {
                String results = simulator.simulate(Starting_Point.force_sequential, Starting_Point.hand_size, Starting_Point.test_hands);

                if (Starting_Point.simulation_results_console)
                    System.out.print(results);
                else
                {
                    final File OUTPUT_FILE = Starting_Point.add_file_extension(true, Starting_Point.SIMULATION_RESULTS_EXTENSION, partial_output_directory);

                    try
                    {
                        OUTPUT_FILE.createNewFile();
                        try (final PrintWriter SIMULATION_RESULTS_OUTPUT = new PrintWriter(OUTPUT_FILE))
                        {
                            SIMULATION_RESULTS_OUTPUT.print(results);
                        }
                    }
                    catch (IOException ex)
                    {
                        System.err.println("Error caused with following path: " + OUTPUT_FILE.getAbsolutePath() +
                                           ", thus could not output its results to a file, diverting to outputting to console instead.\n" + ex.getMessage());
                        System.out.print(results);
                    }
                }
            }
        }
        else if (Starting_Point.error_reporting)
            System.err.println("Chosen file: " + INPUT_FILE.getAbsolutePath() + ", either does not exist or has improper extension.");
    }
}
