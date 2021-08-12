package testing.evaluation;

import structure.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FiveCardPoker {
    enum Suit {
        Spades, Hearts, Diamonds, Clubs
    }

    enum Value {
        Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King
    }

    static class PlayingCard extends Deck_Card {
        Suit suit;
        Value value;

        public PlayingCard(Suit s, Value v)
        {
            super(String.format("%s of %s", v.name(), s.name()));
            this.suit = s;
            this.value = v;
        }

        public PlayingCard(PlayingCard other) {
            super(other);
            this.suit = other.suit;
            this.value = other.value;
        }

        @Override
        public boolean equals(final Object INPUT)
        {
            if (INPUT instanceof PlayingCard) {
                return this.suit == ((PlayingCard) INPUT).suit && this.value == ((PlayingCard) INPUT).value;
            }
            else if (INPUT instanceof PlayingSuit) {
                return this.suit == ((PlayingSuit) INPUT).suit;
            }
            else if (INPUT instanceof PlayingValue) {
                return this.value == ((PlayingValue) INPUT).value;
            }
            else {
                return super.equals(INPUT);
            }
        }
    }

    static class PlayingSuit extends Deck_Card {
        Suit suit;

        public PlayingSuit(Suit s) {
            super(s.name());
            this.suit = s;
        }

        public PlayingSuit(PlayingSuit s) {
            super(s);
            this.suit = s.suit;
        }
    }

    static class PlayingValue extends Deck_Card {
        Value value;

        public PlayingValue(Value v)
        {
            super(v.name());
            this.value = v;
        }

        public PlayingValue(PlayingValue s) {
            super(s);
            this.value = s.value;
        }
    }

    final static Leaf_Node<Deck_Card> hearts = new Leaf_Node<>("Hearts", new PlayingSuit(Suit.Hearts));
    final static Leaf_Node<Deck_Card> spades = new Leaf_Node<>("Spades", new PlayingSuit(Suit.Spades));
    final static Leaf_Node<Deck_Card> diamonds = new Leaf_Node<>("Diamonds", new PlayingSuit(Suit.Diamonds));
    final static Leaf_Node<Deck_Card> clubs = new Leaf_Node<>("Clubs", new PlayingSuit(Suit.Clubs));

    final static Leaf_Node<Deck_Card> ace = new Leaf_Node<>("Ace", new PlayingValue(Value.Ace));
    final static Leaf_Node<Deck_Card> two = new Leaf_Node<>("Two", new PlayingValue(Value.Two));
    final static Leaf_Node<Deck_Card> three = new Leaf_Node<>("Three", new PlayingValue(Value.Three));
    final static Leaf_Node<Deck_Card> four = new Leaf_Node<>("Four", new PlayingValue(Value.Four));
    final static Leaf_Node<Deck_Card> five = new Leaf_Node<>("Five", new PlayingValue(Value.Five));
    final static Leaf_Node<Deck_Card> six = new Leaf_Node<>("Six", new PlayingValue(Value.Six));
    final static Leaf_Node<Deck_Card> seven = new Leaf_Node<>("Seven", new PlayingValue(Value.Seven));
    final static Leaf_Node<Deck_Card> eight = new Leaf_Node<>("Eight", new PlayingValue(Value.Eight));
    final static Leaf_Node<Deck_Card> nine = new Leaf_Node<>("Nine", new PlayingValue(Value.Nine));
    final static Leaf_Node<Deck_Card> ten =  new Leaf_Node<>("Ten", new PlayingValue(Value.Ten));
    final static Leaf_Node<Deck_Card> jack = new Leaf_Node<>("Jack", new PlayingValue(Value.Jack));
    final static Leaf_Node<Deck_Card> queen = new Leaf_Node<>("Queen", new PlayingValue(Value.Queen));
    final static Leaf_Node<Deck_Card> king = new Leaf_Node<>("King", new PlayingValue(Value.King));

    final static Scenario<Deck_Card> pair_exists = new Scenario<>("PAIR EXISTS", new Or_Operator_Node<>(
        new And_Operator_Node<>(ace, ace), new Or_Operator_Node<>(
        new And_Operator_Node<>(two, two), new Or_Operator_Node<>(
        new And_Operator_Node<>(three, three), new Or_Operator_Node<>(
        new And_Operator_Node<>(four, four), new Or_Operator_Node<>(
        new And_Operator_Node<>(five, five), new Or_Operator_Node<>(
        new And_Operator_Node<>(six, six), new Or_Operator_Node<>(
        new And_Operator_Node<>(seven, seven), new Or_Operator_Node<>(
        new And_Operator_Node<>(eight, eight), new Or_Operator_Node<>(
        new And_Operator_Node<>(nine, nine), new Or_Operator_Node<>(
        new And_Operator_Node<>(ten, ten), new Or_Operator_Node<>(
        new And_Operator_Node<>(jack, jack), new Or_Operator_Node<>(
        new And_Operator_Node<>(queen, queen),
        new And_Operator_Node<>(king, king)
        ))))))))))))
    );

    final static Scenario<Deck_Card> triple_exists = new Scenario<>("TRIPLE EXISTS", new Or_Operator_Node<>(
            new And_Operator_Node<>(ace, new And_Operator_Node<>(ace, ace)), new Or_Operator_Node<>(
            new And_Operator_Node<>(two, new And_Operator_Node<>(two, two)), new Or_Operator_Node<>(
            new And_Operator_Node<>(three, new And_Operator_Node<>(three, three)), new Or_Operator_Node<>(
            new And_Operator_Node<>(four, new And_Operator_Node<>(four, four)), new Or_Operator_Node<>(
            new And_Operator_Node<>(five, new And_Operator_Node<>(five, five)), new Or_Operator_Node<>(
            new And_Operator_Node<>(six, new And_Operator_Node<>(six, six)), new Or_Operator_Node<>(
            new And_Operator_Node<>(seven, new And_Operator_Node<>(seven, seven)), new Or_Operator_Node<>(
            new And_Operator_Node<>(eight, new And_Operator_Node<>(eight, eight)), new Or_Operator_Node<>(
            new And_Operator_Node<>(nine, new And_Operator_Node<>(nine, nine)), new Or_Operator_Node<>(
            new And_Operator_Node<>(ten, new And_Operator_Node<>(ten, ten)), new Or_Operator_Node<>(
            new And_Operator_Node<>(jack, new And_Operator_Node<>(jack, jack)), new Or_Operator_Node<>(
            new And_Operator_Node<>(queen, new And_Operator_Node<>(queen, queen)),
            new And_Operator_Node<>(king, new And_Operator_Node<>(king, king))
    ))))))))))))
    );

    final static Scenario<Deck_Card> quad_exists = new Scenario<>("QUAD EXISTS", new Or_Operator_Node<>(
            new And_Operator_Node<>(ace, new And_Operator_Node<>(ace, new And_Operator_Node<>(ace, ace))), new Or_Operator_Node<>(
            new And_Operator_Node<>(two, new And_Operator_Node<>(two, new And_Operator_Node<>(two, two))), new Or_Operator_Node<>(
            new And_Operator_Node<>(three, new And_Operator_Node<>(three, new And_Operator_Node<>(three, three))), new Or_Operator_Node<>(
            new And_Operator_Node<>(four, new And_Operator_Node<>(four, new And_Operator_Node<>(four, four))), new Or_Operator_Node<>(
            new And_Operator_Node<>(five, new And_Operator_Node<>(five, new And_Operator_Node<>(five, five))), new Or_Operator_Node<>(
            new And_Operator_Node<>(six, new And_Operator_Node<>(six, new And_Operator_Node<>(six, six))), new Or_Operator_Node<>(
            new And_Operator_Node<>(seven, new And_Operator_Node<>(seven, new And_Operator_Node<>(seven, seven))), new Or_Operator_Node<>(
            new And_Operator_Node<>(eight, new And_Operator_Node<>(eight, new And_Operator_Node<>(eight, eight))), new Or_Operator_Node<>(
            new And_Operator_Node<>(nine, new And_Operator_Node<>(nine, new And_Operator_Node<>(nine, nine))), new Or_Operator_Node<>(
            new And_Operator_Node<>(ten, new And_Operator_Node<>(ten, new And_Operator_Node<>(ten, ten))), new Or_Operator_Node<>(
            new And_Operator_Node<>(jack, new And_Operator_Node<>(jack, new And_Operator_Node<>(jack, jack))), new Or_Operator_Node<>(
            new And_Operator_Node<>(queen, new And_Operator_Node<>(queen, new And_Operator_Node<>(queen, queen))),
            new And_Operator_Node<>(king, new And_Operator_Node<>(king, new And_Operator_Node<>(king, king)))
    ))))))))))))
    );

    final static Scenario<Deck_Card> flush_exists = new Scenario<>("FLUSH EXISTS", new Or_Operator_Node<>(
            new And_Operator_Node<>(spades, new And_Operator_Node<>(spades, new And_Operator_Node<>(spades, new And_Operator_Node<>(spades, spades)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(hearts, new And_Operator_Node<>(hearts, new And_Operator_Node<>(hearts, new And_Operator_Node<>(hearts, hearts)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(diamonds, new And_Operator_Node<>(diamonds, new And_Operator_Node<>(diamonds, new And_Operator_Node<>(diamonds, diamonds)))),
            new And_Operator_Node<>(clubs, new And_Operator_Node<>(clubs, new And_Operator_Node<>(clubs, new And_Operator_Node<>(clubs, clubs))))
    )))
    );

    final static Scenario<Deck_Card> straight_exists = new Scenario<>("STRAIGHT EXISTS", new Or_Operator_Node<>(
            new And_Operator_Node<>(ace, new And_Operator_Node<>(two, new And_Operator_Node<>(three, new And_Operator_Node<>(four, five)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(two, new And_Operator_Node<>(three, new And_Operator_Node<>(four, new And_Operator_Node<>(five, six)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(three, new And_Operator_Node<>(four, new And_Operator_Node<>(five, new And_Operator_Node<>(six, seven)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(four, new And_Operator_Node<>(five, new And_Operator_Node<>(six, new And_Operator_Node<>(seven, eight)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(five, new And_Operator_Node<>(six, new And_Operator_Node<>(seven, new And_Operator_Node<>(eight, nine)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(six, new And_Operator_Node<>(seven, new And_Operator_Node<>(eight, new And_Operator_Node<>(nine, ten)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(seven, new And_Operator_Node<>(eight, new And_Operator_Node<>(nine, new And_Operator_Node<>(ten, jack)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(seven, new And_Operator_Node<>(nine, new And_Operator_Node<>(ten, new And_Operator_Node<>(jack, queen)))), new Or_Operator_Node<>(
            new And_Operator_Node<>(nine, new And_Operator_Node<>(ten, new And_Operator_Node<>(jack, new And_Operator_Node<>(queen, king)))),
            new And_Operator_Node<>(ten, new And_Operator_Node<>(jack, new And_Operator_Node<>(queen, new And_Operator_Node<>(king, ace))))
    )))))))))
    );

    final static And_Operator_Node<Deck_Card> full_house_exists = new And_Operator_Node<>(
            pair_exists,
            triple_exists
    );

    final static And_Operator_Node<Deck_Card> two_pairs_exists = new And_Operator_Node<>(
            pair_exists,
            pair_exists
    );

    final static Scenario<Deck_Card> four_of_a_kind = new Scenario<>("FOURS OF A KIND", quad_exists);

    final static Scenario<Deck_Card> full_house = new Scenario<>("FULL HOUSE", full_house_exists);

    final static Scenario<Deck_Card> flush = new Scenario<>("FLUSH", flush_exists);

    final static Scenario<Deck_Card> straight = new Scenario<>("STRAIGHT", straight_exists);

    final static Scenario<Deck_Card> three_of_a_kind = new Scenario<>("THREE OF A KIND",
            new And_Operator_Node<>(
                    new Not_Operator_Node<>(new Or_Operator_Node<>(full_house, quad_exists)),
                    triple_exists
            )
    );

    final static Scenario<Deck_Card> two_pair = new Scenario<>("TWO PAIR",
            new And_Operator_Node<>(
                    new Not_Operator_Node<>(new Or_Operator_Node<>(triple_exists, quad_exists)),
                    two_pairs_exists
            )
    );

    final static Scenario<Deck_Card> one_pair = new Scenario<>("ONE PAIR",
            new And_Operator_Node<>(
                    new Not_Operator_Node<>(
                            new Or_Operator_Node<>(triple_exists,
                                    new Or_Operator_Node<>(two_pair, quad_exists)
                            )
                    ),
                    pair_exists
            )
    );

    final static List<Scenario<Deck_Card>> scenarios_to_test = Arrays.asList(one_pair, two_pair, three_of_a_kind, straight, flush, full_house, four_of_a_kind);
    final static Map<String, PlayingCard[]> hands = Stream.of(new Object[][] {
            { "Two Pair", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Hearts, Value.Two), new PlayingCard(Suit.Clubs, Value.Jack), new PlayingCard(Suit.Clubs, Value.Ace), new PlayingCard(Suit.Spades, Value.Jack) }},
            { "Full House", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Hearts, Value.Ace), new PlayingCard(Suit.Spades, Value.Two), new PlayingCard(Suit.Clubs, Value.Ace), new PlayingCard(Suit.Hearts, Value.Two) }},
            { "Flush", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Spades, Value.Three), new PlayingCard(Suit.Spades, Value.Five), new PlayingCard(Suit.Spades, Value.Seven), new PlayingCard(Suit.Spades, Value.Nine) }},
            { "Two of a Kind", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Clubs, Value.Three), new PlayingCard(Suit.Hearts, Value.Five), new PlayingCard(Suit.Spades, Value.Three), new PlayingCard(Suit.Spades, Value.Nine) }},
            { "Three of a Kind", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Two), new PlayingCard(Suit.Hearts, Value.Two), new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Spades, Value.Three), new PlayingCard(Suit.Clubs, Value.Two) }},
            { "Straight", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Five), new PlayingCard(Suit.Diamonds, Value.Seven), new PlayingCard(Suit.Spades, Value.Nine), new PlayingCard(Suit.Clubs, Value.Eight), new PlayingCard(Suit.Hearts, Value.Six) }},
            { "Four of a Kind", new PlayingCard[] { new PlayingCard(Suit.Spades, Value.Jack), new PlayingCard(Suit.Hearts, Value.Jack), new PlayingCard(Suit.Spades, Value.Ace), new PlayingCard(Suit.Clubs, Value.Jack), new PlayingCard(Suit.Diamonds, Value.Jack) }},
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (PlayingCard[]) data[1]));

    public static void main(String[] args) {
        for (Map.Entry<String, PlayingCard[]> entry : hands.entrySet()) {
            System.out.println(entry.getKey());
            PlayingCard[] hand = entry.getValue();
            for (Scenario<Deck_Card> test : scenarios_to_test) {
                List<Deck_Card> temp = Stream.of(hand).map(PlayingCard::new).collect(Collectors.toList());
                boolean result = test.evaluate(temp);
                if (result) {
                    System.out.printf("Test %s passed with: [", test.NAME);
                    String cards_used = temp.stream()
                            .filter(Deck_Card::isReserved)
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));

                    System.out.printf("%s]\n", cards_used);
                }
            }
            System.out.println();
        }

    }
}
