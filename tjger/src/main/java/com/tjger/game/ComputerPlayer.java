package com.tjger.game;

import com.tjger.lib.TimeAction;

/**
 * General computer player that performs a move after a minimum duration.
 */
abstract public class ComputerPlayer extends GamePlayer {
    /**
     * The move of the computer player.
     */
    private MoveInformation move;

    /**
     * Constructs a new instance.
     *
     * @param playerType The type of the player.
     * @param playerName The name of the player.
     * @param pieceColor The piece color of the player.
     */
    public ComputerPlayer(String playerType, String playerName, String pieceColor) {
        super(playerType, playerName, pieceColor);
    }

    /**
     * Returns the minimum duration for the turn.
     *
     * @return The minimum duration for the turn.
     */
    abstract protected long getTurnMinDuration();

    /**
     * Calculates the move of the computer.
     *
     * @return The move to perform.
     */
    abstract protected MoveInformation calculateMove();

    @Override
    public void considerMove() {
        TimeAction.run(new TimeAction(getTurnMinDuration()) {
            @Override
            public void doAction() {
                move = calculateMove();
            }

            @Override
            public void afterAction() {
                // Do the move.
                if (move != null) {
                    performMove(move);
                }
            }
        });
    }
}
