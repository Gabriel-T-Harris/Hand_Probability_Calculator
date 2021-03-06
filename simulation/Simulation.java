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
package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import starting_point.Starting_Point;
import structure.Deck_Card;
import structure.Evaluable;
import structure.Reservable;
import structure.Scenario;

/**
<b>
Purpose: final step which performs the actual simulation.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-04/2021-8-[17-22]/2021-12-18/2021-12-20/2022-5-28
</b>
*/

public class Simulation
{
    /**
     * Object to act as a simple mutex on {@link #parallel_simulation(boolean, int, int, int)}. As 
     * {@link #parallel_simulation(boolean, int, int, int)} attempts to use all the resources, thus only one parallel simulation should occur at a given time. 
     */
    private final static Object PARALLEL_SIMULATION_LOCK = new Object();

    /**
     * Main deck which the hands will be generated from.
     */
    private final ArrayList<Deck_Card> DECK;

    /**
     * Stores the generated scenarios to be tested.
     */
    private final ArrayList<Scenario> FOREST;

    /**
     * Constructor, note creates shallow copies. Also calls {@link ArrayList#trimToSize()} on the arguments.
     * 
     * @param <C> any {@link Collection}
     * 
     * @param DECK which hands will be created from
     * @param FOREST the {@link Scenario} objects which will be tested
     * 
     * @throws IllegalArgumentException when either argument results in {@link ArrayList#isEmpty()}
     */
    public <C extends Collection<Scenario>> Simulation(final ArrayList<Deck_Card> DECK, final C FOREST)
    {
        //shallow copies due to intended internal use
        this.DECK = DECK;
        this.DECK.trimToSize();
        this.FOREST = Simulation.purify_forest(FOREST);
        
        if (this.FOREST.isEmpty())
            throw new IllegalArgumentException("FOREST must not be empty.");
        else if (this.DECK.isEmpty())
            throw new IllegalArgumentException("DECK must not be empty.");
    }

    /**
     * Subroutine for assembling the results of a simulation.
     * 
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * @param SIMULATION_DURATION of simulation in seconds
     * @param WRAPPED_VALUES a simple tuple with the names of that which was tested and the percentage of tested hands that matched
     * 
     * @return the created output
     */
    public static String assemble_results_subroutine(final int HAND_SIZE, final long TEST_HAND_COUNT, final double SIMULATION_DURATION, final Comparable_Result_Pair[] WRAPPED_VALUES)
    {
        final StringBuilder RESULTS = new StringBuilder(1024);

        //make output
        RESULTS.append("Note that precision falls off around the hundredth's place. Such is deemed acceptable given that only to about the tenth's place is required.\n" +
                       Starting_Point.AUTHORS + "\n\nhand size: ");
        RESULTS.append(HAND_SIZE);
        RESULTS.append("\nnumber of test hands: ");
        RESULTS.append(TEST_HAND_COUNT);
        RESULTS.append("\nSimulation duration (seconds): ");
        RESULTS.append(SIMULATION_DURATION);
        RESULTS.append("\n\n");

        Arrays.parallelSort(WRAPPED_VALUES, Collections.reverseOrder()); //sort from high to low

        for (int i = 0; i < WRAPPED_VALUES.length; ++i)
        {
            RESULTS.append("Chance of \"");
            RESULTS.append(WRAPPED_VALUES[i].NAME);
            RESULTS.append("\" is about ");
            RESULTS.append(WRAPPED_VALUES[i].PERCENTAGE);
            RESULTS.append("%\n");
        }

        return RESULTS.toString();
    }

    /**
     * <p>Subroutine for assembling the results of a simulation.</p>
     * 
     * <p>Note is expected that the length of MATCHES equals the size of EQUATIONS. As they have a one to one correspondence.</p>
     * 
     * @param <E> anything which extends {@link Evaluable}
     * @param <C> the type of cards in the deck
     * 
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * @param SIMULATION_DURATION of simulation in seconds
     * @param MATCHES number of times the corresponding equation was matched
     * @param EQUATIONS names of {@link Evaluable} that were just calculated and stored respectively in MATCHES
     * 
     * @return the created output
     */
    public static <E extends Evaluable, C extends Collection<E>> String assemble_results_subroutine(final int HAND_SIZE, final long TEST_HAND_COUNT, final double SIMULATION_DURATION, final int[] MATCHES, final C EQUATIONS)
    {
        assert (MATCHES.length == EQUATIONS.size());
        final Comparable_Result_Pair[] WRAPPED_VALUES = new Comparable_Result_Pair[MATCHES.length];

        //create Comparable_Result_Pair
        {
            final float TO_PERCENTAGE = 100f / TEST_HAND_COUNT;
            final Iterator<E> WALKER = EQUATIONS.iterator();

            for (int i = 0; i < WRAPPED_VALUES.length; ++i)
                WRAPPED_VALUES[i] = new Comparable_Result_Pair(MATCHES[i] * TO_PERCENTAGE, WALKER.next().NAME);
        }

        return Simulation.assemble_results_subroutine(HAND_SIZE, TEST_HAND_COUNT, SIMULATION_DURATION, WRAPPED_VALUES);
    }

    /**
     * <p>Subroutine for assembling the results of a simulation.</p>
     * 
     * <p>Note is expected that the length of MATCHES equals the size of EQUATIONS. As they have a one to one correspondence.</p>
     * 
     * @param <E> anything which extends {@link Evaluable}
     * @param <C> the type of cards in the deck
     * 
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * @param SIMULATION_DURATION of simulation in seconds
     * @param MATCHES number of times the corresponding equation was matched
     * @param EQUATIONS names of {@link Evaluable} that were just calculated and stored respectively in MATCHES
     * 
     * @return the created output
     */
    public static <E extends Evaluable, C extends Collection<E>> String assemble_results_subroutine(final int HAND_SIZE, final long TEST_HAND_COUNT, final double SIMULATION_DURATION, final AtomicInteger[] MATCHES, final C EQUATIONS)
    {
        assert (MATCHES.length == EQUATIONS.size());
        final Comparable_Result_Pair[] WRAPPED_VALUES = new Comparable_Result_Pair[MATCHES.length];

        //create Comparable_Result_Pair
        {
            final float TO_PERCENTAGE = 100f / TEST_HAND_COUNT;
            final Iterator<E> WALKER = EQUATIONS.iterator();

            for (int i = 0; i < WRAPPED_VALUES.length; ++i)
                WRAPPED_VALUES[i] = new Comparable_Result_Pair(MATCHES[i].get() * TO_PERCENTAGE, WALKER.next().NAME);
        }

        return Simulation.assemble_results_subroutine(HAND_SIZE, TEST_HAND_COUNT, SIMULATION_DURATION, WRAPPED_VALUES);
    }

    /**
     * Simple subroutine that initialises an {@link AtomicInteger} array.
     * 
     * @param LENGTH of array to be created
     * 
     * @return newly created array
     */
    public static AtomicInteger[] initialize_array(final int LENGTH)
    {
        final AtomicInteger[] TO_RETURN = new AtomicInteger[LENGTH];

        for (int i = 0; i < LENGTH; ++i)
            TO_RETURN[i] = new AtomicInteger();

        return TO_RETURN;
    }

    /**
     * Creates a new {@link ArrayList} from a shallowly copied {@link Collection} with only the {@link Scenario} which have their {@link Scenario#DISPLAY} set to true.
     * All of which is then {@link ArrayList#trimToSize()}.
     * 
     * @param <C> type of collection being passed
     * 
     * @param RAW_FOREST to be cut down to size, original container itself is unchanged 
     * 
     * @return an {@link ArrayList} of the the {@link Scenario} which will be evaluated later
     */
    public static <C extends Collection<Scenario>> ArrayList<Scenario> purify_forest(final C RAW_FOREST)
    {
        ArrayList<Scenario> filtered_trees = new ArrayList<Scenario>(RAW_FOREST.parallelStream().filter(tree -> tree.DISPLAY).collect(Collectors.toList()));
        filtered_trees.trimToSize();
        return filtered_trees;
    }

    /**
     * Subroutine to draw a hand. Note, returns shallow references. Additionally is intentionally not synchronized for performance.
     * 
     * @param <R> the type of cards in the deck
     * 
     * @param HAND_SIZE to draw, should be <= DECK's size
     * @param DECK to draw from
     * 
     * @return created hand, backed ({@link ArrayList#subList(int, int)}) by the original deck
     */
    public static <R extends Reservable> ArrayList<R> draw_hand(final int HAND_SIZE, final ArrayList<R> DECK)
    {
        Collections.shuffle(DECK);
        return new ArrayList<R>(DECK.subList(0, HAND_SIZE));
    }

    /**
     * Frees all cards in hand. {@link Reservable#release()}
     * 
     * @param <R> the type of cards in the deck
     * @param HAND to be reset
     */
    public static <R extends Reservable> void reset_hand(final ArrayList<R> HAND)
    {
        for (final R CARD : HAND)
            CARD.release();
    }

    /**
     * Passes arguments to {@link #simulate(boolean, boolean, int, int)} with first 2 arguments being false.
     * 
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * 
     * @return {@link #simulate(boolean, boolean, int, int)}
     * 
     * @see #simulate(boolean, boolean, int, int)
     */
    public String simulate(final int HAND_SIZE, final int TEST_HAND_COUNT)
    {
        return simulate(false, false, HAND_SIZE, TEST_HAND_COUNT);
    }

    /**
     * Performs simulation. By differing to appropriate simulation function.
     * 
     * @param OVERRIDE when true will jump straight to calling {@link #sequential_simulation(boolean, int, int)} rather then analyzing {@link Runtime#availableProcessors()} that this program has access to and acting accordingly.
     * @param DISPLAY_PROGRESS is to output simulation progress, true for outputs (slower option) and false for no outputs
     * @param HAND_SIZE of the hand which will be used in the simulation
     * @param TEST_HAND_COUNT number of times to run simulation
     * 
     * @return the result of the simulation
     */
    public String simulate(final boolean OVERRIDE, final boolean DISPLAY_PROGRESS, final int HAND_SIZE, final int TEST_HAND_COUNT)
    {
        //centralize some operations
        if (this.DECK.size() <= HAND_SIZE)
            return "HAND_SIZE is at least as big as the deck itself, thus no point in carrying out a simulation.";

        if (DISPLAY_PROGRESS)
            System.out.println();

        if (OVERRIDE)
            return this.sequential_simulation(DISPLAY_PROGRESS, HAND_SIZE, TEST_HAND_COUNT);

        final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

        if (CORE_COUNT > 1)
            return this.parallel_simulation(DISPLAY_PROGRESS, CORE_COUNT, HAND_SIZE, TEST_HAND_COUNT);
        else if (CORE_COUNT == 1)
            return this.sequential_simulation(DISPLAY_PROGRESS, HAND_SIZE, TEST_HAND_COUNT);
        else if (CORE_COUNT < 1)
            throw new UnknownError("Impossible Error: somehow value from \"Runtime.getRuntime().availableProcessors();\" resulted in \"" + CORE_COUNT + "\" which is < 1.");
        else
            throw new UnknownError("Impossible Error: somehow value from \"Runtime.getRuntime().availableProcessors();\" resulted in \"" + CORE_COUNT +
                                   "\" which is not < 1, == 1, nor > 1.");
    }

    /**
     * <p>Carries out serial simulation with negligible (if any) parallelization. The trade off off with {@link #parallel_simulation(boolean, int, int, int)} is time for resources.</p>
     * 
     * <p>Note: Only instance should ever be running due to calling {@link #draw_hand(int, ArrayList)}, thereby making this not synchronization safe.
     * Such is intentional for performance reasons. As well as it is not meant to be run in parallel, unlike {@link #parallel_simulation(boolean, int, int, int)}.</p>
     * 
     * @param DISPLAY_PROGRESS is to output simulation progress, true for outputs (slower option) and false for no outputs
     * @param HAND_SIZE of the hands drawn
     * @param TEST_HAND_COUNT number of hands to test
     * @param DECK to draw from, should not be empty
     * @param FOREST {@link Evaluable} to be tested, should not be empty
     * 
     * @return result of simulation
     */
    protected String sequential_simulation(final boolean DISPLAY_PROGRESS, final int HAND_SIZE, final int TEST_HAND_COUNT)
    {
        final long START_TIME = System.currentTimeMillis(); //simulation start time in milliseconds
        final int HITS[] = new int[this.FOREST.size()];

        if (DISPLAY_PROGRESS)
        {
            /**
             * Simple wrapper to have effectively final integer. Should be faster than {@link AtomicInteger}.
             */
            class Effectively_Final_Integer
            {
                /**
                 * Value being wrapped. Start at -1 so that call of {@link #increment_get()} will set it to 0.
                 * Thus saving a function call and lowering instruction count.
                 */
                private int counter = -1;

                //getter
                /**
                 * Simply a getter for {@link #counter}
                 * 
                 * @return value of {@link #counter}
                 */
                public int get_counter()
                {
                    return counter;
                }

                /**
                 * Increment {@link #counter}, then return result.
                 * 
                 * @return result of increment
                 */
                public int increment_get()
                {
                    return ++counter;
                }
            }

            final float PROGRESS_COEFFICIENT = 100f / TEST_HAND_COUNT;
            Effectively_Final_Integer hands_drawn = new Effectively_Final_Integer();
            //Have separate thread output progress to not create bottle neck.

            new Thread(() ->
            {
                do
                {
                    try
                    {
                        Thread.sleep(1000); //no need to run the program like every frame
                    }
                    catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                        System.err.println("Ending SEPERATE_SEQUENTIAL_PROGRESS_THREAD.");
                        break;
                    }

                    System.out.printf("\r%.5f%% of hands drawn.", hands_drawn.get_counter() * PROGRESS_COEFFICIENT);
                } while (hands_drawn.get_counter() < TEST_HAND_COUNT);

                System.out.println();
            }, "SEPERATE_SEQUENTIAL_PROGRESS_THREAD").start();

            for (; hands_drawn.increment_get() < TEST_HAND_COUNT;)
                this.sequential_simulation_subroutine(HAND_SIZE, HITS);
        }
        else
            for (int i = 0; i < TEST_HAND_COUNT; ++i)
                this.sequential_simulation_subroutine(HAND_SIZE, HITS);

        return Simulation.assemble_results_subroutine(HAND_SIZE, TEST_HAND_COUNT, (System.currentTimeMillis() - START_TIME) / 1000d, HITS, this.FOREST);
    }

    /**
     * Carries out parallel simulation. The trade off over {@link #sequential_simulation(boolean, int, int)} is resources for time.
     * 
     * @param DISPLAY_PROGRESS is to output simulation progress, true for outputs (slower option) and false for no outputs
     * @param CORE_COUNT number of available cores
     * @param HAND_SIZE of the hands drawn
     * @param TEST_HAND_COUNT number of hands to test
     * 
     * @return result of simulation
     */
    protected String parallel_simulation(final boolean DISPLAY_PROGRESS, final int CORE_COUNT, final int HAND_SIZE, final int TEST_HAND_COUNT)
    {
        /**
         * {@link Runnable} to test a single hand.
         */
        class Hand_Tester implements Runnable
        {
            /**
             * Counts number of times this test has passed.
             */
            private final AtomicInteger HIT_COUNTER;

            /**
             * How to test hand.
             */
            private final Scenario EQUATION;

            /**
             * Hand to be tested.
             */
            private final ArrayList<Deck_Card> TEST_HAND;

            /**
             * Basic constructor.
             * 
             * @param HIT_COUNTER thingy to increment in event of test being passed
             * @param EQUATION to perform testing on {@link #TEST_HAND} 
             * @param TEST_HAND to be tested by {@link #EQUATION}
             */
            public Hand_Tester(final AtomicInteger HIT_COUNTER, final Scenario EQUATION, final ArrayList<Deck_Card> TEST_HAND)
            {
                this.HIT_COUNTER = HIT_COUNTER;
                this.EQUATION = EQUATION;
                this.TEST_HAND = TEST_HAND;
            }

            @Override
            public void run()
            {
                if (this.EQUATION.evaluate(this.TEST_HAND))
                    this.HIT_COUNTER.incrementAndGet();
            }
        }

        /**
         * {@link Runnable} to manage the supplying of {@link Runnable} to a {@link ThreadPoolExecutor}.
         * Also manages said {@link ThreadPoolExecutor} as well. Meant to be singleton, though could theoretically have multiple instances.
         */
        class Task_Manager implements Runnable
        {
            /**
             * Multiplier which separates the buffer into a before and after. This controls the size of the before part, which implicitly affects the after part's size.
             * Before and after, referring to {@link ThreadPoolExecutor#execute(Runnable)} self to {@link #TASK_OVERSEER} for execution, which is in between both partition parts.
             */
            public final static double PARTITION_BUFFER_AMOUNT = 0.75d; //Feels like an optimal number, as it is between 50 and 100.

            /**
             * Initial number of partitions to have. Inversely related to {@link #partition_size}.
             * As {@link #modulate_parallelization()} will 'correct' it, the actual starting value is rather unimportant.
             */
            private int partition_count;

            /**
             * Size of total partition, inversely related to {@link #partition_count}.
             */
            private int partition_size;

            /**
             * Size of partition before {@link ThreadPoolExecutor#execute(Runnable)} self to {@link #TASK_OVERSEER}. Inversely related to {@link #partition_size} and also multiplied by {@link #PARTITION_BUFFER_AMOUNT}.
             */
            private int partition_buffer_size;

            /**
             * Where last iteration left off.
             */
            private int previous_end;

            /**
             * Used when DISPLAY_PROGRESS is true. Avoids repeated evaluations. 
             */
            private final float PROGRESS_COEFFICIENT = 100f / TEST_HAND_COUNT;

            /**
             * Simply avoid recalculating TEST_HAND_COUNT * {@link Task_Manager#PARTITION_BUFFER_AMOUNT}.
             */
            private final double CACHE_PARTITION_BUFFER_SIZE_PRODUCT;

            /**
             * Simply a counter that can be used to display progress.
             */
            private final AtomicInteger HANDS_DRAWN;

            /**
             * Task dispenser. 
             */
            private final ThreadPoolExecutor TASK_OVERSEER;

            /**
             * Makes notes of amount of times a given {@link Scenario} passed. 
             */
            private final AtomicInteger HITS[];

            /**
             * Deck to draw hands from.
             */
            private final ArrayList<Deck_Card> DECK;

            /**
             * {@link Scenario} to perform testing,
             */
            private final ArrayList<Scenario> FOREST;

            /**
             * Basic constructor.
             * 
             * @param TASK_OVERSEER is the task dispenser
             * @param HITS records amount of times {@link Scenario#evaluate} to true
             * @param DECK to draw from
             * @param FOREST how to test hands
             */
            public Task_Manager(ThreadPoolExecutor TASK_OVERSEER, AtomicInteger[] HITS, ArrayList<Deck_Card> DECK, ArrayList<Scenario> FOREST)
            {
                this.partition_count = (int) (Math.sqrt(TEST_HAND_COUNT) * Math.log(HAND_SIZE * FOREST.size())) + 1; //+1 to ensure is non-zero while being relatively negligible
                this.CACHE_PARTITION_BUFFER_SIZE_PRODUCT = TEST_HAND_COUNT * Task_Manager.PARTITION_BUFFER_AMOUNT;
                this.TASK_OVERSEER = TASK_OVERSEER;
                this.HITS = HITS;
                this.DECK = DECK;
                this.FOREST = FOREST;
                this.set_partition_count_related_values();

                if (DISPLAY_PROGRESS)
                {
                    this.HANDS_DRAWN = new AtomicInteger(0);

                    final Thread SEPERATE_PARALLEL_PROGRESS_THREAD = new Thread(() ->
                    {
                        do
                        {
                            try
                            {
                                Thread.sleep(1000); //no need to run the program like every frame
                            }
                            catch (InterruptedException ex)
                            {
                                ex.printStackTrace();
                                System.err.println("Ending SEPERATE_PARALLEL_PROGRESS_THREAD.");
                                break;
                            }

                            System.out.printf("\r%.5f%% of hands drawn.", this.HANDS_DRAWN.floatValue() * this.PROGRESS_COEFFICIENT);
                        } while (this.HANDS_DRAWN.get() < TEST_HAND_COUNT);
                    }, "SEPERATE_PARALLEL_PROGRESS_THREAD");
                    SEPERATE_PARALLEL_PROGRESS_THREAD.setDaemon(true);
                    SEPERATE_PARALLEL_PROGRESS_THREAD.start();
                }
                else
                    this.HANDS_DRAWN = null;
            }

            @Override
            public void run()
            {
                final int START = this.previous_end;

                this.modulate_parallelization();

                if ((this.previous_end = this.partition_size + START) < TEST_HAND_COUNT)
                {
                    final int END = this.previous_end; //cache value in case another thread makes it in
                    final int MIDDLE = this.partition_buffer_size + START;

                    this.run_subroutine(START, MIDDLE);
                    this.TASK_OVERSEER.execute(this);
                    this.run_subroutine(MIDDLE, END);
                }
                else
                {
                    this.run_subroutine(START, TEST_HAND_COUNT);
                    this.TASK_OVERSEER.shutdown();
                }
            }

            /**
             * Subroutine to set values affected by {@link #partition_count}.
             */
            protected void set_partition_count_related_values()
            {
                this.partition_size = TEST_HAND_COUNT / this.partition_count;
                this.partition_buffer_size = (int) (this.CACHE_PARTITION_BUFFER_SIZE_PRODUCT / this.partition_count);
            }

            /**
             * Routine to self adjust data members to optimize resource use for parallelization.
             */
            protected void modulate_parallelization()
            {
                //control number of core threads
                {
                    final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

                    if (AVAILABLE_PROCESSORS != this.TASK_OVERSEER.getCorePoolSize())
                    {
                        this.TASK_OVERSEER.setCorePoolSize(AVAILABLE_PROCESSORS);
                        return; //number of processors changed, so see what happens
                    }
                }

                //control partition size
                {
                    final int BLOCKING_QUEUE_SIZE = this.TASK_OVERSEER.getQueue().size();
                    //Magic numbers have been tested to be rather good, also feels right as well.
                    final int UPPER_THRESHOLD = 125;
                    final int LOWER_THRESHOLD = 25;

                    //10 ensures that lowest possible argument will still result in a value greater than 1.
                    if (BLOCKING_QUEUE_SIZE > UPPER_THRESHOLD)
                    {
                        this.partition_count *= Math.log10(10 - UPPER_THRESHOLD + BLOCKING_QUEUE_SIZE);
                        this.set_partition_count_related_values();
                    }
                    else if (LOWER_THRESHOLD > BLOCKING_QUEUE_SIZE)
                    {
                        this.partition_count /= Math.log10(10 + LOWER_THRESHOLD - BLOCKING_QUEUE_SIZE);
                        this.set_partition_count_related_values();
                    }
                }
            }

            /**
             * Subroutine to both dispatch and run {@link Hand_Tester}
             * 
             * @param START partition value inclusive
             * @param END partition value exclusive
             */
            protected void run_subroutine(int START, final int END)
            {
                if (DISPLAY_PROGRESS)
                    for (; START < END; ++START, this.HANDS_DRAWN.incrementAndGet())
                        this.parallel_simulation_subroutine();
                else
                    for (; START < END; ++START)
                        this.parallel_simulation_subroutine();
            }

            /**
             * Subroutine of {@link Simulation#parallel_simulation(boolean, int, int, int)}, point is to centralize a subpart of the code.
             */
            private void parallel_simulation_subroutine()
            {
                ArrayList<Deck_Card> current_hand;
                //synchronise transformation of shallow to deep
                synchronized (this)
                {
                    current_hand = Deck_Card.deep_copy(Simulation.draw_hand(HAND_SIZE, this.DECK));
                }

                this.TASK_OVERSEER.execute(new Hand_Tester(this.HITS[0], this.FOREST.get(0), current_hand));
                for (int j = 1; j < this.FOREST.size(); ++j)
                    this.TASK_OVERSEER.execute(new Hand_Tester(this.HITS[j], this.FOREST.get(j), Deck_Card.deep_copy(current_hand)));
            }
        }

        final long START_TIME; //simulation start time in milliseconds
        final AtomicInteger[] HITS = Simulation.initialize_array(this.FOREST.size()); //Track the number of successful hands found per Scenario.
        final ThreadPoolExecutor TASK_OVERSEER = new ThreadPoolExecutor(CORE_COUNT, CORE_COUNT << 1, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        TASK_OVERSEER.allowCoreThreadTimeOut(true);

        synchronized (Simulation.PARALLEL_SIMULATION_LOCK)
        {
            TASK_OVERSEER.prestartAllCoreThreads();
            START_TIME = System.currentTimeMillis();
            TASK_OVERSEER.execute(new Task_Manager(TASK_OVERSEER, HITS, this.DECK, this.FOREST));

            try
            {
                final int MAX_DURATION = 10;
                final TimeUnit TEMPORAL_UNIT = TimeUnit.MINUTES;
                final String TEMPORAL_UNIT_TEXT = TEMPORAL_UNIT.name().toLowerCase();
                if (!TASK_OVERSEER.awaitTermination(MAX_DURATION, TEMPORAL_UNIT))
                {
                    System.err.println("Simulation has taken about " + MAX_DURATION + " " + TEMPORAL_UNIT_TEXT + ". Should be done under 2 " + TEMPORAL_UNIT_TEXT +
                                       ". Unless dealing with super complex scenarios; or either the number of test hands to simulate or size of hands, greatly exceeded defaults." +
                                       " Though if an error message occurs, then it was probably that." +
                                       "\nStopping simulation as something likely went wrong. Results are likely inaccurate.");
                    TASK_OVERSEER.shutdownNow();
                }
                else
                    System.out.print("\r100.00000% of hands drawn.");
            }
            catch (InterruptedException ex)
            {
                System.err.println("Warning: Something went wrong with simulation, thus results are likely wrong.\n" + ex.getMessage());
                TASK_OVERSEER.shutdownNow();
            }
        }

        if (DISPLAY_PROGRESS)
            System.out.println();

        return Simulation.assemble_results_subroutine(HAND_SIZE, TEST_HAND_COUNT, (System.currentTimeMillis() - START_TIME) / 1000d, HITS, this.FOREST);
    }

    /**
     * Subroutine of {@link Simulation#sequential_simulation(boolean, int, int)}, point is to centralize a subpart of the code.
     * 
     * @param HAND_SIZE is the number of cards to draw
     * @param HITS stores the results of a hand's tests
     */
    private void sequential_simulation_subroutine(final int HAND_SIZE, final int[] HITS)
    {
        ArrayList<Deck_Card> current_hand = Simulation.draw_hand(HAND_SIZE, this.DECK);

        for (int j = 0; j < this.FOREST.size(); ++j)
        {
            if (this.FOREST.get(j).evaluate(current_hand))
                ++HITS[j];
            Simulation.reset_hand(current_hand);
        }
    }
}
