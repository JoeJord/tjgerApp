package com.tjger.gui;

import com.tjger.MainFrame;
import com.tjger.MainStatusBar;
import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;
import com.tjger.game.GameStateListener;
import com.tjger.game.completed.GameEngine;
import com.tjger.gui.completed.Part;
import com.tjger.lib.PlayerUtil;

import at.hagru.hgbase.lib.HGBaseText;

/**
 * The status bar.
 * 
 * @author hagru
 */
public class SimpleStatusBar extends MainStatusBar implements GameStateListener {
    
    protected GameEngine engine;

    public SimpleStatusBar(int[] panelWidth, MainFrame frame) {
        super(frame, panelWidth);
        engine = frame.getGameManager().getGameEngine();
        engine.addGameStateListener(this);
    }
    
    @Override
    public void actualizeText() {
        setText(getDefaultGameStateText());
    }
    
    /**
     * @return A default text for the game state, either a player name or 'status.roundfinished'/'status.gamefinished'.
     */
    protected String getDefaultGameStateText() {
        if (engine.isActiveGame()) {
            if (engine.isActiveRound()) {
                GamePlayer p = engine.getCurrentPlayer();
                if (p!=null) {
                    return p.getName();
                } else {
                    return "";
                }
            } else {
                return HGBaseText.getText("status_roundfinished");
            }
        } else {
            if (engine.isStoppedGame()) {
                return "";
            } else {
                return HGBaseText.getText("status_gamefinished");
            }
        }
    }

    /**
     * Shows the (first) piece of the active player on the status bar (only on active rounds).<p>
     * Intended to be called by {@link #actualizeText()}.
     */
    protected void showActivePlayerIcon() {
    	showActivePlayerIcon(0);
    }

   /**
    * Shows the (first) piece of the active player on the status bar (only on active rounds).<p>
    * Intended to be called by {@link #actualizeText()}.
    * 
    * @param index index of the view to show the icon
    */
   protected void showActivePlayerIcon(int index) {
       if (engine.isActiveGame() && engine.isActiveRound()) {
           GamePlayer p = engine.getCurrentPlayer();
           if (p != null) {
               Part part = PlayerUtil.getPlayerImage(p, getPlayerPartSet());
               if (part != null) {
                   setImage(index, part.getImage());
                   return;
               }
           }
       }
       setImage(index, null);
   }

    /**
	 * @return the part set for the player pieces, may be an empty string
	 */
	protected String getPlayerPartSet() {
		return "";
	}

	@Override
    public void newGameStarted(GameState state, GameEngine engine) { }
    @Override
    public void newRoundStarted(GameState state, GameEngine engine) {}
    @Override
    public void newTurnStarted(GameState state, GameEngine engine) {}
    @Override
    public void gameFinished(boolean normal) {
        actualizeText();
    }
    @Override
    public void gameStateBeforeMove(GameState state, GameEngine engine) {
        actualizeText();
    }
    @Override
    public void gameStateAfterMove(GameState state, GameEngine engine) {
        actualizeText();
    }
    
}
