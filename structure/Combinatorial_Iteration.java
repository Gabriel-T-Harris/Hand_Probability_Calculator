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

/**
<b>
Purpose: entire structure is to carry out combinatorial algorithm<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-06-04/2022-6-18/2022-6-19/2022-10-8/2022-10-10/2022-10-31
</b>
* @param <E> is the type of the objects being chosen
*/

public class Combinatorial_Iteration<E>
{
    /**
     * Store current combination to avoid regenerating storage.
     */
    private ArrayList<E> current_combination;

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
     * Parameterized constructor.
     * 
     * @param choose is number of choices to make
     * @param OPTIONS is what can be chosen, shallow copy
     * 
     * @throws IllegalArgumentException when choose is non-positive, OPTIONS is empty, or choose > OPTIONS.length
     */
    public Combinatorial_Iteration(int choose, final E[] OPTIONS)
    {
        if (choose < 0)
            throw new IllegalArgumentException("Error: CHOOSE must be positive, yet received \"" + choose + "\" instead.");
        else if (OPTIONS.length < 1)
            throw new IllegalArgumentException("Error: must have OPTIONS to choose from, thus array cannot be empty.");
        else if (choose > OPTIONS.length)
            throw new IllegalArgumentException("Error: CHOOSE must be <= OPTIONS's length, yet received CHOOSE is \"" + choose + "\" and OPTIONS.length is \"" + OPTIONS.length +
                                               "\".");
        //initialize starting indices
        this.INDICES = new int[choose];

        //0 is already set by default
        for (--choose; choose > 0; --choose)
            this.INDICES[choose] = choose;//set true initial indices

        this.OPTIONS = OPTIONS;
        this.INDICES_END_INDEX = this.INDICES.length - 1;
        this.current_combination = new ArrayList<E>(this.INDICES.length);
        for (int i = 0; i < this.INDICES.length; ++i)
            this.current_combination.add(null); //set placeholders
        this.recalculate_current_combination();
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
        return Collections.unmodifiableCollection(this.current_combination);
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
        return this.current_combination;
    }

    /**
     * Function to check if all the iterations have been carried out. Idea is that at the end, in order the last options should be chosen.
     * 
     * @return true for all combinations iterated over and false for more to go over
     */
    public boolean done()
    {
        for (int i = 0, k = this.OPTIONS.length - this.INDICES.length; i < this.INDICES.length; ++i, ++k)
            if (this.INDICES[i] != k)
                return false;

        return true;
    }
    
    @Override
    public String toString()
    {
        return "Combinatorial_Iteration [current_combination=" + current_combination + ", INDICES_END_INDEX=" + INDICES_END_INDEX + ", INDICES=" + Arrays.toString(INDICES) +
               ", OPTIONS=" + Arrays.toString(OPTIONS) + "]";
    }

    /**
     * Calculates number of possible combinations. N choose K. Must be N >= K.
     * 
     * @param N is the number of items to choose from
     * @param K is the number of items to choose
     * 
     * @return N!/(K! * (N - K)!)
     */
    public static long total_combination_count(int N, int K)
    {
        if (N < K)
            throw new IllegalArgumentException("Error received: N = " + N + " and K = " + K + ", however it must be N >= K.");
        else if (K == N)
            return 1;

        long n_minus_k = N - K;
        long to_return_numerator;
        long to_return_divisor;

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
     * Creates next combination.
     *
     * Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.
     * 
     * Takes care of safety, thus meant to be used for general use.
     * 
     * @return next combination
     * 
     * @throws java.util.NoSuchElementException when called with {@link #done()} returning false
     */
    public Collection<E> safe_next_combincation()
    {
        if (!this.done())
        {
            //attempt move
            if (this.INDICES[this.INDICES_END_INDEX] < this.get_option_count() - 1) //false when entered with length - 1 
                ++this.INDICES[this.INDICES_END_INDEX];
            //shift
            else
            {
                //find shift point
                int shift_index = this.INDICES_END_INDEX - 1;

                while (this.INDICES[shift_index] + 1 == this.INDICES[shift_index + 1])
                    --shift_index;

                //perform shift
                for (int shifted_index_value = ++this.INDICES[shift_index++]; shift_index <= this.INDICES_END_INDEX; ++shift_index)
                    this.INDICES[shift_index] = ++shifted_index_value;
            }

            this.recalculate_current_combination();
            return this.get_safe_current_combination();
        }
        else
            throw new java.util.NoSuchElementException("Error: Combinatorial_Iteration.next_combination() has been called when it has no more combinations to make.");
    }

    /**
     * Simply resets internal values such that the data structure can be reused.
     */
    public void reset_combinatorial_algorithm()
    {
        //set initial indices
        for (int i = 0; i < this.INDICES.length; ++i)
            this.INDICES[i] = i;

        this.recalculate_current_combination();
    }

    /**
     * <p>Creates next combination.</p>
     *
     * <p>Refer to notes for greater explanation, but essentially only move around right most.
     * Shift indices when can no longer move, repeat process until done. When cannot shift, is done.</p>
     * 
     * <p>Note, calling it when {@link #done()} returns false is problematic. Thus, generally should not be called unless {@link #done()} returns true.</p>
     * 
     * @return next combination
     */
    ArrayList<E> next_combincation()
    {
        //attempt move
        if (this.INDICES[this.INDICES_END_INDEX] < this.get_option_count() - 1) //false when entered with length - 1 
            ++this.INDICES[this.INDICES_END_INDEX];
        //shift
        else
        {
            //find shift point
            int shift_index = this.INDICES_END_INDEX - 1;

            while (this.INDICES[shift_index] + 1 == this.INDICES[shift_index + 1])
                --shift_index;

            //perform shift
            for (int shifted_index_value = ++this.INDICES[shift_index++]; shift_index <= this.INDICES_END_INDEX; ++shift_index)
                this.INDICES[shift_index] = ++shifted_index_value;
        }

        this.recalculate_current_combination();
        return this.get_current_combination();
    }

    /**
     * Updates {@link #current_combination} to be up to date.
     */
    private void recalculate_current_combination()
    {
        for (int i = 0; i < this.INDICES.length; ++i)
            this.current_combination.set(i, this.OPTIONS[this.INDICES[i]]);
    }
}
