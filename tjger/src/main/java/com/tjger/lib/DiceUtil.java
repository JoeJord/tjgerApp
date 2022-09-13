package com.tjger.lib;

import java.util.Date;
import java.util.Random;

import androidx.annotation.NonNull;

/**
 * Some help methods for using dices.
 * 
 * @author hagru
 */
public class DiceUtil {

    final public static int DEFAULT_MIN = 1;
    final public static int DEFAULT_MAX = 6;
    private static final Random diceRandom = new Random(new Date().getTime());
    
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

    /**
     * Counts how often each value appears in the specified list of values.<br>
     * The result is returned as an array where each element represents a value. The first element (index 0) represents the lowest possible value, the last element represents the highest possible value.
     *
     * @param minValue The lowest possible value.
     * @param maxValue The highest possible value.
     * @param values The list of values to check.
     * @return An array with the result.
     * @throws IllegalArgumentException if {@code minValue} is below 0 or {@code maxValue} is below 0 or {@code maxValue} is lower {@code minValue} or a value in {@code values} is not between {@code minValue} and {@code maxValue}.
     */
    @NonNull
    public static int[] countValues(int minValue, int maxValue, @NonNull int[] values)
            throws IllegalArgumentException {
        if ((minValue < 0) || (maxValue < 0) || (maxValue < minValue)) {
            throw new IllegalArgumentException();
        }
        int[] countValues = new int[(maxValue - minValue) + 1]; // Array is initialized with 0's by default.

        // Count how often each value appears.
        for (int value : values) {
            if ((value < minValue) || (value > maxValue)) {
                throw new IllegalArgumentException("Value is not between minimum and maximum value");
            }
            int valueIndex = value - minValue; // Index 0 represents the lowest possible value.
            countValues[valueIndex]++;
        }

        return countValues;
    }

    /**
     * Counts how often each value appears in the specified list of values.<br>
     * The result is returned as an array where each element represents a value. The first element (index 0) represents the value 1, the last element (index 5) represents the value 6.
     *
     * @param values The list of values to check.
     * @return An array with the result.
     * @throws IllegalArgumentException if a value in {@code values} is not between 1 and 6.
     */
    @NonNull
    public static int[] countValues(@NonNull int[] values) throws IllegalArgumentException {
        return countValues(DEFAULT_MIN, DEFAULT_MAX, values);
    }
}
