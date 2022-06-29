package com.tjger.gui.completed;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;
import com.tjger.game.GameStateListener;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;

import android.app.Activity;
import android.widget.LinearLayout;
import at.hagru.hgbase.android.awt.Color;

/**
 * Creates a panel that shows the information of the players of the current game.
 * For information to display see PlayerInfoPanel.
 *
 * @author hagru
 */
public class PlayerStatePanel extends LinearLayout implements GameStateListener {

    final static private int BACKGROUND_COLOR = Color.DARK_GRAY.getColorCode();
    final static private int BACKGROUND_COLOR2 = Color.GRAY.getColorCode();
    final static private int SELECTED_COLOR = new Color(255, 100, 0).getColorCode();

    final private GameManager manager;
    final private GameConfig config;
    private PlayerInfoPanel[] pnPlayer;
    private int fields;

    public PlayerStatePanel(Activity activity, int fields) {
        super(activity);
        this.fields = fields;
        this.manager = GameManager.getInstance();
        this.config = manager.getGameConfig();
        setOrientation(LinearLayout.VERTICAL);
        createRows(activity);
        manager.getGameEngine().addGameStateListener(this);
    }
    
    /**
     * @return the number of player panels
     */
    public int getPlayerPanelCount() {
    	return (pnPlayer == null) ? 0 : pnPlayer.length;
    }
    
    /**
     * @param index the index of the panel
     * @return the player info panel or null
     */
    public PlayerInfoPanel getPlayerPanel(int index) {
    	return (pnPlayer == null || index < 0 || index >= pnPlayer.length) ? null : pnPlayer[index];
    }
    
    /**
     * @return the calculated value of the panel height
     */
    public int getPanelHeight() {
    	return getPlayerPanelCount() * PlayerInfoPanel.PANEL_HEIGHT;
    }

    /**
     * Actualizes the panel.
     * @param engine The game engine.
     */
    protected void actualize(GameEngine engine) {
        int maxPlayers = config.getMaxPlayers();
        GamePlayer[] players = engine.getActivePlayers();
        for (int i=0; i<maxPlayers; i++) {
            if (players==null || i>=players.length) {
                resetInformation(i);
            } else {
                setPlayerInfo(i, players[i], engine);
            }
        }
    }

    /**
     * Create a row for every possible player.
     */
    protected void createRows(Activity activity) {
        int rows = config.getMaxPlayers();
        pnPlayer = new PlayerInfoPanel[rows];
        for (int i=0; i<rows; i++) {
            createRow(activity, i);
        }
    }

    /**
     * @param index Index of the row.
     */
    protected void createRow(Activity activity, int i) {
        pnPlayer[i] = new PlayerInfoPanel(activity, null, fields);
        setRowBackground(i, false);
        addView(pnPlayer[i]);
    }

    /**
     * @param i Index of the row where to set the background.
     * @param isCurrent true to indicate that this is the current player
     */
    protected void setRowBackground(final int i, final boolean isCurrent) {
        pnPlayer[i].post(new Runnable() {

            @Override
            public void run() {
                if (isCurrent) {
                    pnPlayer[i].setBackgroundColor(SELECTED_COLOR);
                } else if (i % 2 == 0) {
                    pnPlayer[i].setBackgroundColor(BACKGROUND_COLOR);
                } else {
                    pnPlayer[i].setBackgroundColor(BACKGROUND_COLOR2);
                }
                pnPlayer[i].postInvalidate();
            }
        });
    }

    /**
     * Resets all information on the panel.
     */
    protected void resetInformation() {
        for (int i=0; i<config.getMaxPlayers(); i++) {
            resetInformation(i);
        }
    }

    /**
     * Resets all information on the panel.
     */
    protected void resetBackgrounds() {
        for (int i=0; i<config.getMaxPlayers(); i++) {
            setRowBackground(i, false);
        }
    }

    /**
     * @param index Index of the row.
     * @param player The player who's info shell be set.
     * @param engine The game engine.
     */
    protected void setPlayerInfo(int index, GamePlayer player, GameEngine engine) {
        pnPlayer[index].setInformation(player);
        boolean isCurrent = (engine.isActiveRound() && player.equals(engine.getCurrentPlayer()));
        setRowBackground(index, isCurrent);
    }

    /**
     * @param i Index of the panel where the information shall be reseted.
     */
    protected void resetInformation(int i) {
        pnPlayer[i].resetInformation();
        setRowBackground(i, false);
    }

    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newGameStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newGameStarted(GameState state, GameEngine engine) {
        // NOCHECK: actualizing is done before move
    }
    
    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newRoundStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newRoundStarted(GameState state, GameEngine engine) {
        // NOCHECK: actualizing is done before move
    }
    
    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#newTurnStarted(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void newTurnStarted(GameState state, GameEngine engine) {
        // NOCHECK: actualizing is done before move
    }
    
    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameFinished(boolean)
     */
    @Override
    public void gameFinished(boolean normal) {
        if (normal) {
            resetBackgrounds();
        } else {
            resetInformation();
        }
    }
    
    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameStateBeforeMove(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void gameStateBeforeMove(GameState state, GameEngine engine) {
        actualize(engine);
    }
    
    /* (non-Javadoc)
     * @see tjger.game.GameStateListener#gameStateAfterMove(tjger.game.GameState, tjger.game.completed.GameEngine)
     */
    @Override
    public void gameStateAfterMove(GameState state, GameEngine engine) {
        actualize(engine);
    }

}
