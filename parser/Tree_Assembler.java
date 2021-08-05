package parser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import structure.Evaluable;
import structure.Scenario;

/**
<b>
Purpose: assembles the parts of a configuration file for simulating.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04
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
        this.derivation = new StringBuilder(Semantic_Actions.START.name());
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
    public void parse(final Token INPUT) //throws EmptySemanticStackException, TerminalMatchException
    {
        //TODO: write
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
}
