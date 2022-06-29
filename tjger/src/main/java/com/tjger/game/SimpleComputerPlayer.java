package com.tjger.game;

import com.tjger.game.completed.GameManager;
import com.tjger.lib.TimeAction;

/**
 * A typical computer player with a minimum thinking time for a move.
 * 
 * @author hagru
 */
public abstract class SimpleComputerPlayer extends GamePlayer {
    
    private MoveInformation nextMove;

    public SimpleComputerPlayer(String playerType, String playerName, String pieceColor) {
        super(playerType, playerName, pieceColor);
    }

    /**
     * Return the next move to be done by the computer.
     * 
     * @return the next move
     */
    protected abstract MoveInformation getNextMove();

    /**
     * @return the currently set minimum thinking time for the computer
     */
    public int getThinkingTime() {
        return GameManager.getInstance().getGameConfig().getDelayPlayerWithSpeedFactor();
    }

    /* (non-Javadoc)
     * @see tjger.game.GamePlayer#considerMove()
     */
    @Override
    public void considerMove() {
        nextMove = null;
        TimeAction.run(new TimeAction(getThinkingTime()) {
            @Override
            public void doAction() {
                nextMove = getNextMove();
            }
            @Override
            public void afterAction() {
                performMove(nextMove);
            }
        });
    }
}
