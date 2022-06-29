package com.tjger.game;

/**
 * Base class for network players.<p>
 * NOTE: This is just a dummy implementation to keep other classes free of errors.
 *
 * @author hagru
 */
public class NetworkPlayer extends GamePlayer {

    public NetworkPlayer(String playerType, String playerName, String pieceColor) {
        super(playerType, playerName, pieceColor);
    }

    @Override
    public void considerMove() {
    	//NOCHECK: dummy implementation
    }

}
