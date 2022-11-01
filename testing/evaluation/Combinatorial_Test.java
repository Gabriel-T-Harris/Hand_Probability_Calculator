package testing.evaluation;

import java.util.ArrayList;
import simulation.special_ability.Game_State;
import structure.And_Operator_Node;
import structure.Base_Card;
import structure.Combinatorial_Operator_Node;
import structure.Deck_Card;
import structure.Evaluable;
import structure.Leaf_Node;

/**
<b>
Purpose: to test {@link Combinatorial_Operator_Node}}<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-10-31
</b>
*/

public class Combinatorial_Test
{
    public static void main(String[] args)
    {
        final ArrayList<Deck_Card> A_B_HAND = new ArrayList<Deck_Card>();
        A_B_HAND.add(new Deck_Card("A"));
        A_B_HAND.add(new Deck_Card("B"));

        final ArrayList<Deck_Card> C_B_HAND = new ArrayList<Deck_Card>();
        C_B_HAND.add(new Deck_Card("C"));
        C_B_HAND.add(new Deck_Card("B"));

        final ArrayList<Deck_Card> C_B_D_HAND = new ArrayList<Deck_Card>();
        C_B_D_HAND.add(new Deck_Card("C"));
        C_B_D_HAND.add(new Deck_Card("B"));
        C_B_D_HAND.add(new Deck_Card("D"));

        final Leaf_Node<Base_Card>[] BASIC_TEST_DATA = new Leaf_Node[]{new Leaf_Node<Base_Card>("A", new Base_Card("A")), new Leaf_Node<Base_Card>("B", new Base_Card("B")),
                                                                       new Leaf_Node<Base_Card>("C", new Base_Card("C"))};

        final Evaluable[] MID_LEVEL_TEST_DATA = new Evaluable[]{
                                                                new And_Operator_Node(new Leaf_Node<Base_Card>("C", new Base_Card("C")),
                                                                                      new Leaf_Node<Base_Card>("D", new Base_Card("D"))),
                                                                new Leaf_Node<Base_Card>("A", new Base_Card("A")), new Leaf_Node<Base_Card>("B", new Base_Card("B"))};

        final Combinatorial_Operator_Node COMBINATORIAL_BASIC_TEST = new Combinatorial_Operator_Node(2, "BASIC_TEST", BASIC_TEST_DATA),
                COMBINATORIAL_MID_LEVEL_TEST = new Combinatorial_Operator_Node(2, "MID_LEVEL_TEST", MID_LEVEL_TEST_DATA);

        System.out.println("Simple tests, Test A_B: " + COMBINATORIAL_BASIC_TEST.evaluate(A_B_HAND) + ", Test C_B: " + COMBINATORIAL_BASIC_TEST.evaluate(C_B_HAND));

        Game_State.reset_hand(A_B_HAND);

        System.out.println("Mid level tests, Test A_B: " + COMBINATORIAL_MID_LEVEL_TEST.evaluate(A_B_HAND) + ", Test C_B: " + COMBINATORIAL_MID_LEVEL_TEST.evaluate(C_B_D_HAND));
    }
}
