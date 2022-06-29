package com.tjger.game.internal;

import com.tjger.game.GameRules;

import at.hagru.hgbase.lib.internal.ClassFactory;

/**
 * The factory for creating the rules object defined in the game's configuration
 * file.<br>
 * <b>Do not inherit from this class.</b>
 *
 * @author Harald
 */
public class RulesFactory {

    private static RulesFactory factory = new RulesFactory();
    private String rulesClass;

    private RulesFactory() {
        super();
    }

    /**
     * @return The one and only instance of the rules factory.
     */
    public static RulesFactory getInstance() {
        return factory;
    }

    /**
     * Sets the path to the game's rules class.
     *
     * @param classPath The full name of the class that implements GameRules.
     * @return True, if it's a valid rules class.
     */
    public boolean setRulesClass(String classPath) {
        if (ClassFactory.existsClass(classPath)) {
            this.rulesClass = classPath;
            return true;
        } else {
        	return false;
        }
    }

    /**
     * Creates the game specific rules class.
     *
     * @return The new rules class.
     */
    public GameRules createGameRules() {
        return (rulesClass != null)? createGameRules(rulesClass) : null;
    }

    /**
     * @param classPath The full name of the class that implements GameRules.
     * @return The rules object or null if there was an error.
     */
    private GameRules createGameRules(String classPath) {
        return ClassFactory.createClass(classPath, GameRules.class, "rules");
    }
}
