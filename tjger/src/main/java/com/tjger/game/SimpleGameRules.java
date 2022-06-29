package com.tjger.game;

import com.tjger.lib.ConstantValue;

/**
 * A subclass of {@link GameRules} that includes some basic functionality for the start and the next player.
 * 
 * @author hagru
 */
public abstract class SimpleGameRules extends GameRules {

    @Override
    public GamePlayer getStartPlayer(GameState gameState) {
        GamePlayer player = getGameEngine().getFirstPlayer();
        return (player == null)? getGameEngine().getCyclingFirstGamePlayer()
                               : getGameEngine().getNextPlayer(player, ConstantValue.EXCLUDE_DROPOUT);
    }

    @Override
    public GamePlayer getNextPlayer(GamePlayer currentPlayer, GameState gameState) {
        return getGameEngine().getNextPlayer(currentPlayer, ConstantValue.EXCLUDE_DROPOUT);
    }
    
    @Override
    public boolean isTurnFinished(GameState gameState) {
        int numberPlayers = getGameEngine().getNumberPlayers(ConstantValue.EXCLUDE_DROPOUT);
        return getGameEngine().getCurrentMove() >= numberPlayers;
    }

}
