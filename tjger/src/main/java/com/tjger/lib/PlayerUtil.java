package com.tjger.lib;

import java.util.ArrayList;
import java.util.List;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.PlayerManager;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Some help methods for managing players.
 *
 * @author hagru
 */
public class PlayerUtil {

	private PlayerUtil() {
		super();
	}

    /**
     * Returns for a given number of players the indices of the human players.
     *
     * @param players A list with players.
     * @return The indices of the human players.
     */
    public static int[] getHumanPlayerIndices(GamePlayer[] players) {
        List<Integer> listHuman = new ArrayList<>();
        if (players != null) {
	        for (int i=0; i<players.length; i++) {
	            if (players[i].isHuman()) {
	            	listHuman.add(Integer.valueOf(i));
	            }
	        }
        }
        return HGBaseTools.toIntArray(listHuman);
    }

    /**
     * The main player is the first human player of if there is no human player
     * then it's the first player. If it's a network game and there is just the
     * client playing the client's index will be returned.
     *
     * @param engine The game engine.
     * @return The index of the main player.
     */
    public static int getIndexOfMainPlayer(GameEngine engine) {
        // take the human player if there is one
        int[] indices = getHumanPlayerIndices(engine.getActivePlayers());
        // there should only be one index or none if there is no human
        return (indices.length>0)? indices[0] : 0;
    }
    
    /**
     * Returns the main player<br>
     * The main player is determined with {@link #getIndexOfMainPlayer(GameEngine)}.
     * 
     * @param engine The game engine.
     * @return The main player.
     */
    public static GamePlayer getMainPlayer(GameEngine engine) {
	return engine.getActivePlayers()[getIndexOfMainPlayer(engine)];
    }

    /**
     * Returns from a list of players all players that are dropped out.
     * @param players A list of players.
     * @return A list with players that are dropped out.
     */
    public static GamePlayer[] getDropOutPlayers(GamePlayer[] players) {
        return getPlayersByDropOutState(players, false);
    }

    /**
     * Returns from a list of players all players that are dropped out.
     * @param players A list of players.
     * @return A list with players that are dropped out.
     */
    public static GamePlayer[] getAdvancedPlayers(GamePlayer[] players) {
        return getPlayersByDropOutState(players, true);
    }

    /**
     * @param players An array with players.
     * @param isAdvanced True if player shall not be dropped out, false otherwise.
     * @return The dropped out or advanced players.
     */
    private static GamePlayer[] getPlayersByDropOutState(GamePlayer[] players, boolean isAdvanced) {
        List<GamePlayer> list = new ArrayList<>();
        if (players!=null) {
	        for (GamePlayer player : players) {
	            if (player.isDropOut() != isAdvanced) {
	                list.add(player);
	            }
	        }
        }
        return ArrayUtil.toGamePlayer(list);
    }

    /**
     * @param player A player, must not be null.
     * @return The name of the player and it's type in brackets.
     */
    public static String getPlayerNameType(GamePlayer player) {
        return player.getName()+" ("+player.getType().toString()+")";
    }

    /**
     * @param engine The game engine.
     * @return True if there is just a human playing, otherwise false.
     */
    public static boolean isHumanPlaying(GameEngine engine) {
        if (engine.isActiveRound() && engine.isActiveGame()) {
	        GamePlayer player = engine.getCurrentPlayer();
	        return (player!=null && player.isHuman());
        }
        return false;
    }
    
    /**
     * Returns if the the "you"-message for the given player shall be displayed.
     * 
     * @param engine the game engine
     * @param player the player to check
     * @return true if the "you"-message shall be displayed
     */
    public static boolean  showYouMessageForPlayer(GameEngine engine, GamePlayer player) {
        GamePlayer[] players = engine.getActivePlayers();
        return (player.isHuman() && PlayerUtil.getHumanPlayerIndices(players).length == 1 
        					     && player.getName().equals(PlayerManager.getInstance().getDefaultHumanPlayerName()));
    }
    
    /**
     * Returns the default piece for a player.
     * 
     * @param player the player for get the piece for, must not be null
     * @return the default piece for a player, may be null
     */
    public static Part getPlayerImage(GamePlayer player) {
    	return getPlayerImage(player, "");
    }
    
    /**
     * Returns the default piece for a player.
     * 
     * @param player the player for get the piece for, must not be null
     * @param setName the name of the piece/card set
     * @return the default piece for a player, may be null
     */
    public static Part getPlayerImage(GamePlayer player, String setName) {
    	String color =  player.getPieceColor();
        PartSet partSet = (HGBaseTools.hasContent(setName)) ? GameManager.getInstance().getGameConfig().getActivePartSet(setName)
        													: GameManager.getInstance().getGameConfig().getActivePieceSet();
        if (partSet == null) {
        	partSet = (HGBaseTools.hasContent(setName)) ?  GameManager.getInstance().getGameConfig().getActiveCardSet(setName)
        												: GameManager.getInstance().getGameConfig().getActiveCardSet();
        }
        if (partSet != null) {
	        int startSequence = partSet.getStartSequence(color);
	        return partSet.getPart(color, startSequence);
        } else {
        	return null;
        }
    }

    /**
     * Returns an array with the active players starting with the main player.<br>
     * The main player is determined with {@link #getIndexOfMainPlayer(GameEngine)}.
     * 
     * @param engine The game engine.
     * @return An array with the active players starting with the main player.
     */
    public static GamePlayer[] getActivePlayerStartingMainPlayer(GameEngine engine) {
	GamePlayer[] players = engine.getActivePlayers();
	int mainIndex = getIndexOfMainPlayer(engine);
	if (mainIndex == 0) {
	    return players;
	} else {
	    GamePlayer[] sortedPlayers = new GamePlayer[players.length];
	    int lengthAfterMain = players.length - mainIndex;
	    System.arraycopy(players, mainIndex, sortedPlayers, 0, lengthAfterMain);
	    System.arraycopy(players, 0, sortedPlayers, lengthAfterMain, mainIndex);
	    return sortedPlayers;
	}
    }
}
