package testing.evaluation;

import java.util.ArrayList;
import structure.Base_Card;
import structure.Deck_Card;
import structure.Leaf_Node;
import structure.Scenario;
import structure.Xor_Operator_Node;

/**
<b>
Purpose: Test that {@link Xor_Operator_Node} works properly<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04/2021-8-19
</b>
*/

public class XOR_test
{
    public static void main(String[] args)
    {
        ArrayList<Deck_Card> hand_A_A = new ArrayList<Deck_Card>();
        hand_A_A.add(new Deck_Card("A"));
        hand_A_A.add(new Deck_Card("A"));

        ArrayList<Deck_Card> hand_A_B = new ArrayList<Deck_Card>();
        hand_A_A.add(new Deck_Card("A"));
        hand_A_A.add(new Deck_Card("B"));

        ArrayList<Deck_Card> hand_B_A = new ArrayList<Deck_Card>();
        hand_A_A.add(new Deck_Card("B"));
        hand_A_A.add(new Deck_Card("A"));

        ArrayList<Deck_Card> hand_B_B = new ArrayList<Deck_Card>();
        hand_A_A.add(new Deck_Card("B"));
        hand_A_A.add(new Deck_Card("B"));

        Scenario xor_test = new Scenario("xor test", new Xor_Operator_Node(new Leaf_Node<Base_Card>("A", new Base_Card("A")), new Leaf_Node<Base_Card>("A", new Base_Card("A"))));

        System.out.println("xor test (A, A) has " + (xor_test.evaluate(hand_A_A) ? "failed." : "succeed."));
        System.out.println("xor test (A, B) has " + (xor_test.evaluate(hand_A_B) ? "succeed." : "failed."));
        System.out.println("xor test (B, A) has " + (xor_test.evaluate(hand_B_A) ? "succeed." : "failed."));
        System.out.println("xor test (B, B) has " + (xor_test.evaluate(hand_B_B) ? "failed." : "succeed."));
    }
}
