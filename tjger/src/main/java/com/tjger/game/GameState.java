package com.tjger.game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.completed.GameEngine;

/**
 * Holds the game's state. 
 * You have to implement this class and must define it in the game's configuration file. 
 * The class implementing the GameState must not be a GameStateListener! 
 * 
 * @author hagru
 */
public interface GameState extends Cloneable {

    /**
     * Resets the current game state. Is called when a new game is started.
     * @param engine The game engine.
     */
    public void resetGame(GameEngine engine);
    
    /**
     * Resets all information about the current round. Is called when a new round is started.
     * @param engine The game engine.
     */
    public void resetRound(GameEngine engine);
    
    /**
     * Resets all information about the current turn. Is called when a new turn is started.
     * @param engine The game engine.
     */
    public void resetTurn(GameEngine engine);
    
    /**
     * Indicates that the game was stopped irregular (by the user).
     * Resets all values.
     */
    public void stopGame();
    
    /**
     * @param player The player that does the given move.
     * @param move The move that changes the game's state.
     * @param engine The game engine.
     */
    public void changeState(GamePlayer player, MoveInformation move, GameEngine engine);
    
    /**
     * Undos the given (last) move. Is the opposite method of changeState and only necessary 
     * for artificial intelligence methods.
     * 
     * @see tjger.lib.AiAlgorithms 
     * @param player The player that did the move.
     * @param move The move.
     */
    public void undoMove(GamePlayer player, MoveInformation move);
    
    /**
     * Clones the game state. Is only necessaray if methodes for artificial intelligence are used.
     * 
     * @see tjger.lib.AiAlgorithms
     * @return A clone of the game state.
     */
    public Object clone();
    
    /**
     * Save the game state.
     * 
     * @param doc The document object.
     * @param root The node to add the game state information.
     * @return 0 if saving was successful.
     */
    public int save(Document doc, Element root);
    
    /**
     * Load the game sate.
     * 
     * @param node Node to get the game state information from.
     * @return 0 if loading was successful.
     */
    public int load(Node node);
    
    /**
     * Converts the current game state into a message part for the network transformation.
     * 
     * @return The message part representing the game state.
     */
    public String toNetworkString();
    
    /**
     * Converts a message part to the game state.
     * 
     * @param data The message part representing the game state.
     * @return True if converting was successful.
     */
    public boolean fromNetworkString(String data);


    /**
     * Converts the given move information into a message part for the network transformation.
     * 
     * @param move A move information.
     * @return The message part representing the move information.
     */
    public String toNetworkStringMove(MoveInformation move);
    
    /**
     * Converts a message part to the move information.
     * 
     * @param data The message part representing the move information.
     * @return A move information of null if an error occurred.
     */
    public MoveInformation fromNetworkStringMove(String data);    

}
