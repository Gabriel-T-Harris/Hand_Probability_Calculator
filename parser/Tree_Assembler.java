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
import java.util.List;
import com.gth.function_bank.Function_Bank;
import simulation.Simulation;
import simulation.special_ability.Game_State;
import simulation.special_ability.Special_Ability_Banish;
import simulation.special_ability.Special_Ability_Base;
import simulation.special_ability.Special_Ability_Draw;
import simulation.special_ability.Special_Ability_Manager;
import simulation.special_ability.Special_Ability_Mill;
import simulation.special_ability.Special_Ability_Reasoning;
import starting_point.Starting_Point;
import structure.And_Operator_Node;
import structure.Base_Card;
import structure.Combinatorial_Operator_Node;
import structure.Deck_Card;
import structure.Evaluable;
import structure.Expanded_Leaf_Node;
import structure.Not_Operator_Node;
import structure.Or_Operator_Node;
import structure.Scenario;
import structure.Xor_Operator_Node;

/**
<b>
Purpose: assembles the parts of a configuration file for simulating.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-[4, 5]/2021-8-[7, 13]/2021-8-17/2021-8-19/2021-8-[22, 23]/2021-11-14/2022-12-[30, 31]/2023-1-[1, 3]
</b>
*/

//https://smlweb.cpsc.ucalgary.ca/vital-stats.php?grammar=START+-%3E+DECK+SPECIAL_ABILITIES+PROBABILITY+.%0D%0A%0D%0ADECK+-%3E+DECK_START+SENTINEL_START+DECKLIST+SENTINEL_END.%0D%0ADECK_START+-%3E+decklist%3A+.%0D%0ADECKLIST+-%3E+CARD+MORE_CARDS+.%0D%0AMORE_CARDS+-%3E+CARD+MORE_CARDS+%7C+.%0D%0ACARD+-%3E+CARD_NAME+SEMI_COLON+.%0D%0ACARD_NAME+-%3E+id+.%0D%0A%0D%0ASPECIAL_ABILITIES+-%3E+SPECIAL_ABILITY_START+SENTINEL_START+SPECIAL_ABILITY_LIST+SENTINEL_END+%7C+.%0D%0ASPECIAL_ABILITY_START+-%3E+special_abilities%3A+.%0D%0ASPECIAL_ABILITY_LIST+-%3E+SPECIAL_ABILITY+MORE_SPECIAL_ABILITIES+.%0D%0AMORE_SPECIAL_ABILITIES+-%3E+SPECIAL_ABILITY+MORE_SPECIAL_ABILITIES+%7C+.%0D%0ASPECIAL_ABILITY+-%3E+CARD_NAME+ASSIGN+SENTINEL_START+SPECIAL_ABILITY_BODY+ACTIVATION_LIMITATION+SENTINEL_END+.%0D%0A%0D%0ASPECIAL_ABILITY_BODY+-%3E+SPECIAL_ABILITY_BODY_START+ASSIGN+SENTINEL_START+SPECIAL_ABILITY_EXPR_LIST+SENTINEL_END+SEMI_COLON+.%0D%0ASPECIAL_ABILITY_BODY_START+-%3E+special_ability+.%0D%0ASPECIAL_ABILITY_EXPR_LIST+-%3E+SPECIAL_ABILITY_EXPR+MORE_SPECIAL_ABILITY_EXPRS+.%0D%0AMORE_SPECIAL_ABILITY_EXPRS+-%3E+SPECIAL_ABILITY_EXPR+MORE_SPECIAL_ABILITY_EXPRS+%7C+.%0D%0ASPECIAL_ABILITY_EXPR+-%3E+SPECIAL_ABILITY_ACTION+SEMI_COLON+.%0D%0ASPECIAL_ABILITY_ACTION+-%3E+DRAW+%7C+MILL+%7C+REASONING+%7C+BANISH+.%0D%0A%0D%0ADRAW+-%3E+draw+NON_NEGATIVE_INTEGER+.%0D%0AMILL+-%3E+mill+NON_NEGATIVE_INTEGER+.%0D%0ABANISH+-%3E+banish+NON_NEGATIVE_INTEGER+.%0D%0AREASONING+-%3E+reasoning+SENTINEL_START+DECKLIST+SENTINEL_END+.%0D%0A%0D%0AACTIVATION_LIMITATION+-%3E+ACTIVATION_LIMITATION_START+ASSIGN+NON_NEGATIVE_INTEGER+SEMI_COLON+%7C+.%0D%0AACTIVATION_LIMITATION_START+-%3E+uses+.%0D%0A%0D%0APROBABILITY+-%3E+PROBABILITY_START+SENTINEL_START+SCENARIO_LIST+SENTINEL_END+.%0D%0APROBABILITY_START+-%3E+scenarios%3A+.%0D%0ASCENARIO_LIST+-%3E+SCENARIO+MORE_SCENARIOS+.%0D%0AMORE_SCENARIOS+-%3E+SCENARIO+MORE_SCENARIOS+%7C+.%0D%0ASCENARIO+-%3E+SCENARIO_NAME+ASSIGN+SENTINEL_START+TREE+DISPLAY+SENTINEL_END+.%0D%0ASCENARIO_NAME+-%3E+id+.%0D%0A%0D%0ATREE+-%3E+TREE_START+ASSIGN+SENTINEL_START+EXPR+SENTINEL_END+SEMI_COLON+.%0D%0ATREE_START+-%3E+scenario+.%0D%0A%0D%0AEXPR+-%3E+UNARY_EXPR+BINARY_EXPR+.%0D%0A%0D%0AUNARY_EXPR+-%3E+UNARY_OPERATOR+UNARY_EXPR+%7C+PRIMARY_EXPR+%7C+COMBINATORIC_EXPR+.%0D%0AUNARY_OPERATOR+-%3E+not+.%0D%0A%0D%0ACOMBINATORIC_EXPR+-%3E+COMBINATORIC_OPERATOR+SENTINEL_START+COMBINATORIC_BODY+SENTINEL_END+.%0D%0ACOMBINATORIC_OPERATOR+-%3E+combinatoric+.%0D%0ACOMBINATORIC_BODY+-%3E+NON_NEGATIVE_INTEGER+SEMI_COLON+EXPR+SEMI_COLON+EXPR+SEMI_COLON+EXPR+SEMI_COLON+MORE_CHOICES+.%0D%0AMORE_CHOICES+-%3E+EXPR+SEMI_COLON+MORE_CHOICES+%7C+.%0D%0A%0D%0ANON_NEGATIVE_INTEGER+-%3E+non_negative_integer+.%0D%0A%0D%0APRIMARY_EXPR+-%3E+CONDITION_HAND_CARD_START+CARD_NAME+CONDITION_HAND_CARD_END+%7C%0D%0A++++++++++++++++CONDITION_FIELD_CARD+CARD_NAME+CONDITION_FIELD_CARD+%7C%0D%0A++++++++++++++++CONDITION_GY_CARD+CARD_NAME+CONDITION_GY_CARD+%7C%0D%0A++++++++++++++++CONDITION_BANISH_CARD+CARD_NAME+CONDITION_BANISH_CARD+%7C%0D%0A++++++++++++++++CONDITION_SCENARIO_START+SCENARIO_NAME+CONDITION_SCENARIO_END+%7C%0D%0A++++++++++++++++CONDITION_EXPR_START+EXPR+CONDITION_EXPR_END+.%0D%0A%0D%0ACONDITION_HAND_CARD_START+-%3E+open_bracket+.%0D%0ACONDITION_HAND_CARD_END+-%3E+close_bracket+.%0D%0ACONDITION_SCENARIO_START+-%3E+less_then+.%0D%0ACONDITION_SCENARIO_END+-%3E+greater_then+.%0D%0ACONDITION_EXPR_START+-%3E+open_parenthesis+.%0D%0ACONDITION_EXPR_END+-%3E+close_parenthesis+.%0D%0ACONDITION_FIELD_CARD+-%3E+*+.%0D%0ACONDITION_GY_CARD+-%3E+%23+.%0D%0ACONDITION_BANISH_CARD+-%3E+%7E+.%0D%0A%0D%0ABINARY_EXPR+-%3E+BINARY_OPERATOR+UNARY_EXPR+BINARY_EXPR+%7C+.%0D%0ABINARY_OPERATOR+-%3E+and+%7C+or+%7C+xor+.%0D%0A%0D%0ADISPLAY+-%3E+DISPLAY_START+ASSIGN+DISPLAY_VALUE+SEMI_COLON+%7C+.%0D%0ADISPLAY_START+-%3E+display+.%0D%0ADISPLAY_VALUE+-%3E+true+%7C+false+.%0D%0A%0D%0ASENTINEL_START+-%3E+open_brace+.%0D%0ASENTINEL_END+-%3E+close_brace+.%0D%0AASSIGN+-%3E+assign+.%0D%0ASEMI_COLON+-%3E+%3B+.
//https://smlweb.cpsc.ucalgary.ca/ll1-table.php?grammar=START+-%3E+DECK+SPECIAL_ABILITIES+PROBABILITY+.%0A%0ADECK+-%3E+DECK_START+SENTINEL_START+DECKLIST+SENTINEL_END.%0ADECK_START+-%3E+decklist%3A+.%0ADECKLIST+-%3E+CARD+MORE_CARDS+.%0AMORE_CARDS+-%3E+CARD+MORE_CARDS+%7C+.%0ACARD+-%3E+CARD_NAME+SEMI_COLON+.%0ACARD_NAME+-%3E+id+.%0A%0ASPECIAL_ABILITIES+-%3E+SPECIAL_ABILITY_START+SENTINEL_START+SPECIAL_ABILITY_LIST+SENTINEL_END+%7C+.%0ASPECIAL_ABILITY_START+-%3E+special_abilities%3A+.%0ASPECIAL_ABILITY_LIST+-%3E+SPECIAL_ABILITY+MORE_SPECIAL_ABILITIES+.%0AMORE_SPECIAL_ABILITIES+-%3E+SPECIAL_ABILITY+MORE_SPECIAL_ABILITIES+%7C+.%0ASPECIAL_ABILITY+-%3E+CARD_NAME+ASSIGN+SENTINEL_START+SPECIAL_ABILITY_BODY+ACTIVATION_LIMITATION+SENTINEL_END+.%0A%0ASPECIAL_ABILITY_BODY+-%3E+SPECIAL_ABILITY_BODY_START+ASSIGN+SENTINEL_START+SPECIAL_ABILITY_EXPR_LIST+SENTINEL_END+SEMI_COLON+.%0ASPECIAL_ABILITY_BODY_START+-%3E+special_ability+.%0ASPECIAL_ABILITY_EXPR_LIST+-%3E+SPECIAL_ABILITY_EXPR+MORE_SPECIAL_ABILITY_EXPRS+.%0AMORE_SPECIAL_ABILITY_EXPRS+-%3E+SPECIAL_ABILITY_EXPR+MORE_SPECIAL_ABILITY_EXPRS+%7C+.%0ASPECIAL_ABILITY_EXPR+-%3E+SPECIAL_ABILITY_ACTION+SEMI_COLON+.%0ASPECIAL_ABILITY_ACTION+-%3E+DRAW+%7C+MILL+%7C+REASONING+%7C+BANISH+.%0A%0ADRAW+-%3E+draw+NON_NEGATIVE_INTEGER+.%0AMILL+-%3E+mill+NON_NEGATIVE_INTEGER+.%0ABANISH+-%3E+banish+NON_NEGATIVE_INTEGER+.%0AREASONING+-%3E+reasoning+SENTINEL_START+DECKLIST+SENTINEL_END+.%0A%0AACTIVATION_LIMITATION+-%3E+ACTIVATION_LIMITATION_START+ASSIGN+NON_NEGATIVE_INTEGER+SEMI_COLON+%7C+.%0AACTIVATION_LIMITATION_START+-%3E+uses+.%0A%0APROBABILITY+-%3E+PROBABILITY_START+SENTINEL_START+SCENARIO_LIST+SENTINEL_END+.%0APROBABILITY_START+-%3E+scenarios%3A+.%0ASCENARIO_LIST+-%3E+SCENARIO+MORE_SCENARIOS+.%0AMORE_SCENARIOS+-%3E+SCENARIO+MORE_SCENARIOS+%7C+.%0ASCENARIO+-%3E+SCENARIO_NAME+ASSIGN+SENTINEL_START+TREE+DISPLAY+SENTINEL_END+.%0ASCENARIO_NAME+-%3E+id+.%0A%0ATREE+-%3E+TREE_START+ASSIGN+SENTINEL_START+EXPR+SENTINEL_END+SEMI_COLON+.%0ATREE_START+-%3E+scenario+.%0A%0AEXPR+-%3E+UNARY_EXPR+BINARY_EXPR+.%0A%0AUNARY_EXPR+-%3E+UNARY_OPERATOR+UNARY_EXPR+%7C+PRIMARY_EXPR+%7C+COMBINATORIC_EXPR+.%0AUNARY_OPERATOR+-%3E+not+.%0A%0ACOMBINATORIC_EXPR+-%3E+COMBINATORIC_OPERATOR+SENTINEL_START+COMBINATORIC_BODY+SENTINEL_END+.%0ACOMBINATORIC_OPERATOR+-%3E+combinatoric+.%0ACOMBINATORIC_BODY+-%3E+NON_NEGATIVE_INTEGER+SEMI_COLON+EXPR+SEMI_COLON+EXPR+SEMI_COLON+EXPR+SEMI_COLON+MORE_CHOICES+.%0AMORE_CHOICES+-%3E+EXPR+SEMI_COLON+MORE_CHOICES+%7C+.%0A%0ANON_NEGATIVE_INTEGER+-%3E+non_negative_integer+.%0A%0APRIMARY_EXPR+-%3E+CONDITION_HAND_CARD_START+CARD_NAME+CONDITION_HAND_CARD_END+%7C%0A++++++++++++++++CONDITION_FIELD_CARD+CARD_NAME+CONDITION_FIELD_CARD+%7C%0A++++++++++++++++CONDITION_GY_CARD+CARD_NAME+CONDITION_GY_CARD+%7C%0A++++++++++++++++CONDITION_BANISH_CARD+CARD_NAME+CONDITION_BANISH_CARD+%7C%0A++++++++++++++++CONDITION_SCENARIO_START+SCENARIO_NAME+CONDITION_SCENARIO_END+%7C%0A++++++++++++++++CONDITION_EXPR_START+EXPR+CONDITION_EXPR_END+.%0A%0ACONDITION_HAND_CARD_START+-%3E+open_bracket+.%0ACONDITION_HAND_CARD_END+-%3E+close_bracket+.%0ACONDITION_SCENARIO_START+-%3E+less_then+.%0ACONDITION_SCENARIO_END+-%3E+greater_then+.%0ACONDITION_EXPR_START+-%3E+open_parenthesis+.%0ACONDITION_EXPR_END+-%3E+close_parenthesis+.%0ACONDITION_FIELD_CARD+-%3E+%2A+.%0ACONDITION_GY_CARD+-%3E+%23+.%0ACONDITION_BANISH_CARD+-%3E+%7E+.%0A%0ABINARY_EXPR+-%3E+BINARY_OPERATOR+UNARY_EXPR+BINARY_EXPR+%7C+.%0ABINARY_OPERATOR+-%3E+and+%7C+or+%7C+xor+.%0A%0ADISPLAY+-%3E+DISPLAY_START+ASSIGN+DISPLAY_VALUE+SEMI_COLON+%7C+.%0ADISPLAY_START+-%3E+display+.%0ADISPLAY_VALUE+-%3E+true+%7C+false+.%0A%0ASENTINEL_START+-%3E+open_brace+.%0ASENTINEL_END+-%3E+close_brace+.%0AASSIGN+-%3E+assign+.%0ASEMI_COLON+-%3E+%3B+.&substs=
public class Tree_Assembler
{
    /**
    <b>
    Purpose: Be a piece of data that can store the indeterminate form of data stored in {@link Tree_Assembler#syntactical_stack}.<br>
    Programmer: Gabriel Toban Harris<br>
    Date: 2023-01-02
    </b>
     */
    protected static class Syntactical_Stack_Struct
    {
        //Idea is to store the data properly, such that no type casts are required later on. Thus there is just not much to this class, hence why it is so minimal.

        /**
         * Store integer.
         */
        public Integer integer_part;

        /**
         * Store String.
         */
        public String string_part = "";

        /**
         * Store expression part.
         */
        public Evaluable expression_part;

        /**
         * Store a special ability action.
         */
        public Special_Ability_Base special_ability_action_part;

        /**
         * Constructs with {@link Syntactical_Stack_Struct#integer_part} set.
         * 
         * @param INTEGER_PART {@link Syntactical_Stack_Struct#integer_part}
         */
        public Syntactical_Stack_Struct(final int INTEGER_PART)
        {
            this.integer_part = INTEGER_PART;
        }

        /**
         * Constructs with {@link Syntactical_Stack_Struct#string_part} set.
         * 
         * @param STRING_PART {@link Syntactical_Stack_Struct#string_part}
         */
        public Syntactical_Stack_Struct(final String STRING_PART)
        {
            this.string_part = STRING_PART;
        }

        /**
         * Constructs with {@link Syntactical_Stack_Struct#expression_part} set.
         * 
         * @param EXPRESSION_PART {@link Syntactical_Stack_Struct#expression_part}
         */
        public Syntactical_Stack_Struct(final Evaluable EXPRESSION_PART)
        {
            this.expression_part = EXPRESSION_PART;
        }

        /**
         * Constructs with {@link Syntactical_Stack_Struct#special_ability_action_part} set.
         * 
         * @param SPECIAL_ABILITY_ACTION_PART {@link Syntactical_Stack_Struct#special_ability_action_part}
         */
        public Syntactical_Stack_Struct(final Special_Ability_Base SPECIAL_ABILITY_ACTION_PART)
        {
            this.special_ability_action_part = SPECIAL_ABILITY_ACTION_PART;
        }

        @Override
        public String toString()
        {
            boolean add_comma = false;
            StringBuilder result = new StringBuilder("Syntactical_Stack_Struct [");

            if (integer_part != null)
            {
                add_comma = true;
                result.append("integer_part: ");
                result.append(integer_part);
            }

            if (string_part != null && !string_part.isEmpty())
            {
                if (add_comma)
                    result.append(", ");
                result.append("string_part: " + string_part);
                add_comma = true;
            }

            if (expression_part != null)
            {
                if (add_comma)
                    result.append(", ");
                result.append("expression_part: " + expression_part);
                add_comma = true;
            }

            if (special_ability_action_part != null)
            {
                if (add_comma)
                    result.append(", ");
                result.append("special_ability_action_part: " + special_ability_action_part);
                //add_comma = true;
            }

            result.append(']');

            return result.toString();
        }

        /**
         * Essentially unsets {@link Syntactical_Stack_Struct#string_part} and sets {@link Syntactical_Stack_Struct#expression_part}.
         * 
         * @param EXPRESSION_PART {@link Syntactical_Stack_Struct#expression_part}
         */
        public void replace_string_with_expression(final Evaluable EXPRESSION_PART)
        {
            this.string_part = "";
            this.expression_part = EXPRESSION_PART;
        }
        
        /**
         * Essentially unsets {@link Syntactical_Stack_Struct#integer_part} and sets {@link Syntactical_Stack_Struct#special_ability_action_part}.
         * 
         * @param SPECIAL_ABILITY_ACTION_PART {@link Syntactical_Stack_Struct#special_ability_action_part}
         */
        public void replace_integer_with_special_ability_action(final Special_Ability_Base SPECIAL_ABILITY_ACTION_PART)
        {
            this.integer_part = null;
            this.special_ability_action_part = SPECIAL_ABILITY_ACTION_PART;
        }
        
        /**
         * Essentially unsets {@link Syntactical_Stack_Struct#string_part} and sets {@link Syntactical_Stack_Struct#special_ability_action_part}.
         * 
         * @param SPECIAL_ABILITY_ACTION_PART {@link Syntactical_Stack_Struct#special_ability_action_part}
         */
        public void replace_string_with_special_ability_action(final Special_Ability_Base SPECIAL_ABILITY_ACTION_PART)
        {
            this.string_part = "";
            this.special_ability_action_part = SPECIAL_ABILITY_ACTION_PART;
        }
    }

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
     * file to output syntactical errors to.
     */
    private PrintWriter syntactical_error_output;

    /**
     * file to output derivation
     */
    private PrintWriter syntactical_derivation_output;

    /**
     * Manages special effects for. For the Special Ability section.
     */
    private Special_Ability_Manager card_effects = new Special_Ability_Manager();

    /**
     * stack like structure used by {@link Tree_Assembler#parse(Token)}
     */
    private ArrayList<Semantic_Actions> semantic_stack = new ArrayList<Semantic_Actions>();

    /**
     * Represents stack like structure for building the {@link Scenario} and {@link DECK}, used by {@link Tree_Assembler#parse(Token)}, as well as {@link Special_Ability_Manager}.
     */
    private ArrayList<Syntactical_Stack_Struct> syntactical_stack = new ArrayList<Syntactical_Stack_Struct>();

    /**
     * Main deck which the hand will be generated from.
     */
    private final ArrayList<Deck_Card> DECK;

    /**
     * Stores the generated scenarios.
     */
    private final HashMap<String, Scenario> FOREST;

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
     * Parameterized constructor. Which is implicitly setting {@link Tree_Assembler#VERBOSE} to true.
     * 
     * @param EXPECTED_DECK_SIZE helps with memory management, but can be wrong
     * @param EXPECTED_SCENARIO_COUNT helps with memory management, but can be wrong
     * @param SYNTACTICAL_ERROR_OUTPUT {@link Tree_Assembler#syntactical_error_output}
     * @param SYNTACTICAL_DERIVATION_OUTPUT {@link Tree_Assembler#syntactical_derivation_output}
     */
    public Tree_Assembler(final int EXPECTED_DECK_SIZE, final int EXPECTED_SCENARIO_COUNT, final PrintWriter SYNTACTICAL_ERROR_OUTPUT,
                          final PrintWriter SYNTACTICAL_DERIVATION_OUTPUT)
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
     * Convenience method for handling errors. Also reports them using {@link Tree_Assembler#syntactical_error_output}
     * 
     * @param CURRENT_ACTION section that this is called from
     * @param CURRENT_TOKEN {@link Tree_Assembler#skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
     * @param FOLLOW_SET {@link Tree_Assembler#skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
     * 
     * @return {@link Tree_Assembler#skiperror(parser.Lexeme_Types, parser.Lexeme_Types...)}
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
     * Allow various other functions to choose how to use a resulting production in text form.
     * 
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     * 
     * @return what the production rules will look like, fit to be used as replacement to show the rule resulting in such
     */
    public static String derivation_subsubroutine(final Semantic_Actions... L_H_S_)
    {
        //TODO: update for version 2
        //work on derivation
        StringBuilder result = new StringBuilder(32);
        for (int i = 0; i < L_H_S_.length; ++i)
        {
            switch (L_H_S_[i])
            {
                case DECK_START:
                case DECKLIST:
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
                case SPECIAL_ABILITY_START:
                {
                    result.append("\n\n" + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                    result.append(L_H_S_[i].name());
                    result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + "\n"); //combined at compile time
                    break;
                }
                case SCENARIO:
                case SPECIAL_ABILITY:
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
                case NON_NEGATIVE_INTEGER:
                {
                    result.append(" " + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL); //combined at compile time
                    result.append(L_H_S_[i].name());
                    result.append(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL);
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

        return result.toString();
    }

    /**
     * True output of this object.
     * 
     * @return culmination of this class, destroy this object afterwards (as it has served its function)
     */
    public Simulation create_result()
    {
        if (this.card_effects.special_ability_count() == 0)
            this.card_effects = null; //remove it

        return new Simulation(this.card_effects, this.DECK, this.FOREST.values());
    }

    /**
     * Method for parsing tokens into a boolean like postfix notation tree.
     * Make sure to call {@link Tree_Assembler#check_stacks_are_empty()} once all the {@link Token} are are parsed to check the state of things.
     * 
     * @param INPUT current top token being looked at
     * 
     * @throws EmptySemanticStackException when internal {@link Tree_Assembler#semantic_stack} is empty yet there is another token to parse. Fatal error.
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
                            //START -> DECK SPECIAL_ABILITIES PROBABILITY
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.START.name(), Semantic_Actions.DECK, Semantic_Actions.SPECIAL_ABILITIES,
                                                        Semantic_Actions.PROBABILITY);
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
                            //DECK -> DECK_START SENTINEL_START DECKLIST SENTINEL_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECK_POP, Semantic_Actions.DECK.name(), Semantic_Actions.DECK_START,
                                                            Semantic_Actions.SENTINEL_START, Semantic_Actions.DECKLIST, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECK, INPUT, Lexeme_Types.PROBABILITY_START, Lexeme_Types.SPECIAL_ABILITY_START))
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
                            //DECK_START -> decklist:
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
                case DECKLIST:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //DECKLIST -> CARD MORE_CARDS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.DECKLIST.name(), Semantic_Actions.CARD, Semantic_Actions.MORE_CARDS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DECKLIST, INPUT, Lexeme_Types.SENTINEL_END))
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
                            this.handle_recursive_case_subroutine(Semantic_Actions.MORE_CARDS.name(), Semantic_Actions.CARD, Semantic_Actions.MORE_CARDS);
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
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.CARD.name(), Semantic_Actions.CARD_NAME, Semantic_Actions.SEMI_COLON);
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
                            if (this.convenience_error_handling(Semantic_Actions.CARD_NAME, INPUT, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_HAND_CARD_END,
                                                                Lexeme_Types.ASSIGN, Lexeme_Types.CONDITION_FIELD_CARD, Lexeme_Types.CONDITION_GY_CARD,
                                                                Lexeme_Types.CONDITION_BANISH_CARD))
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
                case SPECIAL_ABILITIES:
                {
                    switch (INPUT.get_type())
                    {
                        case PROBABILITY_START:
                        {
                            //SPECIAL_ABILITIES -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITIES.name());
                            break;
                        }
                        case SPECIAL_ABILITY_START:
                        {
                            //SPECIAL_ABILITIES -> SPECIAL_ABILITY_START SENTINEL_START SPECIAL_ABILITY_LIST SENTINEL_END
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITIES.name(), Semantic_Actions.SPECIAL_ABILITY_START,
                                                        Semantic_Actions.SENTINEL_START, Semantic_Actions.SPECIAL_ABILITY_LIST, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITIES, INPUT, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY_START:
                {
                    switch (INPUT.get_type())
                    {
                        case SPECIAL_ABILITY_START:
                        {
                            //SPECIAL_ABILITY_START -> special_abilities:
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_START, INPUT, Lexeme_Types.SENTINEL_START))
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
                case SPECIAL_ABILITY_LIST:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //SPECIAL_ABILITY_LIST -> SPECIAL_ABILITY MORE_SPECIAL_ABILITIES
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_LIST.name(), Semantic_Actions.SPECIAL_ABILITY,
                                                        Semantic_Actions.MORE_SPECIAL_ABILITIES);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_LIST, INPUT, Lexeme_Types.SENTINEL_END))
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
                case MORE_SPECIAL_ABILITIES:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //MORE_SPECIAL_ABILITIES -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SPECIAL_ABILITIES.name());
                            break;
                        }
                        case ID:
                        {
                            //MORE_SPECIAL_ABILITIES -> SPECIAL_ABILITY MORE_SPECIAL_ABILITIES
                            this.handle_recursive_case_subroutine(Semantic_Actions.MORE_SPECIAL_ABILITIES.name(), Semantic_Actions.SPECIAL_ABILITY,
                                                                  Semantic_Actions.MORE_SPECIAL_ABILITIES);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_SPECIAL_ABILITIES, INPUT, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY:
                {
                    switch (INPUT.get_type())
                    {
                        case ID:
                        {
                            //SPECIAL_ABILITY -> CARD_NAME ASSIGN SENTINEL_START SPECIAL_ABILITY_BODY ACTIVATION_LIMITATION SENTINEL_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_POP, Semantic_Actions.SPECIAL_ABILITY.name(),
                                                            Semantic_Actions.CARD_NAME, Semantic_Actions.ASSIGN, Semantic_Actions.SENTINEL_START,
                                                            Semantic_Actions.SPECIAL_ABILITY_BODY, Semantic_Actions.ACTIVATION_LIMITATION, Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY, INPUT, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY_BODY:
                {
                    switch (INPUT.get_type())
                    {
                        case SPECIAL_ABILITY_BODY_START:
                        {
                            //SPECIAL_ABILITY_BODY -> SPECIAL_ABILITY_BODY_START ASSIGN SENTINEL_START SPECIAL_ABILITY_EXPR_LIST SENTINEL_END SEMI_COLON
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_BODY.name(), Semantic_Actions.SPECIAL_ABILITY_BODY_START,
                                                        Semantic_Actions.ASSIGN, Semantic_Actions.SENTINEL_START, Semantic_Actions.SPECIAL_ABILITY_EXPR_LIST,
                                                        Semantic_Actions.SENTINEL_END, Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_BODY, INPUT, Lexeme_Types.ACTIVATION_LIMITATION_START, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY_BODY_START:
                {
                    switch (INPUT.get_type())
                    {
                        case SPECIAL_ABILITY_BODY_START:
                        {
                            //SPECIAL_ABILITY_BODY_START -> special_ability
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_BODY_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_BODY_START, INPUT, Lexeme_Types.ASSIGN))
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
                case SPECIAL_ABILITY_EXPR_LIST:
                {
                    switch (INPUT.get_type())
                    {
                        case REASONING:
                        case BANISH:
                        case MILL:
                        case DRAW:
                        {
                            //SPECIAL_ABILITY_EXPR_LIST -> SPECIAL_ABILITY_EXPR MORE_SPECIAL_ABILITY_EXPRS
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_EXPR_LIST.name(), Semantic_Actions.SPECIAL_ABILITY_EXPR,
                                                        Semantic_Actions.MORE_SPECIAL_ABILITY_EXPRS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_EXPR_LIST, INPUT, Lexeme_Types.SENTINEL_END))
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
                case MORE_SPECIAL_ABILITY_EXPRS:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //MORE_SPECIAL_ABILITY_EXPRS -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SPECIAL_ABILITY_EXPRS.name());
                            break;
                        }
                        case REASONING:
                        case BANISH:
                        case MILL:
                        case DRAW:
                        {
                            //MORE_SPECIAL_ABILITY_EXPRS -> SPECIAL_ABILITY_EXPR MORE_SPECIAL_ABILITY_EXPRS
                            this.handle_recursive_case_subroutine(Semantic_Actions.MORE_SPECIAL_ABILITY_EXPRS.name(), Semantic_Actions.SPECIAL_ABILITY_EXPR,
                                                                  Semantic_Actions.MORE_SPECIAL_ABILITY_EXPRS);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_SPECIAL_ABILITY_EXPRS, INPUT, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY_EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case REASONING:
                        case BANISH:
                        case MILL:
                        case DRAW:
                        {
                            //SPECIAL_ABILITY_EXPR -> SPECIAL_ABILITY_ACTION SEMI_COLON
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_EXPR.name(), Semantic_Actions.SPECIAL_ABILITY_ACTION,
                                                        Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_EXPR, INPUT, Lexeme_Types.DRAW, Lexeme_Types.MILL, Lexeme_Types.REASONING,
                                                                Lexeme_Types.BANISH, Lexeme_Types.SENTINEL_END))
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
                case SPECIAL_ABILITY_ACTION:
                {
                    switch (INPUT.get_type())
                    {
                        case REASONING:
                        {
                            //SPECIAL_ABILITY_ACTION -> REASONING
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_ACTION.name(), Semantic_Actions.REASONING);
                            break;
                        }
                        case BANISH:
                        {
                            //SPECIAL_ABILITY_ACTION -> BANISH
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_ACTION.name(), Semantic_Actions.BANISH);
                            break;
                        }
                        case MILL:
                        {
                            //SPECIAL_ABILITY_ACTION -> MILL
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_ACTION.name(), Semantic_Actions.MILL);
                            break;
                        }
                        case DRAW:
                        {
                            //SPECIAL_ABILITY_ACTION -> DRAW
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.SPECIAL_ABILITY_ACTION.name(), Semantic_Actions.DRAW);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.SPECIAL_ABILITY_ACTION, INPUT, Lexeme_Types.SEMI_COLON))
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
                case DRAW:
                {
                    switch (INPUT.get_type())
                    {
                        case DRAW:
                        {
                            //DRAW -> draw NON_NEGATIVE_INTEGER
                            this.match_literal_add_handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.DRAW_POP, Semantic_Actions.DRAW.name(),
                                                                              Semantic_Actions.NON_NEGATIVE_INTEGER);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.DRAW, INPUT, Lexeme_Types.SEMI_COLON))
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
                case MILL:
                {
                    switch (INPUT.get_type())
                    {
                        case MILL:
                        {
                            //MILL -> mill NON_NEGATIVE_INTEGER
                            this.match_literal_add_handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.MILL_POP, Semantic_Actions.MILL.name(),
                                                                              Semantic_Actions.NON_NEGATIVE_INTEGER);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MILL, INPUT, Lexeme_Types.SEMI_COLON))
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
                case BANISH:
                {
                    switch (INPUT.get_type())
                    {
                        case BANISH:
                        {
                            //BANISH -> banish NON_NEGATIVE_INTEGER
                            this.match_literal_add_handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.BANISH_POP, Semantic_Actions.BANISH.name(),
                                                                              Semantic_Actions.NON_NEGATIVE_INTEGER);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.BANISH, INPUT, Lexeme_Types.SEMI_COLON))
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
                case REASONING:
                {
                    switch (INPUT.get_type())
                    {
                        case REASONING:
                        {
                            //REASONING -> reasoning SENTINEL_START DECKLIST SENTINEL_END
                            this.match_literal_add_handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.REASONING_POP, Semantic_Actions.REASONING.name(),
                                                                              Semantic_Actions.SENTINEL_START, Semantic_Actions.DECKLIST, Semantic_Actions.SENTINEL_END);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.REASONING, INPUT, Lexeme_Types.SEMI_COLON))
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
                case ACTIVATION_LIMITATION:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //ACTIVATION_LIMITATION -> &epsilon
                            this.epsilon_add_case_subroutine(semantic_stack_end_index, Integer.MAX_VALUE, Semantic_Actions.ACTIVATION_LIMITATION.name()); //default to max value
                            break;
                        }
                        case ACTIVATION_LIMITATION_START:
                        {
                            //ACTIVATION_LIMITATION -> ACTIVATION_LIMITATION_START ASSIGN NON_NEGATIVE_INTEGER SEMI_COLON
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.ACTIVATION_LIMITATION.name(), Semantic_Actions.ACTIVATION_LIMITATION_START,
                                                        Semantic_Actions.ASSIGN, Semantic_Actions.NON_NEGATIVE_INTEGER, Semantic_Actions.SEMI_COLON);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.ACTIVATION_LIMITATION, INPUT, Lexeme_Types.SENTINEL_END))
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
                case ACTIVATION_LIMITATION_START:
                {
                    switch (INPUT.get_type())
                    {
                        case ACTIVATION_LIMITATION_START:
                        {
                            //ACTIVATION_LIMITATION_START -> uses
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.ACTIVATION_LIMITATION_START.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.ACTIVATION_LIMITATION_START, INPUT, Lexeme_Types.ASSIGN))
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
                            this.handle_recursive_case_subroutine(Semantic_Actions.MORE_SCENARIOS.name(), Semantic_Actions.SCENARIO,
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
                        case CONDITION_BANISH_CARD:
                        case CONDITION_GY_CARD:
                        case CONDITION_FIELD_CARD:
                        case NOT:
                        case COMBINATORIC:
                        {
                            //Counting on BINARY_EXPR to either self terminate or perform the semantic popping to result in a 'single' piece.
                            //EXPR -> UNARY_EXPR BINARY_EXPR
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.EXPR.name(), Semantic_Actions.UNARY_EXPR, Semantic_Actions.BINARY_EXPR);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.EXPR, INPUT, Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                        case CONDITION_BANISH_CARD:
                        case CONDITION_GY_CARD:
                        case CONDITION_FIELD_CARD:
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
                        case COMBINATORIC:
                        {
                            //UNARY_EXPR -> COMBINATORIC_EXPR
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.UNARY_EXPR.name(), Semantic_Actions.COMBINATORIC_EXPR);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.UNARY_EXPR, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR, Lexeme_Types.SENTINEL_END,
                                                                Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                                                                Lexeme_Types.CONDITION_FIELD_CARD, Lexeme_Types.CONDITION_GY_CARD, Lexeme_Types.CONDITION_BANISH_CARD,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START, Lexeme_Types.COMBINATORIC))
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
                case COMBINATORIC_EXPR:
                {
                    switch (INPUT.get_type())
                    {
                        case COMBINATORIC:
                        {
                            //COMBINATORIC_EXPR -> COMBINATORIC_OPERATOR SENTINEL_START COMBINATORIC_BODY SENTINEL_END
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.COMBINATORIC_EXPR_POP, Semantic_Actions.COMBINATORIC_EXPR.name(),
                                                            Semantic_Actions.COMBINATORIC_OPERATOR, Semantic_Actions.SENTINEL_START, Semantic_Actions.COMBINATORIC_BODY,
                                                            Semantic_Actions.SENTINEL_END);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.COMBINATORIC_EXPR, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                case COMBINATORIC_OPERATOR:
                {
                    switch (INPUT.get_type())
                    {
                        case COMBINATORIC:
                        {
                            //COMBINATORIC_OPERATOR -> combinatoric
                            this.match_litteral_add_subroutine(semantic_stack_end_index, Semantic_Actions.COMBINATORIC_OPERATOR.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.COMBINATORIC_EXPR, INPUT, Lexeme_Types.SENTINEL_START))
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
                case COMBINATORIC_BODY:
                {
                    switch (INPUT.get_type())
                    {
                        case NON_NEGATIVE_INTEGER:
                        {
                            //COMBINATORIC_BODY -> NON_NEGATIVE_INTEGER SEMI_COLON EXPR SEMI_COLON EXPR SEMI_COLON EXPR SEMI_COLON MORE_CHOICES
                            this.handle_case_subroutine(semantic_stack_end_index, Semantic_Actions.COMBINATORIC_BODY.name(), Semantic_Actions.NON_NEGATIVE_INTEGER,
                                                        Semantic_Actions.SEMI_COLON, Semantic_Actions.EXPR, Semantic_Actions.SEMI_COLON, Semantic_Actions.EXPR,
                                                        Semantic_Actions.SEMI_COLON, Semantic_Actions.EXPR, Semantic_Actions.SEMI_COLON, Semantic_Actions.MORE_CHOICES);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.COMBINATORIC_BODY, INPUT, Lexeme_Types.SENTINEL_END))
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
                case MORE_CHOICES:
                {
                    switch (INPUT.get_type())
                    {
                        case SENTINEL_END:
                        {
                            //MORE_CHOICES -> &epsilon
                            this.epsilon_discard_case_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_CHOICES.name());
                            break;
                        }
                        case CONDITION_BANISH_CARD:
                        case CONDITION_GY_CARD:
                        case CONDITION_FIELD_CARD:
                        case CONDITION_EXPR_START:
                        case CONDITION_SCENARIO_START:
                        case CONDITION_HAND_CARD_START:
                        case COMBINATORIC:
                        case NOT:
                        {
                            //MORE_CHOICES -> EXPR SEMI_COLON MORE_CHOICES
                            this.handle_recursive_case_subroutine(Semantic_Actions.MORE_CHOICES.name(), Semantic_Actions.EXPR, Semantic_Actions.SEMI_COLON,
                                                                  Semantic_Actions.MORE_CHOICES);
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.MORE_CHOICES, INPUT, Lexeme_Types.SENTINEL_END))
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
                case NON_NEGATIVE_INTEGER:
                {
                    switch (INPUT.get_type())
                    {
                        case NON_NEGATIVE_INTEGER:
                        {
                            //NON_NEGATIVE_INTEGER -> non_negative_integer
                            try
                            {
                                this.match_litteral_add_subroutine(semantic_stack_end_index, Integer.parseInt(INPUT.get_lexeme()), Semantic_Actions.NON_NEGATIVE_INTEGER.name());
                            }
                            catch (NumberFormatException ex)
                            {
                                if (this.VERBOSE)
                                    this.syntactical_error_output.println("Unable to parse \"" + INPUT.get_lexeme() + "\" as non-negative integer on line " +
                                                                          INPUT.get_line_number() + ". If it is one, then it is larger than " + Integer.MAX_VALUE +
                                                                          ", which is not allowed. Going to assume value is aforementioned maximum.");

                                this.match_litteral_add_subroutine(semantic_stack_end_index, Integer.MAX_VALUE, Semantic_Actions.NON_NEGATIVE_INTEGER.name()); //assume max integer value is intended
                            }

                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.NON_NEGATIVE_INTEGER, INPUT, Lexeme_Types.SEMI_COLON))
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
                        case CONDITION_BANISH_CARD:
                        {
                            //PRIMARY_EXPR -> CONDITION_BANISH_CARD CARD_NAME CONDITION_BANISH_CARD
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_BANISH_CARD_POP, Semantic_Actions.PRIMARY_EXPR.name(),
                                                            Semantic_Actions.CONDITION_BANISH_CARD, Semantic_Actions.CARD_NAME, Semantic_Actions.CONDITION_BANISH_CARD);
                            break;
                        }
                        case CONDITION_GY_CARD:
                        {
                            //PRIMARY_EXPR -> CONDITION_GY_CARD CARD_NAME CONDITION_GY_CARD
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_GY_CARD_POP, Semantic_Actions.PRIMARY_EXPR.name(),
                                                            Semantic_Actions.CONDITION_GY_CARD, Semantic_Actions.CARD_NAME, Semantic_Actions.CONDITION_GY_CARD);
                            break;
                        }
                        case CONDITION_FIELD_CARD:
                        {
                            //PRIMARY_EXPR -> CONDITION_FIELD_CARD CARD_NAME CONDITION_FIELD_CARD
                            this.handle_pop_case_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_FIELD_CARD_POP, Semantic_Actions.PRIMARY_EXPR.name(),
                                                            Semantic_Actions.CONDITION_FIELD_CARD, Semantic_Actions.CARD_NAME, Semantic_Actions.CONDITION_FIELD_CARD);
                            break;
                        }
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
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START, Lexeme_Types.COMBINATORIC))
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
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                case CONDITION_FIELD_CARD:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_FIELD_CARD:
                        {
                            //CONDITION_FIELD_CARD -> *
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_FIELD_CARD.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_FIELD_CARD, INPUT, Lexeme_Types.ID, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                case CONDITION_GY_CARD:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_GY_CARD:
                        {
                            //CONDITION_GY_CARD -> _
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_GY_CARD.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_GY_CARD, INPUT, Lexeme_Types.ID, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                case CONDITION_BANISH_CARD:
                {
                    switch (INPUT.get_type())
                    {
                        case CONDITION_BANISH_CARD:
                        {
                            //CONDITION_BANISH_CARD -> ~
                            this.match_litteral_discard_subroutine(semantic_stack_end_index, Semantic_Actions.CONDITION_BANISH_CARD.name(), INPUT);
                            no_match = false;
                            break;
                        }
                        default:
                        {
                            if (this.convenience_error_handling(Semantic_Actions.CONDITION_BANISH_CARD, INPUT, Lexeme_Types.ID, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.CONDITION_EXPR_END))
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
                        case SEMI_COLON:
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
                            if (this.convenience_error_handling(Semantic_Actions.BINARY_EXPR, INPUT, Lexeme_Types.SENTINEL_END, Lexeme_Types.SEMI_COLON,
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
                                                                Lexeme_Types.CONDITION_FIELD_CARD, Lexeme_Types.CONDITION_GY_CARD, Lexeme_Types.CONDITION_BANISH_CARD,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START, Lexeme_Types.COMBINATORIC))
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
                            this.epsilon_add_case_subroutine(semantic_stack_end_index, Semantic_Actions.DISPLAY.name(), "true"); //default to true
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
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_START, INPUT, Lexeme_Types.SPECIAL_ABILITY_BODY_START, Lexeme_Types.DRAW,
                                                                Lexeme_Types.MILL, Lexeme_Types.REASONING, Lexeme_Types.BANISH, Lexeme_Types.ID, Lexeme_Types.TREE_START,
                                                                Lexeme_Types.NOT, Lexeme_Types.CONDITION_HAND_CARD_START, Lexeme_Types.CONDITION_FIELD_CARD,
                                                                Lexeme_Types.CONDITION_GY_CARD, Lexeme_Types.CONDITION_BANISH_CARD, Lexeme_Types.CONDITION_SCENARIO_START,
                                                                Lexeme_Types.CONDITION_EXPR_START, Lexeme_Types.COMBINATORIC, Lexeme_Types.NON_NEGATIVE_INTEGER))
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
                            if (this.convenience_error_handling(Semantic_Actions.SENTINEL_END, INPUT, Lexeme_Types.AND, Lexeme_Types.OR, Lexeme_Types.XOR,
                                                                Lexeme_Types.CONDITION_EXPR_END, Lexeme_Types.SEMI_COLON, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END,
                                                                Lexeme_Types.SPECIAL_ABILITY_START, Lexeme_Types.PROBABILITY_START))
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
                            if (this.convenience_error_handling(Semantic_Actions.ASSIGN, INPUT, Lexeme_Types.NON_NEGATIVE_INTEGER, Lexeme_Types.SENTINEL_START, Lexeme_Types.TRUE,
                                                                Lexeme_Types.FALSE))
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
                            if (this.convenience_error_handling(Semantic_Actions.SEMI_COLON, INPUT, Lexeme_Types.NOT, Lexeme_Types.CONDITION_HAND_CARD_START,
                                                                Lexeme_Types.CONDITION_FIELD_CARD, Lexeme_Types.CONDITION_GY_CARD, Lexeme_Types.CONDITION_BANISH_CARD,
                                                                Lexeme_Types.CONDITION_SCENARIO_START, Lexeme_Types.CONDITION_EXPR_START, Lexeme_Types.COMBINATORIC,
                                                                Lexeme_Types.DISPLAY_START, Lexeme_Types.DRAW, Lexeme_Types.MILL, Lexeme_Types.REASONING, Lexeme_Types.BANISH,
                                                                Lexeme_Types.ACTIVATION_LIMITATION_START, Lexeme_Types.ID, Lexeme_Types.SENTINEL_END))
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
                case DECK_POP:
                {
                    //only cards for decklist should be in syntactical_stack at this point
                    for (int i = 0; i < this.syntactical_stack.size(); ++i)
                        this.DECK.add(new Deck_Card(this.syntactical_stack.get(i).string_part)); //add card to deck

                    this.syntactical_stack.clear();
                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case SCENARIO_POP:
                {
                    //create scenario
                    //expected form: [scenario name, tree, display]
                    final String SCEANRIO_NAME = this.syntactical_stack.remove(0).string_part;

                    if (this.syntactical_stack.size() == 2)
                    {
                        if (!this.FOREST.containsKey(SCEANRIO_NAME))
                            this.FOREST.put(SCEANRIO_NAME, new Scenario(!Tokenizer.FALSE.matcher(this.syntactical_stack.remove(1).string_part).matches(), SCEANRIO_NAME, this.syntactical_stack.remove(0).expression_part));
                        else
                        {
                            //panic
                            if (this.VERBOSE)
                                this.syntactical_error_output.println("Error, repeated scenario name \"" + SCEANRIO_NAME + "\", discarding this scenario. Detected not before line number " + INPUT.get_line_number());

                            this.syntactical_stack.clear(); //clear remaining stack stuff
                        }
                    }
                    else
                    {
                        //panic
                        if (this.VERBOSE)
                            this.syntactical_error_output.println("Current scenario \"" + SCEANRIO_NAME + "\" found around line " + INPUT.get_line_number() + " is malformed. Discarding this scenario.");

                        this.syntactical_stack.clear();
                    }

                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case UNARY_POP:
                {
                    //handle unary operator
                    final int RESULT_LOCATION = this.syntactical_stack.size() - 2;
                    final String UNARY_OPERATOR = this.syntactical_stack.remove(RESULT_LOCATION).string_part;

                    if (Tokenizer.NOT.matcher(UNARY_OPERATOR).matches())
                    {
                        this.syntactical_stack.get(RESULT_LOCATION).expression_part = new Not_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION).expression_part);
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Error: operator, \"" + UNARY_OPERATOR + "\" did not match any expected unary operator. Could alternatively, be caused by a malformed expression that followed a proper unary operator. Detected around line " + INPUT.get_line_number() + ". Discarding whole scenario.");

                    break;
                }
                case CONDITION_HAND_CARD_POP:
                {
                    //card condition that is in hand
                    this.handle_card_condition_subroutine(semantic_stack_end_index, Game_State.Locations.HAND);
                    break;
                }
                case CONDITION_SCENARIO_POP:
                {
                    //scenario condition in scenario
                    final int SYNTACTICAL_STACK_LAST_INDEX = this.syntactical_stack.size() - 1;
                    final String REFERENCED_SCEANRIO_NAME = this.syntactical_stack.get(SYNTACTICAL_STACK_LAST_INDEX).string_part;
                    final Scenario POTENTIONAL_SCENARIO = this.FOREST.get(REFERENCED_SCEANRIO_NAME);
                    
                    if (POTENTIONAL_SCENARIO != null)
                    {
                        this.syntactical_stack.get(SYNTACTICAL_STACK_LAST_INDEX).replace_string_with_expression(POTENTIONAL_SCENARIO);
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Error: undefined referenced, " + REFERENCED_SCEANRIO_NAME + ", at line " + INPUT.get_line_number() + ". Discarding whole scenario.");
                    break;
                }
                case BINARY_POP_3:
                {
                    //create binary expression out of 3 parts from syntactical_stack
                    assert (this.syntactical_stack.size() >= 4); //Well expected to be 4 under normal circumstances, {scenario name, left operand, binary operator, right operand}. Could be higher when called in other places, like when constructing the combinatorial operator.
                    final int SYNTACTICAL_TARGET_INDEX = this.syntactical_stack.size() - 2; //First time will be the operator, second time will be right operand due to the operator's removal.
                    final int RESULT_LOCATION = SYNTACTICAL_TARGET_INDEX - 1;
                    final String BINARY_OPERATOR = this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX).string_part;

                    if (Tokenizer.AND.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.get(RESULT_LOCATION).expression_part = new And_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION).expression_part,
                                                                                                            this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX).expression_part);
                    else if (Tokenizer.OR.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.get(RESULT_LOCATION).expression_part = new Or_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION).expression_part,
                                                                                                           this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX).expression_part);
                    else if (Tokenizer.XOR.matcher(BINARY_OPERATOR).matches())
                        this.syntactical_stack.get(RESULT_LOCATION).expression_part = new Xor_Operator_Node(this.syntactical_stack.get(RESULT_LOCATION).expression_part,
                                                                                                            this.syntactical_stack.remove(SYNTACTICAL_TARGET_INDEX).expression_part);
                    else
                    {
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Error: \"" + BINARY_OPERATOR + "\" did not match any expected value. Should be a binary operator. Possibly caused by a missing operand. Error found at line " + INPUT.get_line_number() + ". Discarding whole scenario.");
                        break;
                    }

                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case COMBINATORIC_EXPR_POP:
                {
                    //create combinatorial node
                    //Expected form [non_negative_number, expression, expression, expression, ...], with a marker being before non_negative_number.
                    int marker_location = this.syntactical_stack.size() - 2; //Start from minimum location, while -5 could be down with happy paths, this ensures that in problematic cases, the marker is not over shot.
                    final int CHOOSE_N; //for COMBINATORIAL_NODE
                    final Combinatorial_Operator_Node COMBINATORIAL_NODE;
                    final Evaluable[] OPTIONS;

                    //find for marker location
                    while (!Tokenizer.COMBINATORIC.matcher(this.syntactical_stack.get(marker_location).string_part).matches()) //marker must exist, will not go out of bounds
                        --marker_location;

                    if (this.syntactical_stack.size() - marker_location < 5)
                    {
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Failed to make Combinatorial Node around line number " + INPUT.get_line_number() + ". Not enough arguments, so either something unknown is very wrong or too many of the given expressions are malformed, at least 3 properly formed ones are required. Discarding whole scenario.");
                        break;
                    }

                    //choice amount
                    CHOOSE_N = this.syntactical_stack.get(marker_location + 1).integer_part;

                    if (CHOOSE_N < 2)
                    {
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Failed to make Combinatorial Node around line number " + INPUT.get_line_number() + ", due to the choice of \"" + CHOOSE_N + "\" being less than 2, must be at least 2. Discarding whole scenario.");
                        break;
                    }

                    //what is being picked from
                    {
                        final List<Syntactical_Stack_Struct> EVALUABLE_STRUCTS = this.syntactical_stack.subList(marker_location + 2, this.syntactical_stack.size()); //+2 from marker is the start of the options.
                        OPTIONS = new Evaluable[EVALUABLE_STRUCTS.size()];

                        for (int i = 0; i < OPTIONS.length; ++i)
                            OPTIONS[i] = EVALUABLE_STRUCTS.get(i).expression_part;
                    }

                    try
                    {
                        COMBINATORIAL_NODE = new Combinatorial_Operator_Node(CHOOSE_N, OPTIONS);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        this.handle_scenario_critical_error(semantic_stack_end_index, "Failed to make Combinatorial Node around line number " + INPUT.get_line_number() + ". Check following for more in formation:\n" + ex.getMessage() + "\nDiscarding whole scenario.");
                        break;
                    }

                    this.syntactical_stack.subList(marker_location + 1, this.syntactical_stack.size()).clear(); //remove used pieces
                    this.syntactical_stack.get(marker_location).replace_string_with_expression(COMBINATORIAL_NODE); //set node where marker is, thus being space efficient
                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case CONDITION_BANISH_CARD_POP:
                {
                    //card condition that is banished
                    this.handle_card_condition_subroutine(semantic_stack_end_index, Game_State.Locations.BANISH);
                    break;
                }
                case CONDITION_GY_CARD_POP:
                {
                    //card condition for in graveyard
                    this.handle_card_condition_subroutine(semantic_stack_end_index, Game_State.Locations.GRAVEYARD);
                    break;
                }
                case CONDITION_FIELD_CARD_POP:
                {
                    //card condition for on the field
                    this.handle_card_condition_subroutine(semantic_stack_end_index, Game_State.Locations.FIELD);
                    break;
                }
                case SPECIAL_ABILITY_POP:
                {
                    //create special ability card
                    //expected form [card name, special ability body (variable length), activation limitation]
                    final int SYNTACTICAL_STACK_END_INDEX = this.syntactical_stack.size() - 1;
                    int card_name_location = SYNTACTICAL_STACK_END_INDEX;
                    
                    //ensure that syntactical_stack is in a good state
                    if (this.syntactical_stack.get(card_name_location).integer_part != null && this.syntactical_stack.get(--card_name_location).special_ability_action_part != null)
                    {
                        //assume that first one not empty one is card name
                        do
                        {
                            --card_name_location;
                        } while (this.syntactical_stack.get(card_name_location).string_part.isEmpty());

                        assert (card_name_location == 0); //nothing should be before it

                        {
                            int special_ability_start_index = card_name_location + 1;
                            final Special_Ability_Base[] SPECIAL_ABILITY_ACTIONS = new Special_Ability_Base[SYNTACTICAL_STACK_END_INDEX - special_ability_start_index];

                            for (int i = 0; i < SPECIAL_ABILITY_ACTIONS.length; ++i, ++special_ability_start_index)
                                SPECIAL_ABILITY_ACTIONS[i] = this.syntactical_stack.get(special_ability_start_index).special_ability_action_part;

                            if (!this.card_effects.add(this.syntactical_stack.get(SYNTACTICAL_STACK_END_INDEX).integer_part, this.syntactical_stack.get(card_name_location).string_part, SPECIAL_ABILITY_ACTIONS) && this.VERBOSE)
                                this.syntactical_error_output.println("Error: Special Ability named \"" + this.syntactical_stack.get(card_name_location).string_part +
                                                                      "\", around line " + INPUT.get_line_number() + " is already defined. Discarding it.");
                        }
                    }
                    //panic
                    else if (this.VERBOSE)
                        this.syntactical_error_output.println("Error: Special Ability named \"" + this.syntactical_stack.get(0).string_part + "\", around line " +
                                                              INPUT.get_line_number() + " is malformed. Discarding it."); //Assume that this.syntactical_stack.get(0).string_part is special ability name, as nothing should be before it.

                    this.syntactical_stack.clear();
                    this.semantic_stack.remove(semantic_stack_end_index);
                    break;
                }
                case DRAW_POP:
                {
                    //handle DRAW Special Ability
                    final int RESULT_LOCATION = this.syntactical_stack.size() - 2;
                    final String DRAW_MARKER = this.syntactical_stack.remove(RESULT_LOCATION).string_part;

                    if (Tokenizer.DRAW.matcher(DRAW_MARKER).matches())
                    {
                        this.syntactical_stack.get(RESULT_LOCATION).replace_integer_with_special_ability_action(new Special_Ability_Draw(this.syntactical_stack.get(RESULT_LOCATION).integer_part));
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_special_ability_critical_error(semantic_stack_end_index, "Error: \"" + DRAW_MARKER + "\" did not match any expected pattern of " + Tokenizer.DRAW.toString() + ". Could alternatively, be caused by the lack of an integer. Detected around line " + INPUT.get_line_number() + ".");

                    break;
                }
                case MILL_POP:
                {
                    //handle MILL Special Ability
                    final int RESULT_LOCATION = this.syntactical_stack.size() - 2;
                    final String MILL_MARKER = this.syntactical_stack.remove(RESULT_LOCATION).string_part;

                    if (Tokenizer.MILL.matcher(MILL_MARKER).matches())
                    {
                        this.syntactical_stack.get(RESULT_LOCATION).replace_integer_with_special_ability_action(new Special_Ability_Mill(this.syntactical_stack.get(RESULT_LOCATION).integer_part));
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_special_ability_critical_error(semantic_stack_end_index, "Error: \"" + MILL_MARKER + "\" did not match any expected pattern of " + Tokenizer.MILL.toString() + ". Could alternatively, be caused by the lack of an integer. Detected around line " + INPUT.get_line_number() + ".");

                    break;
                }
                case BANISH_POP:
                {
                    //handle BANISH Special Ability
                    final int RESULT_LOCATION = this.syntactical_stack.size() - 2;
                    final String BANISH_MARKER = this.syntactical_stack.remove(RESULT_LOCATION).string_part;

                    if (Tokenizer.BANISH.matcher(BANISH_MARKER).matches())
                    {
                        this.syntactical_stack.get(RESULT_LOCATION).replace_integer_with_special_ability_action(new Special_Ability_Banish(this.syntactical_stack.get(RESULT_LOCATION).integer_part));
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    else
                        this.handle_special_ability_critical_error(semantic_stack_end_index, "Error: \"" + BANISH_MARKER + "\" did not match any expected pattern of " + Tokenizer.BANISH.toString() + ". Could alternatively, be caused by the lack of an integer. Detected around line " + INPUT.get_line_number() + ".");

                    break;
                }
                case REASONING_POP:
                {
                    //handle REASONING Special Ability
                    int reasoning_marker;
                    final int SYNTACTICAL_STACK_END_INDEX = this.syntactical_stack.size() - 1;
                    reasoning_marker = SYNTACTICAL_STACK_END_INDEX;//going to start at the end in case no cards were supplied
                    
                    //find marker
                    while (!Tokenizer.REASONING.matcher(this.syntactical_stack.get(reasoning_marker).string_part).matches())
                        --reasoning_marker;

                    if (reasoning_marker == SYNTACTICAL_STACK_END_INDEX)
                        this.handle_special_ability_critical_error(semantic_stack_end_index, "Error: creating Reasoning Special Ability, no proper list of deck card stopping points received. Detected around line " + INPUT.get_line_number() + "."); //panic
                    else
                    {
                        List<Syntactical_Stack_Struct> decklist = this.syntactical_stack.subList(reasoning_marker + 1, this.syntactical_stack.size());
                        ArrayList<Base_Card> stopping_points = new ArrayList<Base_Card>();
                        
                        for (Syntactical_Stack_Struct card : decklist)
                            stopping_points.add(new Base_Card(card.string_part));
                        
                        decklist.clear(); //remove cards
                        this.syntactical_stack.get(reasoning_marker).replace_string_with_special_ability_action(new Special_Ability_Reasoning(stopping_points));
                        this.semantic_stack.remove(semantic_stack_end_index);
                    }
                    
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
     * Outputs decklist that was read.
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it
     */
    public void print_out_decklist(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        final File OUTPUT_FILE = Starting_Point.add_file_extension(true, Starting_Point.DECKLIST_EXTENSION, PARTIAL_OUTPUT_DIRECTORY);
        try
        {
            OUTPUT_FILE.createNewFile(); //Try creating file first before bothering to assemble its contents.

            final StringBuilder ASSEMBLE_DECKLIST = new StringBuilder("decklist:\n{\n");
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
     * Outputs what the in play card effects are.
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it
     */
    public void print_out_card_effects(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        if (this.card_effects == null)
            return;

        final File OUTPUT_FILE = Starting_Point.add_file_extension(true, Starting_Point.CARD_EFFECTS_FILE_EXTENSION, PARTIAL_OUTPUT_DIRECTORY);
        try
        {
            OUTPUT_FILE.createNewFile(); //Try creating file first before bothering to assemble its contents.

            try (final PrintWriter DECKLIST_OUTPUT = new PrintWriter(OUTPUT_FILE))
            {
                DECKLIST_OUTPUT.print(this.card_effects);
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage() + "\nError caused with following path: " + OUTPUT_FILE.getAbsolutePath() + ", thus could not output its card effects.");
        }
    }

    /**
     * Outputs created scenarios, each to their own file in dot format.
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
     * Convenience method which effectively calls {@link Tree_Assembler#print_out_decklist} and then {@link Tree_Assembler#print_out_scenarios}
     * 
     * @param PARTIAL_OUTPUT_DIRECTORY that is basis for new file. Expected to already have extension extracted, as rest is concatenated on to it.ocation to output files to, expected to be ready to have extensions
     */
    public void print_out_results(final String PARTIAL_OUTPUT_DIRECTORY)
    {
        this.print_out_decklist(PARTIAL_OUTPUT_DIRECTORY);
        this.print_out_card_effects(PARTIAL_OUTPUT_DIRECTORY);
        this.print_out_scenarios(PARTIAL_OUTPUT_DIRECTORY);
    }

    /**
     * Centralize shared constructor code. Should only be called once by the constructor and never again.
     */
    private void finish_construction()
    {
        this.semantic_stack.add(Semantic_Actions.START); //starting symbol
    }

    /**
     * A simple subroutine to handle {@link Semantic_Actions} related to card condition creation.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link #semantic_stack}
     * @param PLACE is where the card to look for is located
     */
    private void handle_card_condition_subroutine(final int SEMANTIC_STACK_END_INDEX, final Game_State.Locations PLACE)
    {
        //Even if the wrong thing is popped due to a syntax error, such will eventually be dealt with by another special Semantic_Action.
        final int SYNTACTICAL_STACK_LAST_INDEX = this.syntactical_stack.size() - 1;
        final String CARD_NAME = this.syntactical_stack.get(SYNTACTICAL_STACK_LAST_INDEX).string_part;
        this.syntactical_stack.get(SYNTACTICAL_STACK_LAST_INDEX).replace_string_with_expression(new Expanded_Leaf_Node<Base_Card>(PLACE, CARD_NAME, new Base_Card(CARD_NAME)));
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
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
     * Handles critical syntax errors relating to Scenarios. Removes {@link Tree_Assembler#semantic_stack} values until {@link Semantic_Actions.MORE_SPECIAL_ABILITIES} is reached (not inclusive). Also {@link Tree_Assembler#syntactical_stack} is cleared.
     * Idea is essentially that the current Scenario is practically unsalvageable, thus discard it.
     * 
     * @param semantic_stack_end_index the last {@link Tree_Assembler#semantic_stack}
     * @param ERROR_MESSAGE to output
     */
    private void handle_scenario_critical_error(int semantic_stack_end_index, final String ERROR_MESSAGE)
    {
        this.handle_critical_error_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SCENARIOS, ERROR_MESSAGE, Semantic_Actions.SENTINEL_END, Semantic_Actions.SEMI_COLON,
                                              Semantic_Actions.SENTINEL_END);
    }

    /**
     * Handles critical syntax errors relating to Special Abilities. Removes {@link Tree_Assembler#semantic_stack} values until {@link Semantic_Actions.MORE_SCENARIOS} is reached (not inclusive). Also {@link Tree_Assembler#syntactical_stack} is cleared.
     * Idea is essentially that the current Special Ability is practically unsalvageable, thus discard it.
     * 
     * @param semantic_stack_end_index the last {@link Tree_Assembler#semantic_stack}
     * @param ERROR_MESSAGE to output
     */
    private void handle_special_ability_critical_error(int semantic_stack_end_index, final String ERROR_MESSAGE)
    {
        this.handle_critical_error_subroutine(semantic_stack_end_index, Semantic_Actions.MORE_SPECIAL_ABILITIES, ERROR_MESSAGE, Semantic_Actions.SEMI_COLON,
                                              Semantic_Actions.SENTINEL_END);
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
            Function_Bank.stringbuilder_replace_string_with_string(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + TARGET + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL,
                                                                   Tree_Assembler.derivation_subsubroutine(L_H_S_), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString() + "\n");//output derivation
        }
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. Essentially the same as {@link Tree_Assembler#handle_case_subroutine(int, String, Semantic_Actions...)}, except that 
     * {@code this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, L_H_S_[L_H_S_.length - 1]);} is omitted as that would do nothing.
     * 
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void handle_recursive_case_subroutine(final String TARGET, final Semantic_Actions... L_H_S_)
    {
        this.derivation_subroutine(TARGET, L_H_S_);

        //work on semantic stack
        for (int i = L_H_S_.length - 2; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. For the case that a production rule results in an epsilon to be discarded. Yet should still have something added to the {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link Tree_Assembler#semantic_stack}
     * @param ADD is what is being added to the {@link Tree_Assembler#syntactical_stack}
     * @param TARGET current symbol being replaced
     */
    private void epsilon_add_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final int ADD, final String TARGET)
    {
        //stack maintenance
        this.syntactical_stack.add(new Syntactical_Stack_Struct(ADD));
        this.epsilon_discard_case_subroutine(SEMANTIC_STACK_END_INDEX, TARGET);
    }

    /**
     * Subroutine for matching terminals and then adds them to {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param CURRENT_LEXEME of the {@link Token} to be created
     * @param TARGET {@link Lexeme_Types} to replace
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final int CURRENT_LEXEME, final String TARGET)
    {
        //stack maintenance
        this.syntactical_stack.add(new Syntactical_Stack_Struct(CURRENT_LEXEME));
        this.match_literal_discard_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, String.valueOf(CURRENT_LEXEME));
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse. For the case that a production rule results in an epsilon to be discarded. Yet should still have something added to the {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET current symbol being replaced
     * @param ADD is what is being added to the {@link Tree_Assembler#syntactical_stack}
     */
    private void epsilon_add_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final String ADD)
    {
        //stack maintenance
        this.syntactical_stack.add(new Syntactical_Stack_Struct(ADD));
        this.epsilon_discard_case_subroutine(SEMANTIC_STACK_END_INDEX, TARGET);
    }

    /**
     * Subroutine for matching terminals and then discarding them.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_LEXEME of the {@link Token} to be discarded
     */
    private void match_literal_discard_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final String CURRENT_LEXEME)
    {
        //stack maintenance
        this.semantic_stack.remove(SEMANTIC_STACK_END_INDEX);
        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + TARGET + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL, CURRENT_LEXEME,
                                                                   this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString() + "\n");//output derivation
        }
    }

    /**
     * Subroutine for matching terminals and then adds them to {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_LEXEME of the {@link Token} to be created
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final String CURRENT_LEXEME)
    {
        //stack maintenance
        this.syntactical_stack.add(new Syntactical_Stack_Struct(CURRENT_LEXEME));
        this.match_literal_discard_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, CURRENT_LEXEME);
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
     * Subroutine for matching terminals and then adds them to {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param SEMANTIC_STACK_END_INDEX last index of {@link Tree_Assembler#semantic_stack}
     * @param TARGET {@link Lexeme_Types} to replace
     * @param CURRENT_NODE values pertaining to node to be created
     */
    private void match_litteral_add_subroutine(final int SEMANTIC_STACK_END_INDEX, final String TARGET, final Token CURRENT_NODE)
    {
        this.match_litteral_add_subroutine(SEMANTIC_STACK_END_INDEX, TARGET, CURRENT_NODE.get_lexeme());
    }

    /**
     * Simple subroutine meant to reduce overall code size by reuse.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link Tree_Assembler#semantic_stack}
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
     * Similar to {@link Tree_Assembler#handle_case_subroutine(int, String, Semantic_Actions...)}, except that it adds a {@link Semantic_Actions} to {@link Tree_Assembler#semantic_stack} without affecting {@link Tree_Assembler#derivation}.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link Tree_Assember#semantic_stack}
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

    /**
     * Essentially combines both {@link Tree_Assembler#match_litteral_add_subroutine(int, String, String)} with {@link Tree_Assembler#handle_pop_case_subroutine(int, Semantic_Actions, String, Semantic_Actions...)}.
     * 
     * @param SEMANTIC_STACK_END_INDEX local variable representing the last index of {@link Tree_Assember#semantic_stack}
     * @param POP {@link Semantic_Actions} relating to stack popping
     * @param TARGET current symbol being replaced
     * @param L_H_S_ expected to be at least length 1, represents the result of the production rule in terms of grammar
     */
    private void match_literal_add_handle_pop_case_subroutine(final int SEMANTIC_STACK_END_INDEX, final Semantic_Actions POP, final String TARGET, final Semantic_Actions... L_H_S_)
    {
        this.syntactical_stack.add(new Syntactical_Stack_Struct(TARGET));

        this.semantic_stack.set(SEMANTIC_STACK_END_INDEX, POP);

        for (int i = L_H_S_.length - 1; i > -1; --i)
            this.semantic_stack.add(L_H_S_[i]);

        if (this.VERBOSE)
        {
            Function_Bank.stringbuilder_replace_string_with_string(Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL + TARGET + Tree_Assembler.SPECIAL_UNLIKELY_SENTINEL,
                                                                   TARGET + Tree_Assembler.derivation_subsubroutine(L_H_S_), this.derivation);
            this.syntactical_derivation_output.println(this.derivation.toString() + "\n");//output derivation
        }
    }

    /**
     * Deal with situations that cannot be recovered from. Removes {@link Semantic_Actions} from {@link Tree_Assembler#semantic_stack} until STOPPING_POINT is reached (STOPPING_POINT is not removed).
     * Then adds SUFFIX the what remains. Additionally, clears {@link Tree_Assembler#syntactical_stack}.
     * 
     * @param semantic_stack_end_index the last {@link Tree_Assembler#semantic_stack}
     * @param ERROR_MESSAGE to output
     * @param STOPPING_POINT is when to stop removing stuff from {@link Tree_Assembler#semantic_stack}
     * @param SUFFIX is stuff to add to {@link Tree_Assembler#semantic_stack} once clean up is done
     */
    private void handle_critical_error_subroutine(int semantic_stack_end_index, final Semantic_Actions STOPPING_POINT, final String ERROR_MESSAGE, final Semantic_Actions... SUFFIX)
    {
        //panic
        if (this.VERBOSE)
            this.syntactical_error_output.println(ERROR_MESSAGE);

        //remove current secnario's semantic actions
        do
        {
            this.semantic_stack.remove(semantic_stack_end_index);
            --semantic_stack_end_index;
        } while (this.semantic_stack.get(semantic_stack_end_index) != STOPPING_POINT && semantic_stack_end_index > -1);

        //add finishing scenario stuff to not have issues
        if (SUFFIX != null)
            for (Semantic_Actions ACTION : SUFFIX)
                this.semantic_stack.add(ACTION);

        //remove traces of incomplete production rules
        this.syntactical_stack.clear(); //Deck, Special Abilities, and Scenarios are all popped when done; thus is fine to clear.
    }
}
