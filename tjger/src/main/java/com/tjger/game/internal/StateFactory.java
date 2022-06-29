package com.tjger.game.internal;

import com.tjger.game.GameState;

import at.hagru.hgbase.lib.internal.ClassFactory;

/**
 * The factory for creating the game's state object defined in the game's
 * configuration file.<br>
 * <b>Do not inherit from this class.</b>
 *
 * @author Harald
 */
public class StateFactory {

    private static StateFactory factory = new StateFactory();
    private String stateClass;

    private StateFactory() {
        super();
    }

    /**
     * @return The one and only instance of the game state factory.
     */
    public static StateFactory getInstance() {
        return factory;
    }

    /**
     * Sets the path to the game's state class.
     *
     * @param classPath The full name of the class that implements GameState.
     * @return True, if it's a valid state class.
     */
    public boolean setGameStateClass(String classPath) {
        if (ClassFactory.existsClass(classPath)) {
            this.stateClass = classPath;
            return true;
        } else {
        	return false;
        }
    }

    /**
     * Creates the game specific game state class.
     *
     * @return The new game state class.
     */
    public GameState createGameState() {
        if (stateClass != null) {
            return createGameState(stateClass);
        }
        return null;
    }

    /**
     * @param classPath The full name of the class that implements GameState.
     * @return The state object or null if there was an error.
     */
    private GameState createGameState(String classPath) {
        return ClassFactory.createClass(classPath, GameState.class, "game state");
    }
}
