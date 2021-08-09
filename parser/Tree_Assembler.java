package parser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import com.gth.function_bank.Function_Bank;
import simulation.Simulation;
import structure.Concrete_Parent;
import structure.Scenario;

/**
<b>
Purpose: assembles the parts of a configuration file for simulating.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-[4, 5]/2021-8-[7, 8]
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
    private ArrayList<Concrete_Parent> syntactical_stack;

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
     * Convenience method for handling errors. Also reports them using {@link #syntactical_error_output}
     * 
     * @param CURRENT_ACTION section that this is called from
     * @param CURRENT_TOKEN {@link #skiperror}
     * @param FOLLOW_SET {@link #skiperror}
     * @return {@link #skiperror(parser.Token.Lexeme_Types, parser.Token.Lexeme_Types...)}
     */
    public boolean convenience_error_handling(final Semantic_Actions CURRENT_ACTION, final Token CURRENT_TOKEN, final Token.Lexeme_Types... FOLLOW_SET)
    {
        if (this.VERBOSE)
            this.syntactical_error_output.println("Error: while top of stack is " + CURRENT_ACTION.name() + " and current Token is " + CURRENT_TOKEN);
        return skiperror(CURRENT_TOKEN.get_type(), FOLLOW_SET);
    }

    /**
     * Implementation of error handling for table driven parser.
     * 
     * @param CURRENT {@link Token} being looked at
     * @param FOLLOW_SET follow set of top of semantic stack being checked
     * @return is current in follow set, answer is result
     */
    public static boolean skiperror(final Token.Lexeme_Types CURRENT, final Token.Lexeme_Types... FOLLOW_SET)
    {
        // pop case, check follow set
        for (int i = 0; i < FOLLOW_SET.length; ++i)
            if (FOLLOW_SET[i] == CURRENT)
                return true;

        // scan case
        return false;
    }

    /**
     * True output of this object.
     * 
     * @return culmination of this class, destroy the object afterwards
     */
    public Simulation<T, U> creat_result()
    {
        return new Simulation<T, U>(this.DECK, this.FOREST);
    }

    /**
     * TODO:rewrite
     * Method for parsing tokens into an abstract syntax tree. Uses all data members.
     * Make sure to call {@link #finish_semantic_stack} once all the {@link Token} are are parsed.
     * @param INPUT current top token being looked at
     * @throws EmptySemanticStackException when internal semantic_stack is empty yet there is another token to parse.
     */
    public void parse(final Token INPUT) throws EmptySemanticStackException
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
                            //TODO: remove comment
                            /*if (this.VERBOSE)
                            {
                                // work on derivation
                                Function_Bank.stringbuilder_replace_string_with_string(Semantic_Actions.START.name(), "DECK PROBABILITY", this.derivation);
                                this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
                            }
                            // work on semantic stack
                            this.semantic_stack.set(semantic_stack_end_index, Semantic_Actions.PROBABILITY);
                            this.semantic_stack.add(Semantic_Actions.DECK);*/
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.START, INPUT))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case DECK:
                {
                    switch (INPUT.get_type())
                    {
                        case DECK_START:
                        {
                            //DECK -> DECK_START SENTINEL_START DECK_LIST SENTINEL_END
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_START.name(), Semantic_Actions.DECK_START, Semantic_Actions.SENTINEL_START,
                                                        Semantic_Actions.DECK_LIST, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECK, INPUT, Token.Lexeme_Types.PROBABILITY_START))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case DECK_START:
                {
                    switch (INPUT.get_type())
                    {
                        case DECK_START:
                        {
                            //DECK_START -> deck list:
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECK_START, INPUT, Token.Lexeme_Types.SENTINEL_START))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case DECK_LIST:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //DECK_LIST -> CARD MORE_CARDS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_LIST.name(), Semantic_Actions.CARD, Semantic_Actions.MORE_CARDS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECK_LIST, INPUT, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case MORE_CARDS:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //MORE_CARDS -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_CARDS.name());
                            break;
                        }
                        case ID:
                        {
                            //MORE_CARDS -> CARD MORE_CARDS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_CARDS.name(), Semantic_Actions.CARD, Semantic_Actions.MORE_CARDS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_CARDS, INPUT, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case CARD:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //CARD -> CARD_NAME ;
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_CARD_POP, Semantic_Actions.CARD.name(), Semantic_Actions.CARD,
                                                            Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CARD, INPUT, Token.Lexeme_Types.ID, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case CARD_NAME:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //CARD_NAME -> id
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.CARD_NAME.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CARD_NAME, INPUT, Token.Lexeme_Types.SEMI_COLON, Token.Lexeme_Types.CONDITION_CARD_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case PROBABILITY:
                {
                    switch (INPUT.get_type())
                    {
                        case PROBABILITY_START:
                        {
                            //PROBABILITY -> PROBABILITY_START SENTINEL_START SCENARIO_LIST SENTINEL_END
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.PROBABILITY.name(), Semantic_Actions.PROBABILITY_START,
                                                        Semantic_Actions.SENTINEL_START, Semantic_Actions.SCENARIO_LIST, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.PROBABILITY, INPUT))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case PROBABILITY_START:
                {
                    switch (INPUT.get_type())
                    {
                        case PROBABILITY_START:
                        {
                            //PROBABILITY_START -> scenarios:
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.PROBABILITY_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.PROBABILITY_START, INPUT, Token.Lexeme_Types.SENTINEL_START))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case SCENARIO_LIST:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //SCENARIO_LIST -> SCENARIO MORE_SCENARIOS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SCENARIO_LIST.name(), Semantic_Actions.SCENARIO,
                                                        Semantic_Actions.MORE_SCENARIOS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO_LIST, INPUT, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case MORE_SCENARIOS:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //MORE_SCENARIOS -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS.name());
                            break;
                        }
                        case ID:
                        {
                            //MORE_SCENARIOS -> SCENARIO MORE_SCENARIOS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS.name(), Semantic_Actions.SCENARIO,
                                                        Semantic_Actions.MORE_SCENARIOS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO_LIST, INPUT, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case SCENARIO:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //SCENARIO -> SCENARIO_NAME assign SENTINEL_START TREE DISPLAY SENTINEL_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.SCENARIO_POP, Semantic_Actions.SCENARIO.name(),
                                                            Semantic_Actions.SCENARIO_NAME, Semantic_Actions.ASSIGN, Semantic_Actions.SENTINEL_START, Semantic_Actions.TREE,
                                                            Semantic_Actions.DISPLAY, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO, INPUT, Token.Lexeme_Types.ID, Token.Lexeme_Types.SENTINEL_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                //TODO:finish
                case SENTINEL_START:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_START:
                        {
                            //SENTINEL_START -> open_brace
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SENTINEL_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_START, INPUT, Token.Lexeme_Types.ID, Token.Lexeme_Types.TREE_START,
                                                                Token.Lexeme_Types.NOT, Token.Lexeme_Types.CONDITION_CARD_START, Token.Lexeme_Types.CONDITION_SCENARIO_START,
                                                                Token.Lexeme_Types.CONDITION_EXPR_START))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case SENTINEL_END:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_START:
                        {
                            //SENTINEL_END -> close_brace
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SENTINEL_END.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_END, INPUT, Token.Lexeme_Types.SEMI_COLON, Token.Lexeme_Types.ID,
                                                                Token.Lexeme_Types.SENTINEL_END, Token.Lexeme_Types.PROBABILITY_START))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                case SCENARIO_NAME:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //SCENARIO_NAME -> id
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.SCENARIO_NAME.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO_NAME, INPUT, Token.Lexeme_Types.ASSIGN, Token.Lexeme_Types.CONDITION_SCENARIO_END))
                            {
                                // remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; // effectively discards current token
                        }
                    }
                    break;
                }
                //TODO:finish
                //DECK_CARD_POP
                //SCENARIO_POP
                default:
                {
                    throw new IllegalStateException("Exception, unsupported Semantic_Actions found: " + switch_value.name());
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
        this.syntactical_stack = new ArrayList<Concrete_Parent>();
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. For the case that a production rule results in an epsilon to be discarded.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param TARGET current symbol being replaced
     */
    private void epsilon_discard_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET)
    {
        // stack maintenance
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(SPECIAL_UNLIKELY_SENTINEL + TARGET + SPECIAL_UNLIKELY_SENTINEL, "", this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
        }
    }

    /**
     * Subroutine to handle derivation part.
     * 
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void derivation_subroutine(final String TARGET, final Semantic_Actions... L_H_S_)
    {
        //TODO: improve
        if (this.VERBOSE)
        {
            // work on derivation
            StringBuilder result = new StringBuilder();
            result.append(SPECIAL_UNLIKELY_SENTINEL);
            result.append(L_H_S_[0].name());
            result.append(SPECIAL_UNLIKELY_SENTINEL);
            for (int i = 1; i < L_H_S_.length; ++i)
            {
                result.append(" " + SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                result.append(L_H_S_[i].name());
                result.append(SPECIAL_UNLIKELY_SENTINEL);
            }
            Function_Bank.stringbuilder_replace_string_with_string(SPECIAL_UNLIKELY_SENTINEL + TARGET + SPECIAL_UNLIKELY_SENTINEL, result.toString(), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
        }
    }

    /**
     * Subroutine for matching terminals and then discarding them.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET {@link Token.Lexeme_Types} to replace
     * @param CURRENT_NODE values pertaining to node to be created
     */
    private void match_litteral_discard_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Token CURRENT_NODE)
    {
        // stack maintenance
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(SPECIAL_UNLIKELY_SENTINEL + TARGET + SPECIAL_UNLIKELY_SENTINEL, CURRENT_NODE.get_lexeme(), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
        }
    }

    /**
     * Subroutine for matching terminals and then adds them to {@link #syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link #semantic_stack}
     * @param TARGET {@link Token.Lexeme_Types} to replace
     * @param CURRENT_NODE values pertaining to node to be created
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Token CURRENT_NODE)
    {
        // stack maintenance
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
        this.syntactical_stack.add(new Concrete_Parent(CURRENT_NODE.get_lexeme()));
        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(SPECIAL_UNLIKELY_SENTINEL + TARGET + SPECIAL_UNLIKELY_SENTINEL, CURRENT_NODE.get_lexeme(),
                                                                   this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString());//output derivation
        }
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
        //TODO: improve
        this.derivation_subroutine(TARGET, L_H_S_);

        // work on semantic stack
        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, L_H_S_[L_H_S_.length - 1]);

        for (int i = L_H_S_.length - 2; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }

    /**
     * Similar to {@link #handle_case_subroutine(int, String, Semantic_Actions...)}, except that it adds a {@link Semantic_Actions} to {@link #semantic_stack} without affecting {@link #derivation}.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param POP {@link Semantic_Actions} relating to stack popping
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void handle_pop_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final Semantic_Actions POP, final String TARGET, final Semantic_Actions... L_H_S_)
    {
        //TODO: improve
        this.derivation_subroutine(TARGET, L_H_S_);

        // work on semantic stack
        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, POP);

        for (int i = L_H_S_.length - 1; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }
}
