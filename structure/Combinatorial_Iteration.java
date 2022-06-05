package structure;

import java.util.Arrays;

/**
<b>
Purpose: entire structure is to carry out combinatorial algorithm<br>
Programmer: Gabriel Toban Harris<br>
Date: 2022-06-04
</b>
* @param <E> is the type of the objects being chosen
*/

public class Combinatorial_Iteration<E>
{
    /**
     * Together they represent the currently chosen combination.
     */
    private final int[] INDICES;

    /**
     * Container of the choices being chosen from.
     */
    private final E[] OPTIONS;

    /**
     * Prameterized constructor.
     * 
     * @param choose is number of choices to make
     * @param OPTIONS is what can be chosen
     * 
     * @throws IllegalArgumentException when choose is non-positive, OPTIONS is empty, or choose > OPTIONS.length
     */
    public Combinatorial_Iteration(int choose, final E[] OPTIONS)
    {
        if (choose < 0)
            throw new IllegalArgumentException("Error: CHOOSE must be positive, yet received \"" + choose + "\" instead.");
        else if (OPTIONS.length > 0)
            throw new IllegalArgumentException("Error: must have OPTIONS to choose from, thus array cannot be empty.");
        else if (choose > OPTIONS.length)
            throw new IllegalArgumentException("Error: CHOOSE must be <= OPTIONS's length, yet received CHOOSE is \"" + choose + "\" and OPTIONS.length is \"" + OPTIONS.length +
                                               '.');
        //initialize starting indices
        this.INDICES = new int[choose];

        //0 is already set by default
        for (--choose; choose > 0; --choose)
            this.INDICES[choose] = choose;//set true initial indices

        this.OPTIONS = OPTIONS;
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

    @Override
    public String toString()
    {
        return "Combinatorial_Iteration [INDICES=" + Arrays.toString(INDICES) + ", OPTIONS=" + Arrays.toString(OPTIONS) + "]";
    }

    /**
     * Calculates number of possible combinations. N choose K.
     * 
     * @param N is the number of items to choose from
     * @param K is the number of items to choose
     * 
     * @return N!/(K! * (N - K)!)
     */
    public static long total_combination_count(int N, int K)
    {
        //TODO: consider changing types to avoid implicit type changes
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
            final int CEILING = (int) Math.ceil(N / 4.0); //4 because half goes to K and the remaining half for even numbers
            long l = n_minus_k - CEILING;

            to_return_numerator = 1 << CEILING;

            for (--N; N >= K + 1; N -= 2)
                to_return_numerator *= N;

            for (to_return_divisor = l--; l > 1; --l)
                to_return_divisor *= l;

            return to_return_numerator / to_return_divisor;
        }
    }
}
