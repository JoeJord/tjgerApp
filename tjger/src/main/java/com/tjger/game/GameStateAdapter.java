package com.tjger.game;

import com.tjger.game.completed.GameEngine;

/**
 * An adapter class that has empty implementations for all methods of the {@link GameStateListener}.
 *
 * @author hagru
 */
public abstract class GameStateAdapter implements GameStateListener {

    protected GameStateAdapter() {
        super();
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newGameStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newGameStarted(GameState state, GameEngine engine) {
        // NOCHECK: empty implementation
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newRoundStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newRoundStarted(GameState state, GameEngine engine) {
        // NOCHECK: empty implementation
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newTurnStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newTurnStarted(GameState state, GameEngine engine) {
        // NOCHECK: empty implementation
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameFinished(boolean)
     */
    @Override
    public void gameFinished(boolean normal) {
        // NOCHECK: empty implementation
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameStateBeforeMove(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void gameStateBeforeMove(GameState state, GameEngine engine) {
        // NOCHECK: empty implementation
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameStateAfterMove(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void gameStateAfterMove(GameState state, GameEngine engine) {
        // NOCHECK: empty implementation
    }

}
