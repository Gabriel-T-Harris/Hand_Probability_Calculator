/*
    Copyright (C) 2022 Gabriel Toban Harris

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
package structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
<b>
Purpose: entire structure is to carry out combinatorial algorithm<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-06-04/2022-6-18/2022-6-19/2022-10-8/2022-10-10/2022-10-31/2023-1-7/2023-1-22
</b>
* @param <E> is the type of the objects being chosen
*/

public class Combinatorial_Iteration<E>
{
    /**
     * Meant to be used to allow {@link Combinatorial_Iteration} to be used concurrently.
     */
    public final class Combinatorial_Iteration_Save_Point
    {
        /**
         * Together they represent the currently chosen combination.
         */
        private int[] INDICES;

        /**
         * Store current combination to avoid regenerating storage.
         */
        private final ArrayList<E> CURRENT_COMBINATION;

        /**
         * Basic constructor.
         */
        public Combinatorial_Iteration_Save_Point()
        {
            this.INDICES = Combinatorial_Iteration.set_up_indices(Combinatorial_Iteration.this.get_choice_count());
            this.CURRENT_COMBINATION = new ArrayList<E>(Combinatorial_Iteration.this.get_choice_count());
            for (int i = 0; i < Combinatorial_Iteration.this.get_choice_count(); ++i)
                this.CURRENT_COMBINATION.add(null); //set placeholders
            Combinatorial_Iteration.this.recalculate_current_combination(this.INDICES, this.CURRENT_COMBINATION);
        }

        //getters
        /**
         * Returns the current combination as a result of the current choices.
         * 
         * @return a representation of the current combination
         */
        public Collection<E> get_safe_current_combination()
        {
            return Collections.unmodifiableCollection(this.CURRENT_COMBINATION);
        }

        /**
         * <p>Returns the current combination as a result of the current choices.</p>
         * 
         * <p>Note returned value is a shallow copy. However {@link Combinatorial_Iteration} only writes to it and does not read from it.
         * Thus changes are fine. However changes to underlying size of the container and load factor may cause problems.
         * As such, this is only meant for internal use that is going for speed by not having safety.</p>
         * 
         * @return a representation of the current combination
         */
        ArrayList<E> get_current_combination()
        {
            return this.CURRENT_COMBINATION;
        }

        @Override
        public String toString()
        {
            return "Combinatorial_Iteration_Save_Point INDICES: " + Arrays.toString(this.INDICES);
        }

        /**
         * Function to check if all the iterations have been carried out. Idea is that at the end, in order the last options should be chosen.
         * 
         * @return true for all combinations iterated over and false for more to go over
         */
        public boolean done()
        {
            return Combinatorial_Iteration.this.done_subroutine(this.INDICES);
        }

        /**
         * Simply resets internal values such that the data structure can be reused.
         */
        public void reset_internals()
        {
            Combinatorial_Iteration.reset_combinatorial_algorithm(this.INDICES);
        }
    }

    /**
     * Cache simple value to avoid recalculation.
     */
    private final int INDICES_END_INDEX;

    /**
     * Together they represent the currently chosen combination.
     */
    private final int[] INDICES;

    /**
     * Container of the choices being chosen from.
     */
    private final E[] OPTIONS;

    /**
     * Store current combination to avoid regenerating storage.
     */
    private final ArrayList<E> CURRENT_COMBINATION;

    /**
     * Parameterized constructor.
     * 
     * @param choose is number of choices to make
     * @param OPTIONS is what can be chosen, shallow copy
     * 
     * @throws IllegalArgumentException when choose is non-positive, OPTIONS is empty, or choose > OPTIONS.length
     */
    public Combinatorial_Iteration(int choose, final E[] OPTIONS) throws IllegalArgumentException
    {
        if (choose < 0)
            throw new IllegalArgumentException("Error: CHOOSE must be positive, yet received \"" + choose + "\" instead.");
        else if (OPTIONS.length < 1)
            throw new IllegalArgumentException("Error: must have OPTIONS to choose from, thus array cannot be empty.");
        else if (choose > OPTIONS.length)
            throw new IllegalArgumentException("Error: CHOOSE must be <= OPTIONS's length, yet received CHOOSE is \"" + choose + "\" and OPTIONS.length is \"" + OPTIONS.length +
                                               "\".");

        this.INDICES = Combinatorial_Iteration.set_up_indices(choose); //initialize starting indices

        this.OPTIONS = OPTIONS;
        this.INDICES_END_INDEX = this.get_choice_count() - 1;
        this.CURRENT_COMBINATION = new ArrayList<E>(this.get_choice_count());
        for (; choose > 0; --choose)
            this.CURRENT_COMBINATION.add(null); //set placeholders
        this.recalculate_current_combination(this.INDICES, this.CURRENT_COMBINATION);
    }

    //getters
    /**
     * Number of choices that is made per combination.
     * 
     * @return number of choices
     */
    public int get_choice_count()
    {
        return this.INDICES.length;
    }

    /**
     * Number of options to choose from to form a combination.
     * 
     * @return number of options
     */
    public int get_option_count()
    {
        return this.OPTIONS.length;
    }

    /**
     * Returns the underlying options being chosen from.
     * 
     * @return an unmodifiable view of the underlying options being chosen from
     */
    public Collection<E> get_options()
    {
        return Collections.unmodifiableCollection(Arrays.asList(this.OPTIONS));
    }

    /**
     * Returns the current combination as a result of the current choices.
     * 
     * @return a representation of the current combination
     */
    public Collection<E> get_safe_current_combination()
    {
        return Collections.unmodifiableCollection(this.CURRENT_COMBINATION);
    }

    /**
     * <p>Returns the current combination as a result of the current choices.</p>
     * 
     * <p>Note returned value is a shallow copy. However {@link Combinatorial_Iteration} only writes to it and does not read from it.
     * Thus changes are fine. However changes to underlying size of the container and load factor may cause problems.
     * As such, this is only meant for internal use that is going for speed by not having safety.</p>
     * 
     * @return a representation of the current combination
     */
    ArrayList<E> get_current_combination()
    {
        return this.CURRENT_COMBINATION;
    }

    @Override
    public String toString()
    {
        return "Combinatorial_Iteration [CURRENT_COMBINATION=" + CURRENT_COMBINATION + ", INDICES_END_INDEX=" + INDICES_END_INDEX + ", INDICES=" + Arrays.toString(INDICES) +
               ", OPTIONS=" + Arrays.toString(OPTIONS) + "]";
    }

    /**
     * Function to check if all the iterations have been carried out. Idea is that at the end, in order the last options should be chosen.
     * 
     * @return true for all combinations iterated over and false for more to go over
     */
    public boolean done()
    {
        return this.done_subroutine(this.INDICES);
    }

    /**
     * To allow various wrappers to call the underlying code.
     * 
     * @param INDICES means {@link Combinatorial_Iteration#INDICES}
     * 
     * @return true for all combinations iterated over and false for more to go over
     */
    private boolean done_subroutine(final int[] INDICES)
    {
        return INDICES[0] == this.get_option_count() - this.get_choice_count(); //Only have to check the first index, as it is the last to get into its final position.
    }

    /**
     * Calculates number of possible combinations. N choose K. Must be N >= K.
     * 
     * @param N is the number of items to choose from
     * @param K is the number of items to choose
     * 
     * @return N!/(K! * (N - K)!)
     * 
     * @throws IllegalArgumentException if N < K
     */
    public static long total_combination_count(int N, int K) throws IllegalArgumentException
    {
        if (N < K)
            throw new IllegalArgumentException("Error received: N = " + N + " and K = " + K + ", however it must be N >= K.");
        else if (K == N)
            return 1;

        long n_minus_k = N - K, to_return_numerator, to_return_divisor;

        if (N < K << 1)
        {
            //K! > (N - K)!
            to_return_numerator = N;
            for (--N; N > K; --N)
                to_return_numerator *= N;

            for (to_return_divisor = n_minus_k--; n_minus_k > 1; --n_minus_k)
                to_return_divisor *= n_minus_k;

            return to_return_numerator / to_return_divisor;
        }
        else if (N > K << 1)
        {
            //(N - K)! > K!
            for (to_return_numerator = N--; N > n_minus_k; --N)
                to_return_numerator *= N;

            for (to_return_divisor = K--; K > 1; --K)
                to_return_divisor *= K;

            return to_return_numerator / to_return_divisor;
        }
        else //K must be half of N.
        {
            //K! == (N - K)!
            final int CEILING = (int) Math.ceil(N / 4.0); //4 because half goes to K and the remaining half for even numbers
            long leftover_odd_numbers_count = n_minus_k - CEILING;

            to_return_numerator = 1 << CEILING;

            for (--N; N >= K + 1; N -= 2)
                to_return_numerator *= N;

            for (to_return_divisor = leftover_odd_numbers_count--; leftover_odd_numbers_count > 1; --leftover_odd_numbers_count)
                to_return_divisor *= leftover_odd_numbers_count;

            return to_return_numerator / to_return_divisor;
        }
    }

    /**
     * <p>Creates next combination.</p>
     *
     * <p>Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.</p>
     * 
     * <p>Takes care of safety, thus meant to be used for general use.</p>
     * 
     * @return next combination
     * 
     * @throws NoSuchElementException when called with {@link #done()} returning false
     */
    public Collection<E> safe_next_combincation() throws NoSuchElementException
    {
        if (!this.done())
        {
            this.next_combination_subroutine(this.INDICES, this.CURRENT_COMBINATION);
            return this.get_safe_current_combination();
        }
        else
            throw new NoSuchElementException("Error: Combinatorial_Iteration.next_combination() has been called when it has no more combinations to make.");
    }

    /**
     * <p>Creates next combination in a multi-threaded safe way, by modifying {@link Combinatorial_Iteration_Save_Point} rather than internals of {@link Combinatorial_Iteration}.</p>
     *
     * <p>Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.</p>
     * 
     * <p>Takes care of safety, thus meant to be used for general use.</p>
     * 
     * @param POINT is the external place that data relating to the algorithm's iteration is stored
     * 
     * @return next combination
     * 
     * @throws NoSuchElementException when called with {@link #done()} returning false
     */
    public Collection<E> safe_next_combincation(final Combinatorial_Iteration_Save_Point POINT) throws NoSuchElementException
    {
        if (!POINT.done())
        {
            this.next_combination_subroutine(POINT.INDICES, POINT.CURRENT_COMBINATION);
            return POINT.get_safe_current_combination();
        }
        else
            throw new NoSuchElementException("Error: Combinatorial_Iteration.next_combination(Combinatorial_Iteration_Save_Point) has been called when it has no more combinations to make.");
    }

    /**
     * Allows one to access a specific combination, 0 is initial combination. 4 would be the equivalent of calling {@link Combinatorial_Iteration#safe_next_combincation()} 4 times with respect to what {@link Combinatorial_Iteration#get_safe_current_combination()} would return.
     * 
     * @param iteration is the combination to jump to
     * 
     * @return the specific requested combination
     * 
     * @throws IllegalArgumentException when the requested combination does not exist, combinations are [0, total number of combinations[
     */
    public ArrayList<E> get_specific_combination(long iteration) throws IllegalArgumentException
    {
        {
            final long MAXIMUM_ITERATION = Combinatorial_Iteration.total_combination_count(this.get_option_count(), this.get_choice_count());

            if (MAXIMUM_ITERATION <= iteration)
                throw new IllegalArgumentException("Error: specified iteration \"" + iteration + "\" is problematic. It is probably >= to " + MAXIMUM_ITERATION +
                                                   ", otherwise something went horribly wrong.");
        }

        final int[] INDICES_CLONE = Combinatorial_Iteration.set_up_indices(this.INDICES.length); //thingy to play with such that actual data members are unaffected

        {
            int move_amount; //amount to move
            final int FINAL_INDEX_SENTINEL = this.get_option_count() - 1; //maximum value for last index

            //calculate term
            while (0 < iteration)
            {
                if (INDICES_CLONE[INDICES_END_INDEX] != FINAL_INDEX_SENTINEL)
                {
                    //subtract then move
                    move_amount = FINAL_INDEX_SENTINEL - INDICES_CLONE[INDICES_END_INDEX];

                    if (iteration >= move_amount)
                    {
                        INDICES_CLONE[INDICES_END_INDEX] = FINAL_INDEX_SENTINEL;
                        iteration -= move_amount;
                    }
                    else
                    {
                        INDICES_CLONE[INDICES_END_INDEX] += iteration;
                        iteration = 0;
                    }
                }
                else
                {
                    this.shift(INDICES_CLONE);
                    --iteration;
                }
            }
        }

        //form result
        {
            final ArrayList<E> TO_RETURN = new ArrayList<E>();

            for (int i = 0; i < INDICES_CLONE.length; ++i)
                TO_RETURN.set(i, this.OPTIONS[INDICES_CLONE[i]]);

            return TO_RETURN;
        }
    }

    /**
     * Simply resets internal values such that the data structure can be reused.
     */
    public void reset_combinatorial_algorithm()
    {
        Combinatorial_Iteration.reset_combinatorial_algorithm(this.INDICES);
        this.recalculate_current_combination(this.INDICES, this.CURRENT_COMBINATION);
    }

    /**
     * <p>Creates next combination.</p>
     *
     * <p>Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.</p>
     * 
     * <p>Note, calling it when {@link Combinatorial_Iteration#done()} returns false is problematic. Thus, generally should not be called unless {@link Combinatorial_Iteration#done()} returns true.</p>
     * 
     * @return next combination
     */
    ArrayList<E> next_combincation()
    {
        this.next_combination_subroutine(this.INDICES, this.CURRENT_COMBINATION);
        return this.get_current_combination();
    }

    /**
     * <p>Creates next combination in a multi-threaded safe way, by modifying {@link Combinatorial_Iteration_Save_Point} rather than internals of {@link Combinatorial_Iteration}.</p>
     *
     * <p>Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.</p>
     * 
     * <p>Note, calling it when {@link Combinatorial_Iteration_Save_Point#done()} returns false is problematic. Thus, generally should not be called unless {@link Combinatorial_Iteration_Save_Point#done()} returns true.</p>
     * 
     * @param POINT is the external place that data relating to the algorithm's iteration is stored
     * 
     * @return next combination
     */
    ArrayList<E> next_combincation(final Combinatorial_Iteration_Save_Point POINT)
    {
        this.next_combination_subroutine(POINT.INDICES, POINT.CURRENT_COMBINATION);
        return POINT.get_current_combination();
    }

    /**
     * Updates CURRENT_COMBINATION to be up to date.
     * 
     * @param INDICES means {@link Combinatorial_Iteration#INDICES}
     * @param CURRENT_COMBINATION means {@link Combinatorial_Iteration#CURRENT_COMBINATION}
     */
    protected void recalculate_current_combination(final int[] INDICES, final ArrayList<E> CURRENT_COMBINATION)
    {
        for (int i = 0; i < this.get_choice_count(); ++i)
            CURRENT_COMBINATION.set(i, this.OPTIONS[INDICES[i]]);
    }

    /**
     * Subroutine to set up {@link Combinatorial_Iteration#INDICES} and {@link Combinatorial_Iteration.Combinatorial_Iteration_Save_Point#INDICES}.
     * 
     * @param size of the array to return
     * 
     * @return is the fully set up array
     */
    private static int[] set_up_indices(int size)
    {
        final int[] TO_RETURN = new int[size];

        //0 is already set by default
        for (--size; size > 0; --size)
            TO_RETURN[size] = size;//set true initial indices

        return TO_RETURN;
    }

    /**
     * Performs the shift part of the combinatorial algorithm.
     * 
     * @param INDICES is the argument that will be modified and what will be shifted
     */
    private void shift(final int[] INDICES)
    {
        //find shift point
        int shift_index = this.INDICES_END_INDEX - 1;

        while (INDICES[shift_index] + 1 == INDICES[shift_index + 1])
            --shift_index;

        //perform shift
        for (int shifted_index_value = ++INDICES[shift_index++]; shift_index <= this.INDICES_END_INDEX; ++shift_index)
            INDICES[shift_index] = ++shifted_index_value;
    }

    /**
     * Creates next combination.
     *
     * Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.
     * 
     * Takes care of safety, thus meant to be used for general use.
     * Subroutine of methods which generate combinations.
     * 
     * @param INDICES means {@link Combinatorial_Iteration#INDICES}
     * @param CURRENT_COMBINATION means {@link Combinatorial_Iteration#CURRENT_COMBINATION}
     */
    private void next_combination_subroutine(final int[] INDICES, final ArrayList<E> CURRENT_COMBINATION)
    {
        //attempt move
        if (INDICES[this.INDICES_END_INDEX] < this.get_option_count() - 1) //false when entered with length - 1 
            ++INDICES[this.INDICES_END_INDEX];
        //shift
        else
            this.shift(INDICES);

        this.recalculate_current_combination(INDICES, CURRENT_COMBINATION);
    }

    /**
     * Simply resets internal values such that tracking structure is reset.
     */
    private static void reset_combinatorial_algorithm(final int[] INDICES)
    {
        //set initial indices
        for (int i = 0; i < INDICES.length; ++i)
            INDICES[i] = i;
    }
}
