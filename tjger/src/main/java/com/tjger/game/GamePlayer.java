package com.tjger.game;

import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.PlayerType;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.game.internal.PlayerKey;

import at.hagru.hgbase.gui.ProgressState;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * An abstract player of the game.
 *
 * @author hagru
 */
abstract public class GamePlayer implements Comparable<GamePlayer>, Cloneable {

    final public static int SCORE_TURN = 1;
    final public static int SCORE_ROUND = 2;
    final public static int SCORE_GAME = 4;

    private String playerName;
    private String playerType;
    private String pieceColor;
    private boolean dropOut;
    private boolean playing;

    public GamePlayer(String playerType, String playerName, String pieceColor) {
        super();
        this.dropOut = false;
        this.playing = false;
        this.playerType = playerType;
        setName(playerName);
        setPieceColor(pieceColor);
    }

    /**
     * @return The game manager.
     */
    protected GameManager getGameManager() {
        return GameManager.getInstance();
    }

    /**
     * @return The game's rules.
     */
    protected GameRules getGameRules() {
        return getGameManager().getGameRules();
    }

    /**
     * @return The game's state.
     */
    protected GameState getGameState() {
        return getGameEngine().getGameState();
    }

    /**
     * @return The game engine.
     */
    protected GameEngine getGameEngine() {
        return getGameManager().getGameEngine();
    }

    /**
     * @return The name of the player.
     */
    public String getName() {
        return playerName;
    }
    /**
     * @param playerName The name for the player.
     */
    public void setName(String playerName) {
        this.playerName = (playerName==null)? "" : playerName;
    }
    /**
     * @return The piece color for this player.
     */
    public String getPieceColor() {
        if (pieceColor == null) {
            return "";
        } else {
            return pieceColor;
        }
    }
    /**
     * @param pieceColor The pieceColor to set.
     */
    public void setPieceColor(String pieceColor) {
        setPieceColor(pieceColor, false);
    }
    /**
     * @param pieceColor The pieceColor to set.
     * @param considerOthers true to consider other players and look that the same color is not available twice.
     */
    public void setPieceColor(String pieceColor, boolean considerOthers) {
        String oldColor = this.pieceColor;
        this.pieceColor = pieceColor;
        if (considerOthers && HGBaseTools.hasContent(oldColor)) {
            for (GamePlayer otherPlayer : GameManager.getInstance().getPlayerManager().getPlayers()) {
                if (!this.equals(otherPlayer) && this.pieceColor.equals(otherPlayer.getPieceColor())) {
                    otherPlayer.setPieceColor(oldColor);
                    break;
                }
            }
        }
    }

    /**
     * @return True if this player is drop out of the active game (but he is still an active player).
     */
    public boolean isDropOut() {
        return dropOut;
    }
    /**
     * @param dropOut True to drop this player out of the game (but he still is an active player).
     */
    public void setDropOut(boolean dropOut) {
        this.dropOut = dropOut;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }
    /**
     * @return True if this player is a human player.
     */
    public boolean isHuman() {
        return (this instanceof HumanPlayer);
    }

    /**
     * Start's the players move.
     */
    public void startMove() {
        this.playing=true;
        if (!isHuman()) {
            // set the progress panel to waiting if there exists one
            getGameManager().getMainFrame().setStatusProgress(ProgressState.STATE_WAITING);
            //getGameManager().getMainFrame().setCursorWait();
        }
        considerMove();
    }

    /**
     * Play method of the player, has to call <code>performMove</code>.
     * E.g. for computer players this is the method where the AI is implemented.
     * Human players do not need this method, because <code>performMove</code> is called by the GUI.
     */
    abstract public void considerMove();

    /**
     * Does the player's move. This method shall called by <code>decideMove</code>
     * or - if it's a human player - by the GUI.
     *
     * @param move The player's move.
     */
    public void performMove(MoveInformation move) {
        //getGameManager().getMainFrame().setCursorDefault();
        if (isPlaying()) {
            stopPlaying();
            getGameEngine().performMove(move);
        }
    }

    /**
     * Interrupts the player.
     */
    public void stopPlaying() {
        this.playing=false;
    }

    /**
     * Returns if the player is playing.
     *
     * @return True if player is playing.
     */
    public boolean isPlaying() {
        return (this.playing && getGameEngine().isActiveRound());
    }

    /**
     * @return The previous player at this game, can be null if player is not active.
     */
    public GamePlayer getPrevPlayer(boolean withoutDropOut) {
        return getGameEngine().getPrevPlayer(this, withoutDropOut);
    }

    /**
     * @see #getPrevPlayer(boolean)
     */
    public GamePlayer getPrevPlayer() {
        return getGameEngine().getPrevPlayer(this);
    }

    /**
     * @return The next player at this game, can be null if player is not active.
     */
    public GamePlayer getNextPlayer(boolean withoutDropOut) {
        return getGameEngine().getNextPlayer(this, withoutDropOut);
    }

    /**
     * @see #getNextPlayer(boolean)
     */
    public GamePlayer getNextPlayer() {
        return getGameEngine().getNextPlayer(this);
    }

    /**
     * @param scoreType The type of the returned score (SCORE_TURN, SCORE_ROUND, SCORE_GAME).
     * @return The score of this player.
     */
    public int getScore(int scoreType) {
        return getGameManager().getGameStatistics().getScore(this, scoreType);
    }

    /**
     * @param score The score to add.
     * @param scoreTypes The score types can be joined with the | operator (SCORE_TURN, SCORE_ROUND, SCORE_GAME).
     */
    public void addScore(int score, int scoreTypes) {
        getGameManager().getGameStatistics().addScore(this, score, scoreTypes);
    }

    /**
     * Use with care!
     * @param score The score to set.
     * @param scoreTypes The score types can be joined with the | operator (SCORE_TURN, SCORE_ROUND, SCORE_GAME).
     */
    public void setScore(int score, int scoreTypes) {
        getGameManager().getGameStatistics().setScore(this, score, scoreTypes);
    }

    /**
     * @return The number of games this player has already played.
     */
    public int getGamesPlayed() {
        return getGameManager().getGameStatistics().getGamesPlayed(this);
    }

    /**
     * @return The number of games this player has already won.
     */
    public int getGamesWon() {
        return getGameManager().getGameStatistics().getGamesWon(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(GamePlayer o2) {
        return new PlayerKey(this).compareTo(new PlayerKey(o2));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        // A player is equal if the player keys are equal
        if (o2 instanceof GamePlayer) {
            return new PlayerKey(this).equals(new PlayerKey((GamePlayer)o2));
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new PlayerKey(this).hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public GamePlayer clone() {
        try {
            return (GamePlayer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * @return The type name of this player.
     */
    public PlayerType getType() {
        return PlayerFactory.getInstance().getPlayerType(playerType);
    }
}
