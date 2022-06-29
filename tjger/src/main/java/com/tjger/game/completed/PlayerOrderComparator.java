package com.tjger.game.completed;

import java.io.Serializable;
import java.util.Comparator;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The player order comparator is used to get the active players in the correct order.
 * For that a field with Integer values is used.
 *
 * @author hagru
 */
class PlayerOrderComparator implements Comparator<Integer>, Serializable {

    private int playerOrder;

    public PlayerOrderComparator(int playerOrder) {
        this.playerOrder = playerOrder;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Integer o1, Integer o2) {
        int i1 = o1.intValue();
        int i2 = o2.intValue();
        // the player with index 0 is always first
        if (i1 == 0) {
            return -1;
        }
        if (i2 == 0) {
            return 1;
        }
        boolean is1Odd = HGBaseTools.isOdd(i1);
        boolean is2Odd = HGBaseTools.isOdd(i2);
        // depend on the player order
        if (playerOrder == GameConfig.PLAYERS_CLOCKWISE) {
            // take first the odd than the even values
            if (is1Odd && !is2Odd) {
                return -1;
            }
            if (!is1Odd && is2Odd) {
                return 1;
            }
            if (is1Odd) {
                return o1.compareTo(o2);
            } else {
                return o2.compareTo(o1);
            }
        } else if (playerOrder == GameConfig.PLAYERS_COUNTERCLOCKWISE) {
            // take first the even than the odd
            if (!is1Odd && is2Odd) {
                return -1;
            }
            if (is1Odd && !is2Odd) {
                return 1;
            }
            if (!is1Odd) {
                return o1.compareTo(o2);
            } else {
                return o2.compareTo(o1);
            }
        } else {
            // the default left-to-right
            return o1.compareTo(o2);
        }
    }

}