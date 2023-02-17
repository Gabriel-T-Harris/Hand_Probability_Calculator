package testing.evaluation;

import java.util.ArrayList;
import simulation.special_ability.Game_State;
import simulation.special_ability.Special_Ability_Banish;
import simulation.special_ability.Special_Ability_Base;
import simulation.special_ability.Special_Ability_Draw;
import simulation.special_ability.Special_Ability_Manager;
import simulation.special_ability.Special_Ability_Mill;
import simulation.special_ability.Special_Ability_Reasoning;
import structure.Deck_Card;

/**
<b>
Purpose: simply test that {@link simulation.special_ability} works properly<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-12-26
</b>
*/

public class Special_Ability_Test
{
    public static void main(String[] args)
    {
        //simple test of abilities
        final Special_Ability_Manager CARD_EFFECTS = new Special_Ability_Manager();
        final Game_State<Deck_Card> CARD_TRACKER;
        final ArrayList<Deck_Card> HAND = new ArrayList<Deck_Card>(), DECK = new ArrayList<Deck_Card>();

        //set up basic card effects
        {
            final String[] SPECAIL_CARD_NAMES = {"Draw Stuff", "Mill Stuff", "Banish Stuff", "Draw 0"};

            CARD_EFFECTS.add(1, SPECAIL_CARD_NAMES[0], new Special_Ability_Draw(1));
            CARD_EFFECTS.add(SPECAIL_CARD_NAMES[3], new Special_Ability_Draw(0));
            CARD_EFFECTS.add(SPECAIL_CARD_NAMES[1], new Special_Ability_Mill(1));
            CARD_EFFECTS.add(SPECAIL_CARD_NAMES[2], new Special_Ability_Banish(1));

            //make hand
            //top of deck are special cards
            HAND.add(new Deck_Card(SPECAIL_CARD_NAMES[0]));
            for (String name : SPECAIL_CARD_NAMES)
                HAND.add(new Deck_Card(name));
        }

        //make arbitrarily large deck
        for (int i = 0; i < 20; ++i)
            DECK.add(new Deck_Card("Do Nothing Card"));

        CARD_TRACKER = new Game_State<Deck_Card>(HAND, DECK);
        CARD_EFFECTS.parse(CARD_TRACKER);

        System.out.println(CARD_TRACKER); //Output should be: a "Do Nothing Card" banished, a "Do Nothing Card" in GY along with only one of each special cards, and hand should have "Do Nothing Card" and "Draw Stuff" , with no other locations.

        //reasoning test
        {
            final Deck_Card REASONING_STOPPING_POINT = new Deck_Card("Stop Reasoning");
            final ArrayList<Deck_Card> REASONING_ARGUMENT = new ArrayList<Deck_Card>();
            REASONING_ARGUMENT.add(REASONING_STOPPING_POINT);

            CARD_EFFECTS.add("Reasoning", new Special_Ability_Reasoning(REASONING_ARGUMENT));
            DECK.add(8, REASONING_STOPPING_POINT); //before call, has 17 "Do Nothing"
            HAND.add(new Deck_Card("Reasoning"));

            CARD_TRACKER.set_cards(Game_State.Locations.MAIN_DECK, DECK);
            CARD_EFFECTS.parse(CARD_TRACKER);

            System.out.println(CARD_TRACKER); //Output should be: a "Do Nothing Card" banished, a "Do Nothing Card" in GY along with only one of each special cards + another copy of "Draw Stuff" + "Reasoning" + another 7 copies of "Do Nothing", and hand should have 2 "Do Nothing Card", field should have "Stop Reasoning", with no other locations.

            //complex test
            CARD_EFFECTS.add("Superduper Reasoning", new Special_Ability_Base[]{new Special_Ability_Reasoning(REASONING_ARGUMENT), new Special_Ability_Draw(10)});
            DECK.add(8, REASONING_STOPPING_POINT);
            HAND.add(new Deck_Card("Superduper Reasoning"));

            CARD_TRACKER.set_cards(Game_State.Locations.MAIN_DECK, DECK);
            CARD_EFFECTS.parse(CARD_TRACKER);

            System.out.println(CARD_TRACKER); //Output should be: a "Do Nothing Card" banished, a "Do Nothing Card" in GY along with only one of each special cards + another copy of "Draw Stuff" + "Reasoning" + another 7 copies of "Do Nothing", and hand should have 2 "Do Nothing Card" and "Superduper Reasoning", field should have "Stop Reasoning", with no other locations.
        }
    }
}
