import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Pattern;
import parser.Token;
import parser.Tokenizer;
import parser.Tokenizer.Returned_Data;

/**
<b>
Purpose: To be the central part which calls and runs the other parts. With the goal of calculating probility of a given hand.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-30
</b>
*/

public class Starting_Point
{
    /**
     * Directory to input source files.
     */
    public final static String SOURCE_FILE_LOCATION = "Input/";

    /**
     * Directory to output files to.
     */
    public final static String OUTPUT_FILE_LOCATION = "Output/";

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

    public static void main(String[] args) throws IOException
    {
        //TODO: improve both command line argument passing and parsing.
        //TODO: have command line option to create less files.
        File[] source_files;

        if (args.length == 1)
        {
            if (args[0].equals("--all"))
                source_files = (new File(SOURCE_FILE_LOCATION)).listFiles();
            else if (args[0].equals("--help"))
            {
                System.out.println("Command line options are none for default file that should be located at: " + SOURCE_FILE_LOCATION + DEFAULT_FILE +
                                   ". --all for all files at: " + SOURCE_FILE_LOCATION + ". Or can provide a path which will either use it as a file if it leads to one or use" +
                                   " all files in that directory in the event that it is a directory. Note file extension expected to be " + SOURCE_FILE_EXTENSION + ".");
                return;
            }
            else
            {
                File command_argument = new File(args[0]);
                if (!command_argument.exists())
                    source_files = new File[]{new File(SOURCE_FILE_LOCATION, DEFAULT_FILE)};
                else if (command_argument.isFile())
                    source_files = new File[]{command_argument};
                else if (command_argument.isDirectory())
                    source_files = command_argument.listFiles();
                else
                    source_files = new File[]{new File(SOURCE_FILE_LOCATION, DEFAULT_FILE)};
            }
        }
        else
            source_files = new File[]{new File(SOURCE_FILE_LOCATION, DEFAULT_FILE)};

        //make output directory
        {
            File parent_output_path = new File(OUTPUT_FILE_LOCATION);
            if (!parent_output_path.exists())
                parent_output_path.mkdirs();
        }

        final int FILE_COUNT = source_files.length;
        for (int i = 0; i < FILE_COUNT; ++i)
            if (source_files[i].exists() && source_files[i].getName().endsWith(SOURCE_FILE_EXTENSION))
            {
                File lexical_error_output_file;
                File lexical_correct_output_file;

                //create files
                {
                    String placeholder = OUTPUT_FILE_LOCATION + source_files[i].getName();
                    lexical_error_output_file = change_file_extension(true, LEXICAL_OUT_ERROR_FILE_EXTENSION, placeholder);
                    lexical_error_output_file.createNewFile();
                    lexical_correct_output_file = change_file_extension(true, LEXICAL_OUT_CORRECT_FILE_EXTENSION, placeholder);
                    lexical_correct_output_file.createNewFile();
                }

                try (Scanner lexical_analyer_input = new Scanner(source_files[i]); PrintWriter lexical_error_output = new PrintWriter(lexical_error_output_file);
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
            else
                System.err.println("Chosen file: " + source_files[i].getCanonicalPath() + ", either does not exist or has improper extension.");
    }

    //TODO: replace with call to version in Function_Bank.
    /**
     * Changes file extension while optionally dating it as well.
     * 
     * @param DATED If true, then will add current date-time, else will not.
     * @param NEW_EXTENSION that is replacing old file extension in new file.
     * @param INPUT_FILE that is basis for new file.
     * @return new File.
     */
    public static File change_file_extension(final boolean DATED, final String NEW_EXTENSION, final String INPUT_FILE)
    {
        String extracted_name = INPUT_FILE.substring(0, INPUT_FILE.lastIndexOf('.'));

        if (DATED)
            extracted_name += " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("u-M-d_km"));

        return new File(extracted_name + NEW_EXTENSION);
    }
}
