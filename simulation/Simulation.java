package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import structure.Scenario;

/**
<b>
Purpose: final step which performs the actual simulation.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04
</b>
*/

public class Simulation<T, U>
{
    /**
     * Main deck which the hand will be generated from.
     */
    private final ArrayList<T> DECK;

    /**
     * Stores the generated scenarios.
     */
    private final HashMap<String, Scenario<U>> FOREST;

    /**
     * Constructor, note creates a shallow copy.
     * 
     * @param DECK which hands will be created from
     * @param FOREST the {@link Scenario} objects which will be tested.
     */
    public Simulation(final ArrayList<T> DECK, final HashMap<String, Scenario<U>> FOREST)
    {
        //shallow copy do to intended internal use.
        this.DECK = DECK;
        this.FOREST = FOREST;
    }

    /**
     * Performs simulation.
     * 
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * @return the result of the simulation
     */
    public String simulate(final int HAND_SIZE, final int TEST_HAND_COUNT)
    {
        //TODO: finish
        //TODO: parallelize at 2 points, 1) at forest level (duplicate decks), 2) scenario level (duplicate decks.)
        throw new UnsupportedOperationException("Not finished.");
    }
}
