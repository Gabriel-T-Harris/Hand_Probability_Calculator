package testing.evaluation;

import java.util.Arrays;
import java.util.List;

import structure.Base_Card;
import structure.Deck_Card;
import structure.Leaf_Node;
import structure.Scenario;
import structure.Xor_Operator_Node;

/**
<b>
Purpose: Test that {@link Xor_Operator_Node} works properly<br>
Programmer: Gabriel Toban Harris, Alexander Herman Oxorn<br>
</b>
*/

public class XOR_test
{
    public static void main(String[] args)
    {
        List<Deck_Card> hand_A_A = Arrays.asList(new Deck_Card("A"), new Deck_Card("A"));
        List<Deck_Card> hand_A_B = Arrays.asList(new Deck_Card("A"), new Deck_Card("B"));
        List<Deck_Card> hand_B_A = Arrays.asList(new Deck_Card("B"), new Deck_Card("A"));
        List<Deck_Card> hand_B_B = Arrays.asList(new Deck_Card("B"), new Deck_Card("B"));

        Scenario<Base_Card> xor_test = new Scenario<>("xor test",
                new Xor_Operator_Node<>(
                        new Leaf_Node<>("A", new Base_Card("A")),
                        new Leaf_Node<>("A", new Base_Card("A"))
                ));

        System.out.println("xor test (A, A) has " + (xor_test.evaluate(hand_A_A) ? "failed." : "succeed."));
        System.out.println("xor test (A, B) has " + (xor_test.evaluate(hand_A_B) ? "succeed." : "failed."));
        System.out.println("xor test (B, A) has " + (xor_test.evaluate(hand_B_A) ? "succeed." : "failed."));
        System.out.println("xor test (B, B) has " + (xor_test.evaluate(hand_B_B) ? "failed." : "succeed."));
    }
}
