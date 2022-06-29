package com.tjger.game;

import at.hagru.hgbase.gui.ProgressState;

/**
 * Base class for human players.
 *
 * @author hagru
 */
public class HumanPlayer extends GamePlayer {

    public HumanPlayer(String playerType, String playerName, String pieceColor) {
        super(playerType, playerName, pieceColor);
    }

    /* (non-Javadoc)
     * @see tjger.game.GamePlayer#considerMove()
     */
    @Override
    public void considerMove() {
        // Do nothing, performMove is called by the GUI.
        getGameManager().getMainFrame().setStatusProgress(ProgressState.STATE_NORMAL);
    }
}
