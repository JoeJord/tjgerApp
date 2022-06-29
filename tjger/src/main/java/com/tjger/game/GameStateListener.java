package com.tjger.game;

import com.tjger.game.completed.GameEngine;

/**
 * For listening all changes of the game state. 
 * The method <code>gameStateChanged</code> is invoked by the GameEngine before and after every move. 
 * Add listeners to the GameEngine with <code>addGameStateListener</code>.
 * 
 * @author hagru
 */
public interface GameStateListener {
    
    /**
     * A new game was started, and the game state is initialized.
     * 
     * @param state The game state.
     * @param engine The game engine.
     */
    public void newGameStarted(GameState state, GameEngine engine);

    /**
     * A new round started.
     * 
     * @param state The game state.
     * @param engine The game engine.
     */
    public void newRoundStarted(GameState state, GameEngine engine);

    /**
     * A new turn started.
     * 
     * @param state The game state.
     * @param engine The game engine.
     */
    public void newTurnStarted(GameState state, GameEngine engine);
    
    /**
     * The game was finished. Neither it' ready regularly or it was stopped.
     * 
     * @param normal True if the game finished because it's ready, false if it was stopped.
     */
    public void gameFinished(boolean normal);
    
    /**
     * This method is called, before a player starts his move.
     * 
     * @param state The game state.
     * @param engine The game engine.
     */
    public void gameStateBeforeMove(GameState state, GameEngine engine);
    
    /**
     * This method is called, after a player finished his move.
     * 
     * @param state The game state.
     * @param engine The game engine.
     */
    public void gameStateAfterMove(GameState state, GameEngine engine);

}
