package simulation.special_ability;

/**
<b>
Purpose: acts as the base special ability that others descend from for polymorphic special ability handling.<br>
Programmer: Gabriel Toban Harris<br>
Date: 2021-12-25/2021-12-30/2022-1-1
</b>
*/

public interface Special_Ability_Base
{
    /**
     * Carry out the implemented special ability.
     * 
     * @param CURRENT_STATE which will be internally modified
     * 
     * @return true for carried out and false for could not be carried out
     */
    public abstract boolean perform_special_ability(final Game_State<?> CURRENT_STATE);
}
