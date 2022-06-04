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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import com.gth.function_bank.Function_Bank;
import simulation.Simulation;
import starting_point.Starting_Point;
import structure.And_Operator_Node;
import structure.Base_Card;
import structure.Deck_Card;
import structure.Evaluable;
import structure.Leaf_Node;
import structure.Not_Operator_Node;
import structure.Or_Operator_Node;
import structure.Scenario;
import structure.Xor_Operator_Node;

/**
<b>
Purpose: assembles the parts of a configuration file for simulating.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-[4, 5]/2021-8-[7, 13]/2021-8-17/2021-8-19/2021-8-[22, 23]/2021-11-14
</b>
*/

//https://smlweb.cpsc.ucalgary.ca/vital-stats.php?grammar=START+-%3E+DECK+PROBABILITY+.%0D%0A%0D%0ADECK+-%3E+DECK_START+SENTINEL_START+DECK_LIST+SENTINEL_END.%0D%0ADECK_START+-%3E+deck+list%3A+.%0D%0ADECK_LIST+-%3E+CARD+MORE_CARDS+.%0D%0AMORE_CARDS+-%3E+CARD+MORE_CARDS+%7C+.%0D%0ACARD+-%3E+CARD_NAME+SEMI_COLON+.%0D%0ACARD_NAME+-%3E+id+.%0D%0A%0D%0APROBABILITY+-%3E+PROBABILITY_START+SENTINEL_START+SCENARIO_LIST+SENTINEL_END+.%0D%0APROBABILITY_START+-%3E+scenarios%3A+.%0D%0A%0D%0ASCENARIO_LIST+-%3E+SCENARIO+MORE_SCENARIOS+.%0D%0AMORE_SCENARIOS+-%3E+SCENARIO+MORE_SCENARIOS+%7C+.%0D%0A%0D%0ASCENARIO+-%3E+SCENARIO_NAME+ASSIGN+SENTINEL_START+TREE+DISPLAY+SENTINEL_END+.%0D%0ASCENARIO_NAME+-%3E+id+.%0D%0A%0D%0ATREE+-%3E+TREE_START+ASSIGN+SENTINEL_START+EXPR+SENTINEL_END+SEMI_COLON+.%0D%0ATREE_START+-%3E+scenario+.%0D%0A%0D%0AEXPR+-%3E+UNARY_EXPR+BINARY_EXPR+.%0D%0A%0D%0AUNARY_EXPR+-%3E+UNARY_OPERATOR+UNARY_EXPR+%7C+PRIMARY_EXPR+.%0D%0AUNARY_OPERATOR+-%3E+not+.%0D%0A%0D%0APRIMARY_EXPR+-%3E+CONDITION_HAND_CARD_START+CARD_NAME+CONDITION_HAND_CARD_END+%7C+CONDITION_SCENARIO_START+SCENARIO_NAME+CONDITION_SCENARIO_END+%7C+CONDITION_EXPR_START+EXPR+CONDITION_EXPR_END+.%0D%0A%0D%0ACONDITION_CARD_START+-%3E+open_bracket+.%0D%0ACONDITION_CARD_END+-%3E+close_bracket+.%0D%0ACONDITION_SCENARIO_START+-%3E+less_then+.%0D%0ACONDITION_SCENARIO_END+-%3E+greater_then+.%0D%0ACONDITION_EXPR_START+-%3E+open_parenthesis+.%0D%0ACONDITION_EXPR_END+-%3E+close_parenthesis+.%0D%0A%0D%0ABINARY_EXPR+-%3E+BINARY_OPERATOR+UNARY_EXPR+BINARY_EXPR+%7C+.%0D%0ABINARY_OPERATOR+-%3E+and+%7C+or+%7C+xor+.%0D%0A%0D%0ADISPLAY+-%3E+DISPLAY_START+ASSIGN+DISPLAY_VALUE+SEMI_COLON+%7C+.%0D%0ADISPLAY_START+-%3E+display+.%0D%0ADISPLAY_VALUE+-%3E+true+%7C+false+.%0D%0A%0D%0ASENTINEL_START+-%3E+open_brace+.%0D%0ASENTINEL_END+-%3E+close_brace+.%0D%0AASSIGN+-%3E+assign+.%0D%0ASEMI_COLON+-%3E+%3B.
//https://smlweb.cpsc.ucalgary.ca/ll1-table.php?grammar=START+-%3E+DECK+PROBABILITY+.%0A%0ADECK+-%3E+DECK_START+SENTINEL_START+DECK_LIST+SENTINEL_END.%0ADECK_START+-%3E+deck+list%3A+.%0ADECK_LIST+-%3E+CARD+MORE_CARDS+.%0AMORE_CARDS+-%3E+CARD+MORE_CARDS+%7C+.%0ACARD+-%3E+CARD_NAME+SEMI_COLON+.%0ACARD_NAME+-%3E+id+.%0A%0APROBABILITY+-%3E+PROBABILITY_START+SENTINEL_START+SCENARIO_LIST+SENTINEL_END+.%0APROBABILITY_START+-%3E+scenarios%3A+.%0A%0ASCENARIO_LIST+-%3E+SCENARIO+MORE_SCENARIOS+.%0AMORE_SCENARIOS+-%3E+SCENARIO+MORE_SCENARIOS+%7C+.%0A%0ASCENARIO+-%3E+SCENARIO_NAME+ASSIGN+SENTINEL_START+TREE+DISPLAY+SENTINEL_END+.%0ASCENARIO_NAME+-%3E+id+.%0A%0ATREE+-%3E+TREE_START+ASSIGN+SENTINEL_START+EXPR+SENTINEL_END+SEMI_COLON+.%0ATREE_START+-%3E+scenario+.%0A%0AEXPR+-%3E+UNARY_EXPR+BINARY_EXPR+.%0A%0AUNARY_EXPR+-%3E+UNARY_OPERATOR+UNARY_EXPR+%7C+PRIMARY_EXPR+.%0AUNARY_OPERATOR+-%3E+not+.%0A%0APRIMARY_EXPR+-%3E+CONDITION_HAND_CARD_START+CARD_NAME+CONDITION_HAND_CARD_END+%7C+CONDITION_SCENARIO_START+SCENARIO_NAME+CONDITION_SCENARIO_END+%7C+CONDITION_EXPR_START+EXPR+CONDITION_EXPR_END+.%0A%0ACONDITION_CARD_START+-%3E+open_bracket+.%0ACONDITION_CARD_END+-%3E+close_bracket+.%0ACONDITION_SCENARIO_START+-%3E+less_then+.%0ACONDITION_SCENARIO_END+-%3E+greater_then+.%0ACONDITION_EXPR_START+-%3E+open_parenthesis+.%0ACONDITION_EXPR_END+-%3E+close_parenthesis+.%0A%0ABINARY_EXPR+-%3E+BINARY_OPERATOR+UNARY_EXPR+BINARY_EXPR+%7C+.%0ABINARY_OPERATOR+-%3E+and+%7C+or+%7C+xor+.%0A%0ADISPLAY+-%3E+DISPLAY_START+ASSIGN+DISPLAY_VALUE+SEMI_COLON+%7C+.%0ADISPLAY_START+-%3E+display+.%0ADISPLAY_VALUE+-%3E+true+%7C+false+.%0A%0ASENTINEL_START+-%3E+open_brace+.%0ASENTINEL_END+-%3E+close_brace+.%0AASSIGN+-%3E+assign+.%0ASEMI_COLON+-%3E+%3B.&substs=
public class Tree_Assembler
{
    /**
     * Determines whether to output files, true for make files and false for no file creation.
     */
    public final boolean VERBOSE;

    /**
     * Special sequence meant to surround the production rules such that they are less likely to accidentally appear as a token. Though the use guarantees that they will never be mistaken for a token.
     */
    public static final String SPECIAL_UNLIKELY_SENTINEL = "&GTH&";

    /**
     * Derivation of the input.
     */
    private StringBuilder derivation;

    /**
     * stack like structure used by {@link #parse(Token)}
     */
    private ArrayList<Semantic_Actions> semantic_stack;

    /**
     * Represents stack like structure for building the {@link Scenario} and deck list, used by {@link Tree_Assembler#parse(Token)}
     */
    private ArrayList<Evaluable> syntactical_stack;

    /**
     * Main deck which the hand will be generated from.
     */
    private final ArrayList<Deck_Card> DECK;

    /**
     * Stores the generated scenarios.
     */
    private final HashMap<String, Scenario> FOREST;

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
        this.DECK = new ArrayList<Deck_Card>(40); //predicted average deck size
        this.FOREST = new HashMap<String, Scenario>(10); //predicted average number of scenarios
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
        this.DECK = new ArrayList<Deck_Card>(EXPECTED_DECK_SIZE);
        this.FOREST = new HashMap<String, Scenario>(EXPECTED_SCENARIO_COUNT);
        this.derivation = new StringBuilder(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + Semantic_Actions.START.name() + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
        this.syntactical_error_output = SYNTACTICAL_ERROR_OUTPUT;
        this.syntactical_derivation_output = SYNTACTICAL_DERIVATION_OUTPUT;
        this.finish_construction();
    }

    /**
     * To be called once there are no more tokens to parse. Meant to check that all operations have been completed. As it is expected that once no more tokens are coming in (and no errors have occurred), that this function return true.
     * 
     * @return true for expected state, false for undetected critical error or parsing is incomplete.
     */
    public boolean check_stacks_are_empty()
    {
        return this.semantic_stack.isEmpty() && this.syntactical_stack.isEmpty();
    }

    /**
     * Convenience method for handling errors. Also reports them using {@link #syntactical_error_output}
     * 
     * @param CURRENT_ACTION section that this is called from
     * @param CURRENT_TOKEN {@link #skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
     * @param FOLLOW_SET {@link #skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
     * 
     * @return {@link #skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
     */
    public boolean convenience_error_handling(final Semantic_Actions CURRENT_ACTION, final Token CURRENT_TOKEN, final Lexeme_Types... FOLLOW_SET)
    {
        if (this.VERBOSE)
            this.syntactical_error_output.println("Error: while top of stack is " + CURRENT_ACTION.name() + " and current Token is " + CURRENT_TOKEN);
        return Tree_Assembler.skiperror(CURRENT_TOKEN.get_type(), FOLLOW_SET);
    }

    /**
     * Implementation of error handling for table driven parser.
     * 
     * @param CURRENT {@link Token} being looked at
     * @param FOLLOW_SET follow set of top of semantic stack being checked
     * 
     * @return is current in follow set, answer is result
     */
    public static boolean skiperror(final Lexeme_Types CURRENT, final Lexeme_Types... FOLLOW_SET)
    {
        //pop case, check follow set
        for (int i = 0; i < FOLLOW_SET.length; ++i)
            if (FOLLOW_SET[i] == CURRENT)
                return true;

        //scan case
        return false;
    }

    /**
     * True output of this object.
     * 
     * @return culmination of this class, destroy this object afterwards (as it has served its function)
     */
    public Simulation create_result()
    {
        return new Simulation(this.DECK, this.FOREST.values());
    }

    /**
     * Method for parsing tokens into a boolean like postfix notation tree.
     * Make sure to call {@link #check_stacks_are_empty()} once all the {@link Token} are are parsed to check the state of things.
     * 
     * @param INPUT current top token being looked at
     * 
     * @throws EmptySemanticStackException when internal {@link #semantic_stack} is empty yet there is another token to parse. Fatal error.
     * @throws IllegalStateException unexpected non-recoverable error has occurred
     */
    public void parse(final Token INPUT) throws EmptySemanticStackException, IllegalStateException
    {
        boolean no_match = true;
        int semantic_stack_end_index;
        Semantic_Actions switch_value;

        do
        {
            semantic_stack_end_index = this.semantic_stack.size() - 1;

            if (semantic_stack_end_index == -1)
                throw new EmptySemanticStackException("Error: semantic stack is empty yet received the following Token: " + INPUT);

            switch_value = this.semantic_stack.get(semantic_stack_end_index);

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
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.START, INPUT))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK.name(), Semantic_Actions.DECK_START, Semantic_Actions.SENTINEL_START,
                                                        Semantic_Actions.DECK_LIST, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECK, INPUT, Lexeme_Types.PROBABILITY_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.DECK_START, INPUT, Lexeme_Types.SENTINEL_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.DECK_LIST, INPUT, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            this.handle_recursive_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_CARDS.name(), Semantic_Actions.CARD, Semantic_Actions.MORE_CARDS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_CARDS, INPUT, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_CARD_POP, Semantic_Actions.CARD.name(), Semantic_Actions.CARD_NAME,
                                                            Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CARD, INPUT, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.CARD_NAME, INPUT, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_HAND_CARD_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.PROBABILITY_START, INPUT, Lexeme_Types.SENTINEL_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO_LIST, INPUT, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            this.handle_recursive_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS.name(), Semantic_Actions.SCENARIO,
                                                                  Semantic_Actions.MORE_SCENARIOS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_SCENARIOS, INPUT, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO, INPUT, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
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
                            if (this.convenience_error_handling(Semantic_Actions.SCENARIO_NAME, INPUT, Lexeme_Types.ASSIGN, Lexeme_Types.CONDITION_SCENARIO_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case TREE:
                {
                    switch (INPUT.get_type())
                    {
                        case TREE_START:
                        {
                            //TREE -> TREE_START assign SENTINEL_START EXPR SENTINEL_END ;
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.TREE.name(), Semantic_Actions.TREE_START, Semantic_Actions.ASSIGN,
                                                        Semantic_Actions.SENTINEL_START, Semantic_Actions.EXPR, Semantic_Actions.SENTINEL_END, Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.TREE, INPUT, Lexeme_Types.DISPLAY_START, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case TREE_START:
                {
                    switch (INPUT.get_type())
                    {
                        case TREE_START:
                        {
                            //TREE_START -> scenario
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.TREE_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.TREE_START, INPUT, Lexeme_Types.ASSIGN))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_EXPR_START:
                        case CONDITION_SCENARIO_START:
                        case CONDITION_HAND_CARD_START:
                        case NOT:
                        {
                            //Counting on BINARY_EXPR to either self terminate or perform the semantic popping to result in a 'single' piece.
                            //EXPR -> UNARY_EXPR BINARY_EXPR
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.EXPR.name(), Semantic_Actions.UNARY_EXPR, Semantic_Actions.BINARY_EXPR);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.EXPR, INPUT, Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case UNARY_EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_EXPR_START:
                        case CONDITION_SCENARIO_START:
                        case CONDITION_HAND_CARD_START:
                        {
                            //UNARY_EXPR -> PRIMARY_EXPR
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.UNARY_EXPR.name(), Semantic_Actions.PRIMARY_EXPR);
                            break;
                        }
                        case NOT:
                        {
                            //UNARY_EXPR -> UNARY_OPERATOR UNARY_EXPR
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.UNARY_POP, Semantic_Actions.UNARY_EXPR.name(),
                                                            Semantic_Actions.UNARY_OPERATOR, Semantic_Actions.UNARY_EXPR);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.UNARY_EXPR, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR, Lexeme_Types.SENTINEL_END,
                                                                Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case UNARY_OPERATOR:
                {
                    switch (INPUT.get_type())
                    {
                        case NOT:
                        {
                            //UNARY_OPERATOR -> not
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.UNARY_OPERATOR.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.UNARY_OPERATOR, INPUT, Lexeme_Types.NOT, Lexeme_Types.CONDITION_HAND_CARD_START,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case PRIMARY_EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_EXPR_START:
                        {
                            //PRIMARY_EXPR -> CONDITION_EXPR_START EXPR CONDITION_EXPR_END
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.PRIMARY_EXPR.name(), Semantic_Actions.CONDITION_EXPR_START,
                                                        Semantic_Actions.EXPR, Semantic_Actions.CONDITION_EXPR_END);
                            break;
                        }
                        case CONDITION_SCENARIO_START:
                        {
                            //PRIMARY_EXPR -> CONDITION_SCENARIO_START SCENARIO_NAME CONDITION_SCENARIO_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_SCENARIO_POP, Semantic_Actions.PRIMARY_EXPR.name(),
                                                            Semantic_Actions.CONDITION_SCENARIO_START, Semantic_Actions.SCENARIO_NAME, Semantic_Actions.CONDITION_SCENARIO_END);
                            break;
                        }
                        case CONDITION_HAND_CARD_START:
                        {
                            //PRIMARY_EXPR -> CONDITION_HAND_CARD_START CARD_NAME CONDITION_HAND_CARD_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_HAND_CARD_POP, Semantic_Actions.PRIMARY_EXPR.name(),
                                                            Semantic_Actions.CONDITION_HAND_CARD_START, Semantic_Actions.CARD_NAME, Semantic_Actions.CONDITION_HAND_CARD_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.PRIMARY_EXPR, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_HAND_CARD_START:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_HAND_CARD_START:
                        {
                            //CONDITION_HAND_CARD_START -> [
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_HAND_CARD_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_HAND_CARD_START, INPUT, Lexeme_Types.ID))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_HAND_CARD_END:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_HAND_CARD_END:
                        {
                            //CONDITION_HAND_CARD_START -> ]
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_HAND_CARD_END.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_HAND_CARD_END, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_SCENARIO_START:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_SCENARIO_START:
                        {
                            //CONDITION_SCENARIO_START -> <
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_SCENARIO_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_SCENARIO_START, INPUT, Lexeme_Types.ID))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_SCENARIO_END:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_SCENARIO_END:
                        {
                            //CONDITION_SCENARIO_END -> >
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_SCENARIO_END.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_SCENARIO_END, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_EXPR_START:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_EXPR_START:
                        {
                            //CONDITION_EXPR_START -> (
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_EXPR_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_EXPR_START, INPUT, Lexeme_Types.NOT, Lexeme_Types.CONDITION_HAND_CARD_START,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case CONDITION_EXPR_END:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_EXPR_END:
                        {
                            //CONDITION_HAND_CARD_START -> )
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_EXPR_END.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_EXPR_END, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case BINARY_EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        case CONDITION_EXPR_END:
                        {
                            //BINARY_EXPR -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.BINARY_EXPR.name());
                            break;
                        }
                        case XOR:
                        case OR:
                        case AND:
                        {
                            //BINARY_EXPR -> BINARY_OPERATOR UNARY_EXPR BINARY_EXPR
                            this.derivation_subroutine(Semantic_Actions.BINARY_EXPR.name(), Semantic_Actions.BINARY_OPERATOR, Semantic_Actions.UNARY_EXPR,
                                                       Semantic_Actions.BINARY_EXPR);
                            //Not setting current semantic_stack_end_index to Semantic_Actions.BINARY_EXPR, due to already being that value.
                            this.semantic_stack.add(Semantic_Actions.BINARY_POP_3);
                            this.semantic_stack.add(Semantic_Actions.UNARY_EXPR);
                            this.semantic_stack.add(Semantic_Actions.BINARY_OPERATOR);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.BINARY_EXPR, INPUT, Lexeme_Types.SENTINEL_END, Lexeme_Types.CONDITION_EXPR_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case BINARY_OPERATOR:
                {
                    switch (INPUT.get_type())
                    {
                        case XOR:
                        case OR:
                        case AND:
                        {
                            //BINARY_OPERATOR -> xor
                            //BINARY_OPERATOR -> or
                            //BINARY_OPERATOR -> and
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.BINARY_OPERATOR.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.BINARY_OPERATOR, INPUT, Lexeme_Types.NOT, Lexeme_Types.CONDITION_HAND_CARD_START,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case DISPLAY:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //DISPLAY -> &epsilon
                            this.epsilon_add_case_subroutine(semantic_stack_end_index, Semantic_Actions.DISPLAY.name());
                            break;
                        }
                        case DISPLAY_START:
                        {
                            //DISPLAY -> DISPLAY_START assign DISPLAY_VALUE ;
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.DISPLAY.name(), Semantic_Actions.DISPLAY_START, Semantic_Actions.ASSIGN,
                                                        Semantic_Actions.DISPLAY_VALUE, Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DISPLAY, INPUT, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case DISPLAY_START:
                {
                    switch (INPUT.get_type())
                    {
                        case DISPLAY_START:
                        {
                            //DISPLAY_START -> display
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.DISPLAY_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DISPLAY_START, INPUT, Lexeme_Types.ASSIGN))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case DISPLAY_VALUE:
                {
                    switch (INPUT.get_type())
                    {
                        case FALSE:
                        case TRUE:
                        {
                            //DISPLAY_VALUE -> false
                            //DISPLAY_VALUE -> true
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.DISPLAY_VALUE.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DISPLAY_VALUE, INPUT, Lexeme_Types.SEMI_COLON))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
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
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_START, INPUT, Lexeme_Types.ID, Lexeme_Types.TREE_START, Lexeme_Types.NOT,
                                                                Lexeme_Types.CONDITION_HAND_CARD_START, Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case SENTINEL_END:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //SENTINEL_END -> close_brace
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SENTINEL_END.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_END, INPUT, Lexeme_Types.SEMI_COLON, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END,
                                                                Lexeme_Types.PROBABILITY_START))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case ASSIGN:
                {
                    switch (INPUT.get_type())
                    {
                        case ASSIGN:
                        {
                            //ASSIGN -> =
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.ASSIGN.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.ASSIGN, INPUT, Lexeme_Types.SENTINEL_START, Lexeme_Types.TRUE, Lexeme_Types.FALSE))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case SEMI_COLON:
                {
                    switch (INPUT.get_type())
                    {
                        case SEMI_COLON:
                        {
                            //SEMI_COLON -> ;
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SEMI_COLON.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SEMI_COLON, INPUT, Lexeme_Types.DISPLAY_START, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END))
                            {
                                //remove semantic_stack top
                                this.semantic_stack.remove(semantic_stack_end_index);
                                continue;
                            }
                            else
                                return; //effectively discards current token
                        }
                    }
                    break;
                }
                case DECK_CARD_POP:
                {
                    //add card to deck
                    this.DECK.add(new Deck_Card(this.syntactical_stack.remove(syntactical_stack.size() - 1).NAME));
                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case SCENARIO_POP:
                {
                    //create scenario
                    //expected form: [scenario name, tree, display]
                    final String SCEANRIO_NAME = this.syntactical_stack.remove(0).NAME;

                    if (this.syntactical_stack.size() == 2)
                    {
                        if (!this.FOREST.containsKey(SCEANRIO_NAME))
                            this.FOREST.put(SCEANRIO_NAME, new Scenario(!Tokenizer.FALSE.matcher(this.syntactical_stack.remove(1).NAME).matches(), SCEANRIO_NAME,
                                                                        this.syntactical_stack.remove(0)));
                        else
                        {
                            //clear remaining stack stuff
                            this.syntactical_stack.clear();
                            if (this.VERBOSE)
                                this.syntactical_error_output.println("Error, repeated scenario name \"" + SCEANRIO_NAME +
                                                                      "\", discarding this scenario. Detected not before line number " + INPUT.get_line_number());
                        }
                    }
                    else
                    {
                        //panic
                        if (this.VERBOSE)
                            this.syntactical_error_output.println("Current scenario \"" + SCEANRIO_NAME + "\" found around line " + INPUT.get_line_number() +
                                                                  " is malformed. Discarding this scenario.");
                        this.syntactical_stack.clear();
                    }

                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case UNARY_POP:
                {
                    //handle unary operator
                    final int RESULT_LOCATION = this.syntactical_stack.size() - 2;
                    final String UNARY_OPERATOR = this.syntactical_stack.remove(RESULT_LOCATION).NAME;

                    if (Tokenizer.NOT.matcher(UNARY_OPERATOR).matches())
                        this.syntactical_stack.set(RESULT_LOCATION, new Not_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION)));
                    else
                        throw new IllegalStateException("Error: should be impossible, \"" + UNARY_OPERATOR + "\" did not match any expected value. Should be a unary operator.");

                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case CONDITION_HAND_CARD_POP:
                {
                    //card condition in scenario
                    final int SYNTACTICAL_STACK_LAST_INDEX = this.syntactical_stack.size() - 1;
                    final String CARD_NAME = this.syntactical_stack.get(SYNTACTICAL_STACK_LAST_INDEX).NAME;
                    this.syntactical_stack.set(SYNTACTICAL_STACK_LAST_INDEX, new Leaf_Node<Base_Card>(CARD_NAME, new Base_Card(CARD_NAME)));
                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case CONDITION_SCENARIO_POP:
                {
                    //scenario condition in scenario
                    int syntactical_stack_last_index = this.syntactical_stack.size() - 1;
                    final String REFERENCED_SCEANRIO_NAME = this.syntactical_stack.get(syntactical_stack_last_index).NAME;
                    final Scenario POTENTIONAL_SCENARIO = this.FOREST.get(REFERENCED_SCEANRIO_NAME);
                    
                    if (POTENTIONAL_SCENARIO != null)
                    {
                        this.syntactical_stack.set(syntactical_stack_last_index, POTENTIONAL_SCENARIO);
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_scenario_error(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS, INPUT.get_line_number(),
                                                   "Error: undefined referenced, " + REFERENCED_SCEANRIO_NAME + ", at line ");
                    break;
                }
                case BINARY_POP_3:
                {
                    //create binary expression out of 3 parts from syntactical_stack
                    assert (this.syntactical_stack.size() >= 4); //Well expected to be 4, {scenario name, left operand, binary operator, right operand}.
                    final int SYNTACTICAL_TARGET_INDEX = this.syntactical_stack.size() - 2; //First time will be the operator, second time will be right operand due to the operator's removal.
                    final int RESULT_LOCATION = SYNTACTICAL_TARGET_INDEX - 1;
                    final String BINARY_OPERATOR = this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX).NAME;

                    if (Tokenizer.AND.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.set(RESULT_LOCATION, new And_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION), this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX)));
                    else if (Tokenizer.OR.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.set(RESULT_LOCATION, new Or_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION), this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX)));
                    else if (Tokenizer.XOR.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.set(RESULT_LOCATION, new Xor_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION), this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX)));
                    else
                    {
                        this.handle_scenario_error(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS, INPUT.get_line_number(),
                                                   "Error: \"" + BINARY_OPERATOR + "\" did not match any expected value. Should be a binary operator. Possibly caused by a missing operand. Error found at line ");
                        break;
                    }

                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                default:
                {
                    throw new IllegalStateException("Exception, unsupported Semantic_Actions found: " + switch_value.name());
                }
            }
        } while (no_match);
    }

    /**
     * Out puts decklist that was read.
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it
     */
    public void print_out_decklist(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        final File OUTPUT_FILE = Starting_Point.add_file_extension(true, Starting_Point.DECKLIST_EXTENSION, PARTIAL_OUTPUT_DIRECTORY);
        try
        {
            OUTPUT_FILE.createNewFile(); //Try creating file first before bothering to assemble its contents.

            final StringBuilder ASSEMBLE_DECKLIST = new StringBuilder("deck list:\n{\n");
            for (Base_Card card : this.DECK)
            {
                ASSEMBLE_DECKLIST.append(card.NAME);
                ASSEMBLE_DECKLIST.append(";\n");
            }
            ASSEMBLE_DECKLIST.append("}\n\n");

            try (final PrintWriter DECKLIST_OUTPUT = new PrintWriter(OUTPUT_FILE))
            {
                DECKLIST_OUTPUT.print(ASSEMBLE_DECKLIST.toString());
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage() + "\nError caused with following path: " + OUTPUT_FILE.getAbsolutePath() + ", thus could not output its decklist.");
        }
    }

    /**
     * Out puts created scenarios, each to their own file in dot format.
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it
     */
    public void print_out_scenarios(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        this.FOREST.values().parallelStream().forEach(schema ->
        {
            final File OUTPUT_FILE = Starting_Point.add_file_extension(true, Starting_Point.SYNTACTICAL_OUT_SCENARIO_FILE_EXTENSION,
                                                                       PARTIAL_OUTPUT_DIRECTORY + " " + Starting_Point.remove_illegal_char_file_name(schema.NAME));
            try
            {
                OUTPUT_FILE.createNewFile();
                try (final PrintWriter TEMP_WRITER = new PrintWriter(OUTPUT_FILE))
                {
                    TEMP_WRITER.println(Evaluable.print_whole_subtree(schema.TREE_CONDITION));
                }
            }
            catch (IOException ex)
            {
                System.err.println(ex.getMessage() + "\nError caused with following path: " + OUTPUT_FILE.getAbsolutePath() +
                                   ", thus could not output its corresponding scenario as a dot file.");
            }
        });
    }

    /**
     * Convenience method which effectively calls {@link #print_out_decklist} and then {@link #print_out_scenarios}
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it.ocation to output files to, expected to be ready to have extensions
     */
    public void print_out_results(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        this.print_out_decklist(PARTIAL_OUTPUT_DIRECTORY);
        this.print_out_scenarios(PARTIAL_OUTPUT_DIRECTORY);
    }

    /**
     * Centralize shared constructor code. Should only be called once by the constructor and never again.
     */
    private void finish_construction()
    {
        this.semantic_stack = new ArrayList<Semantic_Actions>();
        this.semantic_stack.add(Semantic_Actions.START); //starting symbol
        this.syntactical_stack = new ArrayList<Evaluable>();
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. For the case that a production rule results in an epsilon to be discarded.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param TARGET current symbol being replaced
     */
    private void epsilon_discard_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET)
    {
        this.match_literal_discard_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, "");
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. For the case that a production rule results in an epsilon to be discarded.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param TARGET current symbol being replaced
     */
    private void epsilon_add_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET)
    {
        //stack maintenance
        this.syntactical_stack.add(new Evaluable("NULL"));
        this.epsilon_discard_case_subroutine(SEMANTIC_STACK_END_INDEX, TARGET);
    }

    /**
     * Subroutine to handle derivation part.
     * 
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void derivation_subroutine(final String TARGET, final Semantic_Actions... L_H_S_)
    {
        if (this.VERBOSE)
        {
            //work on derivation
            StringBuilder result = new StringBuilder(32);
            for (int i = 0; i < L_H_S_.length; ++i)
            {
                switch (L_H_S_[i])
                {
                    case DECK_START:
                    case DECK_LIST:
                    {
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + "\n"); //combined at compile time
                        break;
                    }
                    case CARD:
                    {
                        result.append("\n" + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
                        break;
                    }
                    case PROBABILITY_START:
                    {
                        result.append("\n\n" + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + "\n"); //combined at compile time
                        break;
                    }
                    case SCENARIO:
                    {
                        result.append("\n" + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + "\n"); //combined at compile time
                        break;
                    }
                    case UNARY_OPERATOR:
                    {
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + " "); //combined at compile time
                        break;
                    }
                    case BINARY_OPERATOR:
                    case ASSIGN:
                    {
                        result.append(" " + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + " "); //combined at compile time
                        break;
                    }
                    default:
                    {
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
                        result.append(L_H_S_[i].name());
                        result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
                        break;
                    }
                }
            }
            Function_Bank.stringbuilder_replace_string_with_string(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + TARGET + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL, result.toString(), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString() + "\n");//output derivation
        }
    }

    /**
     * Subroutine for matching terminals and then discarding them.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link #semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_LEXEME of the {@link Token} to be discarded
     */
    private void match_literal_discard_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final String CURRENT_LEXEME)
    {
        //stack maintenance
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + TARGET + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL, CURRENT_LEXEME, this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString() + "\n");//output derivation
        }
    }

    /**
     * Subroutine for matching terminals and then discarding them.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_NODE values pertaining to node to be created
     */
    private void match_litteral_discard_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Token CURRENT_NODE)
    {
        this.match_literal_discard_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, CURRENT_NODE.get_lexeme());
    }

    /**
     * Subroutine for matching terminals and then adds them to {@link #syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link #semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_LEXEME of the {@Link Token} to be created
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final String CURRENT_LEXEME)
    {
        //stack maintenance
        this.syntactical_stack.add(new Evaluable(CURRENT_LEXEME));
        this.match_literal_discard_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, CURRENT_LEXEME);
    }

    /**
     * Subroutine for matching terminals and then adds them to {@link #syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link #semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_NODE values pertaining to node to be created
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Token CURRENT_NODE)
    {
        this.match_litteral_add_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, CURRENT_NODE.get_lexeme());
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. Essentially the same as {@link #handle_case_subroutine(int, String, Semantic_Actions...)}, except that 
     * {@code this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, L_H_S_[L_H_S_.length - 1]);} is omitted as that would do nothing.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void handle_recursive_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Semantic_Actions... L_H_S_)
    {
        this.derivation_subroutine(TARGET, L_H_S_);

        //work on semantic stack
        for (int i = L_H_S_.length - 2; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
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
        this.derivation_subroutine(TARGET, L_H_S_);

        //work on semantic stack
        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, L_H_S_[L_H_S_.length - 1]);

        for (int i = L_H_S_.length - 2; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }

    /**
     * Handles critical syntax errors relating to scenarios. Removes {@link #semantic_stack} values until STOPPING_POINT is reached (not inclusive). Also {@link #syntactical_stack} is cleared.
     * 
     * @param semantic_stack_end_index the last {@link #semantic_stack}
     * @param STOPPING_POINT is the {@link Semantic_Actions} to remove up to
     * @param LINE_NUMBER of where the error occurs
     * @param ERROR_MESSAGE to output
     */
    private void handle_scenario_error(int semantic_stack_end_index, final Semantic_Actions STOPPING_POINT, final long LINE_NUMBER, final String ERROR_MESSAGE)
    {
        //panic
        if (this.VERBOSE)
            this.syntactical_error_output.println(ERROR_MESSAGE + LINE_NUMBER + ", discarding whole scenario.");

        //remove current secnario's semantic actions
        do
        {
            this.semantic_stack.remove(semantic_stack_end_index);
            --semantic_stack_end_index;
        } while (this.semantic_stack.get(semantic_stack_end_index) != STOPPING_POINT && semantic_stack_end_index > -1);

        //add finishing scenario stuff to not have issues
        this.semantic_stack.add(Semantic_Actions.SENTINEL_END);
        this.semantic_stack.add(Semantic_Actions.SEMI_COLON);
        this.semantic_stack.add(Semantic_Actions.SENTINEL_END);

        //remove traces of incomplete scenario
        this.syntactical_stack.clear(); //Deck cards are popped and scenarios are popped when done, thus only stuff in stack is current scenario.
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
        this.derivation_subroutine(TARGET, L_H_S_);

        //work on semantic stack
        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, POP);

        for (int i = L_H_S_.length - 1; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }
}
