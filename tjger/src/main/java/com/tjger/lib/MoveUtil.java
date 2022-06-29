package com.tjger.lib;

import com.tjger.game.MoveInformation;
import com.tjger.game.SplitableMove;

/**
 * Utility class for move information.
 * 
 * @author hagru
 */
public class MoveUtil {

    /**
     * Prevent instantiation.
     */
    private MoveUtil() {
        super();
    }

    /**
     * @param move The move to test if the move is complete.
     * @return True if the move is complete, otherwise false.
     */
    public static boolean isMoveComplete(MoveInformation move) {
        if (move instanceof SplitableMove) {
            // look if the splitable move is complete
            return ((SplitableMove) move).isMoveComplete();
        } else {
            // if it's not a splitable move, the move is always complete
            return true;
        }
    }    
}
