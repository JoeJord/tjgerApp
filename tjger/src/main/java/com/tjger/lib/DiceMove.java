package com.tjger.lib;

import com.tjger.game.MoveInformation;

/**
 * A simple implementation of a move that holds the information for a dice throw.
 * 
 * @author hagru
 */
public class DiceMove implements MoveInformation {

    public final static int INVALID_VALUE = 0; // an invalid dice value
    
    final private int value;

    public DiceMove(int value) {
        super();
        this.value = value;
    }

    /**
     * @return The dice value.
     */
    public int getValue() {
        return value;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DiceMove) {
            DiceMove dm2 = (DiceMove)o;
            return (value == dm2.getValue());
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Dice move " + getValue();
    }
}
