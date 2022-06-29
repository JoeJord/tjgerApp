package com.tjger.game.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tjger.game.GamePlayer;
import com.tjger.game.HumanPlayer;
import com.tjger.game.NetworkPlayer;
import com.tjger.game.completed.PlayerType;
import com.tjger.lib.ArrayUtil;

import android.graphics.Bitmap;
import at.hagru.hgbase.lib.internal.ClassFactory;

/**
 * The factory for creating new players has to be implemented.<br>
 * <b>Do not inherit from this class.</b>
 *
 * @author hagru
 */
public final class PlayerFactory {

    private static final PlayerFactory FACTORY = new PlayerFactory();
    
    final private Map<String,String> mapPlayerTypes = new HashMap<>();
    final private List<PlayerType> supportTypes = new ArrayList<>(); // all player types
    final private List<PlayerType> computerTypes = new ArrayList<>();
    private String defaultComputerType;
    final private List<PlayerType> networkTypes = new ArrayList<>();
    final private List<PlayerType> humanTypes = new ArrayList<>();

    private PlayerFactory() {
        super();
    }

    /**
     * @return The one and only player factory.
     */
    public static PlayerFactory getInstance() {
        return FACTORY;
    }

    /**
     * @param playerType New player type to add.
     * @param classPath The full name of the class that implements this player.
     * @param typeImage The image of the player type.
     * @return True, if player was added.
     */
    public boolean addPlayerType(String playerType, String classPath, Bitmap typeImage) {
        Object player = createPlayer(classPath, playerType, "", "");
        if (player != null) {
            mapPlayerTypes.put(playerType, classPath);
            PlayerType ptNew = createPlayerType(playerType, typeImage);
            supportTypes.add(ptNew);
            if (player instanceof HumanPlayer) {
                humanTypes.add(ptNew);
            } else if (player instanceof NetworkPlayer) {
                networkTypes.add(ptNew);
            } else {
            	computerTypes.add(ptNew);
            }
            return true;
        }
        return false;
    }

    /**
     * Creates a new player type, trying to get the default image.
     *
     * @param playerType The id of the new player type.
     * @param typeImage The image of the player type.
     * @return A new PlayerType object.
     */
    private PlayerType createPlayerType(String playerType, Bitmap typeImage) {
        return new PlayerType(playerType, typeImage);
    }

    /**
     * @param playerType The id of a player type.
     * @param playerName The name of the new player.
     * @param pieceColor The piece color for this player.
     * @return A new player object from the given type.
     */
    public GamePlayer createPlayer(String playerType, String playerName, String pieceColor) {
        Object classPath = mapPlayerTypes.get(playerType);
        if (classPath!=null) {
            // create the new player
            return createPlayer(classPath.toString(), playerType, playerName, pieceColor);
        }
        return null;
    }

    /**
     * @param classPath The full path for the class file.
     * @param playerType The id of a player type.
     * @param playerName The name of the new player.
     * @param pieceColor The piece color for this player.
     * @return A new player object from the given type.
     */
    @SuppressWarnings("rawtypes")
	private GamePlayer createPlayer(String classPath, String playerType, String playerName, String pieceColor) {
        Class[] classes = new Class[] { String.class, String.class, String.class };
        Object[] params = new Object[] { playerType, playerName, pieceColor } ;
        return ClassFactory.createClass(classPath, GamePlayer.class, "player", classes, params);
    }

    /**
     * @param playerType The id of a player type.
     * @return The PlayerType object or null if this type does not exist.
     */
    public PlayerType getPlayerType(String playerType) {
        PlayerType[] pt = getSupportedPlayerTypes();
        for (int i=0; i<pt.length; i++) {
            if (pt[i].getId().equals(playerType)) {
                return pt[i];
            }
        }
        return null;
    }

    /**
     * @param playerType The id of a player type.
     * @return True if this is a network player type.
     */
    public boolean isNetworkType(String playerType) {
        PlayerType pt = getPlayerType(playerType);
        if (pt!=null) {
            return pt.isNetwork();
        } else {
            return false;
        }
    }

    /**
     * @param playerType The id of a player type.
     * @return True if this is a human player type.
     */
    public boolean isHumanType(String playerType) {
        PlayerType pt = getPlayerType(playerType);
        if (pt!=null) {
            return pt.isHuman();
        } else {
            return false;
        }
    }
    
    /**
     * @param playerType the id of the player type
     * @return true if it is a computer type, i.e., neither human nor network
     */
    public boolean isComputerType(String playerType) {
    	return !isHumanType(playerType) && !isNetworkType(playerType);
    }

    /**
     * @return A list with the names of all player types that can be created.
     */
    public PlayerType[] getSupportedPlayerTypes() {
        return ArrayUtil.toPlayerType(supportTypes);
    }

    /**
     * @return A list with the names of all player types that are human players.
     */
    public PlayerType[] getHumanPlayerTypes() {
        return ArrayUtil.toPlayerType(humanTypes);
    }

    /**
     * @return A list with the names of all player types that are network clients.
     */
    public PlayerType[] getNetworkPlayerTypes() {
        return ArrayUtil.toPlayerType(networkTypes);
    }

    /**
     * @return A list with the names of all player types that are computer players.
     */
    public PlayerType[] getComputerPlayerTypes() {
        return ArrayUtil.toPlayerType(computerTypes);
    }

    /**
     * Sets the default computer player type.
     *
     * @param type The type to use as default for computer players.
     */
    public void setDefaultComputerType(String type) {
        defaultComputerType = type;
    }

    /**
     * Returns the default computer player type.
     *
     * @return The default computer player type.
     */
    public PlayerType getDefaultComputerType() {
	return (isComputerType(defaultComputerType)) ? getPlayerType(defaultComputerType): null; 
    }
}
