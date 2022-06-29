package com.tjger.game;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.lib.ScoreUtil;

/**
 * Manages the game's rules. Including valid move, ending of a game and scoring.
 * You have to inherit from this class and must define it in the game's configuration file.
 *
 * @author hagru
 */
abstract public class GameRules {
    
    /**
     * @return The game manager.
     */
    protected GameManager getGameManager() {
        return GameManager.getInstance();
    }
    
    /**
     * @return The game config object.
     */
    protected GameConfig getGameConfig() {
        return GameManager.getInstance().getGameConfig();
    }
    
    /**
     * @return The game engine object.
     */
    protected GameEngine getGameEngine() {
        return GameManager.getInstance().getGameEngine();
    }
    
    /**
     * Returns the start player of the game and is not called by default.<p> 
     * This method is intended to be used by {@link #getStartPlayer(GameState)} to find out the start player 
     * of the first round. The default implementation returns the player with index 0 for the first game and 
     * the next player of the player that started the last round of the game before.
     * 
     * @param gameState the game state
     * @return the first player of the game, must not be null.
     */
    public GamePlayer getGameStartPlayer(GameState gameState) {
        return getGameEngine().getCyclingFirstGamePlayer();
    }
    
    /**
     * @param gameState The game state.
     * @return The start player for the current round, must not be null.
     */
    abstract public GamePlayer getStartPlayer(GameState gameState);
    
    /**
     * @param currentPlayer The current player.
     * @param gameState The game state.
     * @return The player whose turn is next.
     */
    abstract public GamePlayer getNextPlayer(GamePlayer currentPlayer, GameState gameState);
    
    /**
     * @param gameState The game state.
     * @return True if a turn has just finished.
     */
    abstract public boolean isTurnFinished(GameState gameState);
   
    /**
     * @param gameState The game state.
     * @return True if a round has just finished.
     */
    abstract public boolean isRoundFinished(GameState gameState);
    
    /**
     * @param gameState The game state.
     * @return True if a game has just finished.
     */
    abstract public boolean isGameFinished(GameState gameState);
    
    /**
     * Changes the players' score after every move.
     *
     * @param playerToScore A list with the players to score.
     * @param gameState The game state.
     * @see tjger.game.GamePlayer#addScore
     */
    abstract public void doScoring(GamePlayer[] playerToScore, GameState gameState);
    
    /**
     * Does the ranking of the players for the current turn.
     * The order of the ranks is like the order of the players.
     * So the player with the index 0 gets the rank of int[0].
     * Ranking starts with 1.
     * 
     * @param playerToRank A list with the players to rank.
     * @return An array with the players' rankings.
     * @see tjger.lib.ScoreUtil
     */
    public int[] getTurnRanking(GamePlayer[] playerToRank) {
        return ScoreUtil.getScoreRanking(playerToRank, GamePlayer.SCORE_TURN);
    }
    
    /**
     * Does the ranking of the players for the current round.
     * The order of the ranks is like the order of the players.
     * So the player with the index 0 gets the rank of int[0].
     * Ranking starts with 1.
     * 
     * @param playerToRank A list with the players to rank.
     * @return An array with the players' rankings.
     * @see tjger.lib.ScoreUtil
     */
    public int[] getRoundRanking(GamePlayer[] playerToRank) {
        return ScoreUtil.getScoreRanking(playerToRank, GamePlayer.SCORE_ROUND);
    }
    
    /**
     * Does the ranking of the players for the game.
     * The order of the ranks is like the order of the players.
     * So the player with the index 0 gets the rank of int[0].
     * Ranking starts with 1.
     * 
     * @param playerToRank A list with the players to rank.
     * @return An array with the players' rankings.
     * @see tjger.lib.ScoreUtil
     */
    public int[] getGameRanking(GamePlayer[] playerToRank) {
        return ScoreUtil.getScoreRanking(playerToRank, GamePlayer.SCORE_GAME);
    }
    
    /**
     * Returns for a given move, is it is valid.
     * This method is not used in tjger but useful to implement for games.
     * 
     * @param move The move to test.
     * @param gameState The game state.
     * @return True if the move is valid.
     */
    abstract public boolean isValidMove(MoveInformation move, GameState gameState);

}
