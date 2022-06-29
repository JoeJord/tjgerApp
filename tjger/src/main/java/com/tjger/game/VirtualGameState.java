package com.tjger.game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.completed.GameEngine;

/**
 * A game state that implements all method except {@link #load(Node)} and {@link #save(Document, Element)}
 * with empty functionality.<p>
 * Intended to be used for application that use tjger but do not implement game functionality.
 * 
 * @author hagru
 */
public abstract class VirtualGameState implements GameState {
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public VirtualGameState clone() {
        try {
            return (VirtualGameState) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#resetGame(tjger.game.completed.GameEngine)
     */
    @Override
    public void resetGame(GameEngine engine) {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#resetRound(tjger.game.completed.GameEngine)
     */
    @Override
    public void resetRound(GameEngine engine) {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#resetTurn(tjger.game.completed.GameEngine)
     */
    @Override
    public void resetTurn(GameEngine engine) {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#stopGame()
     */
    @Override
    public void stopGame() {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#changeState(tjger.game.GamePlayer, tjger.game.MoveInformation, tjger.game.completed.GameEngine)
     */
    @Override
    public void changeState(GamePlayer player, MoveInformation move, GameEngine engine) {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#undoMove(tjger.game.GamePlayer, tjger.game.MoveInformation)
     */
    @Override
    public void undoMove(GamePlayer player, MoveInformation move) {
        // NOCHECK: not implemented
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#toNetworkString()
     */
    @Override
    public String toNetworkString() {
        // NOCHECK: not implemented
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#fromNetworkString(java.lang.String)
     */
    @Override
    public boolean fromNetworkString(String data) {
        // NOCHECK: not implemented
        return false;
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#toNetworkStringMove(tjger.game.MoveInformation)
     */
    @Override
    public String toNetworkStringMove(MoveInformation move) {
        // NOCHECK: not implemented
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.game.GameState#fromNetworkStringMove(java.lang.String)
     */
    @Override
    public MoveInformation fromNetworkStringMove(String data) {
        // NOCHECK: not implemented
        return null;
    }

}
