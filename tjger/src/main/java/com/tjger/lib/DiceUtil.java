package com.tjger.lib;

import java.util.Date;
import java.util.Random;

/**
 * Some help methods for using dices.
 * 
 * @author hagru
 */
public class DiceUtil {

    final public static int DEFAULT_MIN = 1;
    final public static int DEFAULT_MAX = 6;
    private static Random diceRandom = new Random(new Date().getTime());
    
    private DiceUtil() {
    	super();
    }
    
    /**
     * Throws a standard dice.
     * 
     * @return A value between 1 and 6.
     */
    public static int throwDice() {
        return throwDice(1, 6);
    }
    
    /**
     * Throws a dice of the given value range.
     * 
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A value between min and max.
     */
    public static int throwDice(int min, int max) {
        if (min>max) {
            int h = min;
            min = max;
            max = h;
        }
        return diceRandom.nextInt(max-min+1) + min;
    }

}
