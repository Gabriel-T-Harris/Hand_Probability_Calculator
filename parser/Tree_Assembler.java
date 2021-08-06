package parser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import com.gth.function_bank.Function_Bank;
import structure.Evaluable;
import structure.Scenario;

/**
<b>
Purpose: assembles the parts of a configuration file for simulating.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04/2021-8-5
</b>
*/

//TODO: figure out how to effectively handle VERBOSE, as would like to not recheck it, maybe just use lambda as function pointer.
public class Tree_Assembler<T, U>
{
    /**
     * Determines whether to output files, true for make files and false for no file creation.
     */
    public final boolean VERBOSE;

    /**
     * Special sequence meant to surround the production rules such that they are less likely to accidently appear as a token.
     */
    private static final String SPECIAL_UNLIKELY_SENTINEL = "_&GTH&_";

    /**
     * Derivation of the input.
     */
    private StringBuilder derivation;

    /**
     * stack like structure used by {@link Tree_Assembler#parse(Token)}
     */
    private ArrayList<Semantic_Actions> semantic_stack;

    /**
     * Represents stack like structure for building the {@link Scenario} and deck list, used by {@link Tree_Assembler#parse(Token)}
     */
    private ArrayList<Evaluable<U>> syntactical_stack;

    /**
     * Main deck which the hand will be generated from.
     */
    private final ArrayList<T> DECK;

    /**
     * Stores the generated scenarios.
     */
    private final HashMap<String, Scenario<U>> FOREST;

    /**
     * file to output syntactical errors to.
     */
    private PrintWriter syntactical_error_output;

    /**
     * file to output derivation
     */
    private PrintWriter syntactical_derivation_output;

    /**
     * Default constructor.
     */
    public Tree_Assembler()
    {
        this.VERBOSE = false;
        this.DECK = new ArrayList<T>(40); //predicted average deck size
        this.FOREST = new HashMap<String, Scenario<U>>(10); //predicted average number of scenarios
        this.finish_construction();
    }
    
    /**
     * Parameterized constructor. Which is implicitly setting {@link #VERBOSE} to true.
     * 
     * @param EXPECTED_DECK_SIZE helps with memory management, but can be wrong
     * @param EXPECTED_SCENARIO_COUNT helps with memory management, but can be wrong
     * @param SYNTACTICAL_ERROR_OUTPUT {@link #syntactical_error_output}
     * @param SYNTACTICAL_DERIVATION_OUTPUT {@link #syntactical_derivation_output}
     */
    public Tree_Assembler(final int EXPECTED_DECK_SIZE, final int EXPECTED_SCENARIO_COUNT, final PrintWriter SYNTACTICAL_ERROR_OUTPUT, final PrintWriter SYNTACTICAL_DERIVATION_OUTPUT)
    {
        this.VERBOSE = true;
        this.DECK = new ArrayList<T>(EXPECTED_DECK_SIZE);
        this.FOREST = new HashMap<String, Scenario<U>>(EXPECTED_SCENARIO_COUNT);
        this.derivation = new StringBuilder(SPECIAL_UNLIKELY_SENTINEL + Semantic_Actions.START.name() + SPECIAL_UNLIKELY_SENTINEL);
        this.syntactical_error_output = SYNTACTICAL_ERROR_OUTPUT;
        this.syntactical_derivation_output = SYNTACTICAL_DERIVATION_OUTPUT;
        this.finish_construction();
    }

    /**
     * True output of this object.
     * 
     * @return culmination of this class, destroy the object afterwards
     */
    public Simulatation<T, U> creat_result()
    {
        return new Simulatation<T, U>(this.DECK, this.FOREST);
    }

    /**
     * Method for parsing tokens into an abstract syntax tree. Uses all data members.
     * Make sure to call {@link #finish_semantic_stack} once all the {@link Token} are are parsed.
     * @param INPUT current top token being looked at
     * @throws EmptySemanticStackException when internal semantic_stack is empty yet there is another token to parse.
     * @throws TerminalMatchException {@link #match_subroutine}
     */
    public void parse(final Token INPUT) throws EmptySemanticStackException, TerminalMatchException
    {
        boolean no_match = true;
        int semantic_stack_end_index;

        do
        {
            semantic_stack_end_index = this.semantic_stack.size() - 1;

            if (semantic_stack_end_index == -1)
                throw new EmptySemanticStackException("Error: semantic stack is empty yet received the following Token: " + INPUT);

            Semantic_Actions switch_value = this.semantic_stack.get(semantic_stack_end_index);

            switch (switch_value)
            {
                case START:
                {
                    switch (INPUT.get_type())
                    {
                        case DECK_START:
                        {
                            //START -> DECK PROBABILITY
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.START.name(), Semantic_Actions.DECK, Semantic_Actions.PROBABILITY);
                            /*if (this.VERBOSE)
                            {
                                // work on derivation
                                Function_Bank.stringbuilder_replace_string_with_string(Semantic_Actions.START.name(), "DECK PROBABILITY", this.derivation);
                                this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
                            }
                            // work on semantic stack
                            this.semantic_stack.set(semantic_stack_end_index, Semantic_Actions.PROBABILITY);
                            this.semantic_stack.add(Semantic_Actions.DECK);*/
                        }
                        default:
                        {
                            //TODO:finish
                        }
                    }
                    break;
                }
                //TODO:finish
                default:
                {
                    throw new IllegalStateException("Exception; unsupported Semantic_Actions found: " + switch_value.name());
                }
            }
        } while (no_match);
    }
    
    /**
     * Centralize shared constructor code. Should only be called once by the constructor and never again.
     */
    private void finish_construction()
    {
        this.semantic_stack = new ArrayList<Semantic_Actions>();
        this.semantic_stack.add(Semantic_Actions.START); // starting symbol
        this.syntactical_stack = new ArrayList<Evaluable<U>>();
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void handle_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Semantic_Actions... L_H_S_)
    {
        final int L_H_S_LENGTH = L_H_S_.length;

        //TODO: improve
        if (this.VERBOSE)
        {
            // work on derivation
            StringBuilder result = new StringBuilder();
            result.append(SPECIAL_UNLIKELY_SENTINEL);
            result.append(L_H_S_[0].name());
            result.append(SPECIAL_UNLIKELY_SENTINEL);
            for (int i = 1; i < L_H_S_LENGTH; ++i)
            {
                result.append(" " + SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                result.append(L_H_S_[i].name());
                result.append(SPECIAL_UNLIKELY_SENTINEL);
            }
            Function_Bank.stringbuilder_replace_string_with_string(SPECIAL_UNLIKELY_SENTINEL + TARGET + SPECIAL_UNLIKELY_SENTINEL, result.toString(), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
        }

        // work on semantic stack
        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, L_H_S_[L_H_S_LENGTH - 1]);

        for (int i = L_H_S_LENGTH - 2; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }
}
