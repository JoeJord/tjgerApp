package com.tjger.lib;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;

/**
 * Interface for evaluating moves used for artificial intelligence.
 * 
 * @see AiAlgorithms
 * @author hagru
 */
public interface AiMoveEvaluator {
    
    /**
     * Generate a number of moves that can be done depending on the given game information.
     * 
     * @param player The player that shall d the moves.
     * @param state A clone of the game state.
     * @param rules The game rules.
     * @return An array with moves.
     */
    public AiMoveInformation[] generateMoves(GamePlayer player, GameState state, GameRules rules);
    
    /**
     * Evaluates a game state for the given player depending on the game information.
     * 
     * @param step The number of step the move is already calculated in the future (start with 1).
     * @param player The player to evaluate the state for.
     * @param move The move, with that the player starts to reach this state.
     * @param state A clone of the game state.
     * @param rules The game rules.
     * @return The evaluation value of the state.
     */
    public long evaluateState(int step, GamePlayer player, AiMoveInformation move, GameState state, GameRules rules);

}
