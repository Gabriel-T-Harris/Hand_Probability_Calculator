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
Date: 2021-08-04
</b>
*/

public class XOR_test
{
    public static void main(String[] args)
    {
        ArrayList<Deck_Card> hand = new ArrayList<Deck_Card>();
        hand.add(new Deck_Card("A"));
        hand.add(new Deck_Card("A"));

        Scenario<Base_Card> xor_test = new Scenario<Base_Card>("xor test", new Xor_Operator_Node<Base_Card>(new Leaf_Node<Base_Card>("A", new Base_Card("A")),
                                                                                                            new Leaf_Node<Base_Card>("A", new Base_Card("A"))));

        System.out.println("xor test has " + (xor_test.evaluate(hand) ? "failed." : "succeed."));
    }
}
