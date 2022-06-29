package com.tjger.lib;

/**
 * This exception is thrown, if the game configuration file has errors. 
 * 
 * @author hagru
 */
public class GameConfigurationException extends RuntimeException {
	
	private static final long serialVersionUID = 854371078L;
	
    public GameConfigurationException() {
        super("The game's configuration file has errors!");
    }

}
