package com.tjger.game;

import com.tjger.lib.AiAlgorithms;
import com.tjger.lib.AiMoveEvaluator;

/**
 * An computer player that uses the alpha-beta algorithm with a minimum thinking time.
 * 
 * @author hagru
 */
public abstract class SimpleAiComputerPlayer extends SimpleComputerPlayer implements AiMoveEvaluator {

    public SimpleAiComputerPlayer(String playerType, String playerName, String pieceColor) {
        super(playerType, playerName, pieceColor);
    }

    @Override
    protected MoveInformation getNextMove() {
        return AiAlgorithms.getMiniMaxMove(getSearchDepth(), this);
    }
    
    /**
     * @return the current search depth
     */
    protected abstract int getSearchDepth();

}
