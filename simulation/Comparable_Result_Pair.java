package simulation;

/**
<b>
Purpose: Underlying value representing number of successful matches.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-08-21 (date of extraction from {@link Simulation})/2021-8-24
</b>
 */

public class Comparable_Result_Pair implements Comparable<Comparable_Result_Pair>
{
    /**
     * Underlying value representing number of successful matches.
     */
    public final Float PERCENTAGE;

    /**
     * Name of this pair.
     */
    public final String NAME;

    /**
     * Basic constructor.
     * 
     * @param PERCENTAGE percentage of number of times evaluation succeeded
     * @param NAME of pair
     */
    public Comparable_Result_Pair(final Float PERCENTAGE, final String NAME)
    {
        this.PERCENTAGE = PERCENTAGE;
        this.NAME = NAME;
    }

    @Override
    public int compareTo(final Comparable_Result_Pair OTHER)
    {
        final int FLOAT_COMPARISON = this.PERCENTAGE.compareTo(OTHER.PERCENTAGE);

        if (FLOAT_COMPARISON == 0)
            return -NAME.compareTo(OTHER.NAME); //negative to have its order reversed
        else
            return FLOAT_COMPARISON;
    }
}
