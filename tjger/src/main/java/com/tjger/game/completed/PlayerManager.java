package com.tjger.game.completed;

import com.tjger.R;
import com.tjger.game.GamePlayer;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Manages all possible players.
 * Remark: This are not only the active players of a game.
 *
 * @author hagru
 */
final public class PlayerManager {

    public static final int HUMAN_PLAYER_INDEX = 0;

    private static final PlayerManager MANAGER = new PlayerManager();

    private final GameConfig gameConfig;
    private GamePlayer[] playerList;

    private PlayerManager() {
        super();
        gameConfig = GameConfig.getInstance();
        initPlayers();
    }

    /**
     * @return The one and only player manager instance.
     */
    public static PlayerManager getInstance() {
        return MANAGER;
    }

    /**
     * Initialize the player list.
     */
    public void initPlayers() {
        playerList = new GamePlayer[getMaxPlayers()];
        for (int i = 0; i < playerList.length; i++) {
            String playerType = getPlayerType(i);
            String playerName = getPlayerName(i);
            String pieceColor = getPieceColor(i);
            // create the player
            playerList[i] = PlayerFactory.getInstance().createPlayer(playerType, playerName, pieceColor);
        }
    }

    /**
     * @param i index of the player
     * @return the piece color for the player with the given index
     */
    public String getPieceColor(int i) {
        String pieceConfigKey = (i == HUMAN_PLAYER_INDEX) ? ConstantValue.CONFIG_PLAYERPIECECOLOR : ConstantValue.CONFIG_PLAYERPIECECOLOR + i;
        String pieceColor = HGBaseConfig.get(pieceConfigKey);
        if (!HGBaseTools.hasContent(pieceColor) && HGBaseTools.hasContent(gameConfig.getPlayerPieceColor())) {
            // as the piece color of the other players are not set in the new games dialog,
            // check here if human piece color was set
            String humanColor = HGBaseConfig.get(ConstantValue.CONFIG_PLAYERPIECECOLOR);
            if (HGBaseTools.hasContent(humanColor)) {
                int humanIndex = HGBaseTools.toInt(humanColor.replaceFirst(gameConfig.getPlayerPieceColor(), ""));
                if (HGBaseTools.isValid(humanIndex)) {
                    int maxPlayers = gameConfig.getMaxPlayers();
                    pieceColor = gameConfig.getPlayerPieceColor() + (humanIndex + HUMAN_PLAYER_INDEX + i) % maxPlayers;
                }
            }
            if (!HGBaseTools.hasContent(pieceColor)) {
                pieceColor = gameConfig.getPlayerPieceColor() + i;
            }
        }
        return pieceColor;
    }

    /**
     * @param i Index of the player.
     * @return The player name from the configuration or the default name.
     */
    public String getPlayerName(int i) {
        String playerName = GameConfig.getPlayerName(i);
        boolean isOnlyOneHuman = isOnlyOneHumanPlayer();
        if (!HGBaseTools.hasContent(playerName) || (i == HUMAN_PLAYER_INDEX && (playerName.equals(getDefaultHumanPlayerName()) || playerName.equals(getDefaultPlayerName(i))))) {
            return (i == HUMAN_PLAYER_INDEX && isOnlyOneHuman) ? getDefaultHumanPlayerName() : getDefaultPlayerName(i);
        } else {
            return playerName;
        }
    }

    /**
     * @return true if only one human player is playing
     */
    private boolean isOnlyOneHumanPlayer() {
        return (HGBaseConfig.getInt(ConstantValue.CONFIG_NUMHUMANS, 1) == 1);
    }

    /**
     * @param i the index of the player
     * @return the default name for the player with the given index
     */
    public String getDefaultPlayerName(int i) {
        return HGBaseText.getText(R.string.default_player_name) + " " + (i + 1);
    }

    /**
     * Returns the name for the human player, i.e. the first part of the user name that could by found or the defined
     * human_player_name if no other name is found.
     *
     * @return the name for the (first) human player
     */
    public String getDefaultHumanPlayerName() {
        return HGBaseText.getText(R.string.default_human_name);
        //String name = HGBaseAppTools.getUserName().split(" ")[0];
        //return (name.isEmpty()) ? getDefaultPlayerName(HUMAN_PLAYER_INDEX)
        //                        : HGBaseTools.capitalizeFirstLetter(name);
    }

    /**
     * @param index Index of the player.
     * @return The id of the player type.
     */
    private String getPlayerType(int index) {
        int numHumanPlayers = HGBaseConfig.getInt(ConstantValue.CONFIG_NUMHUMANS, 1);
        PlayerType[] humanTypes = PlayerFactory.getInstance().getHumanPlayerTypes();
        if (index < numHumanPlayers && humanTypes.length > 0) {
            return (index < humanTypes.length) ? humanTypes[index].getId() : humanTypes[humanTypes.length - 1].getId();
        } else {
            String computerType;
            if (HGBaseConfig.existsKey(ConstantValue.CONFIG_COMPUTERTYPE + index)) {
                computerType = HGBaseConfig.get(ConstantValue.CONFIG_COMPUTERTYPE + index);
            } else {
                computerType = HGBaseConfig.get(ConstantValue.CONFIG_COMPUTERTYPE);
            }
            if (HGBaseTools.hasContent(computerType) && PlayerFactory.getInstance().getPlayerType(computerType) != null) {
                return computerType;
            } else {
                computerType = getDefaultComputerPlayerType();
                if (HGBaseTools.hasContent(computerType)) {
                    return computerType;
                }
            }
        }
        // no suitable player type found for the given index, return just the first type
        PlayerType[] allTypes = PlayerFactory.getInstance().getSupportedPlayerTypes();
        return (allTypes.length > 0) ? allTypes[0].getId() : null;
    }

    /**
     * Returns the default computer player type.
     *
     * @return The default computer player type.
     */
    private String getDefaultComputerPlayerType() {
        PlayerFactory playerFactory = PlayerFactory.getInstance();
        PlayerType defaultComputerType = playerFactory.getDefaultComputerType();
        if (defaultComputerType != null) {
            return defaultComputerType.getId();
        }
        // Return the first available computer player if no default is specified.
        PlayerType[] computerTypes = playerFactory.getComputerPlayerTypes();
        if (computerTypes.length > 0) {
            return computerTypes[0].getId();
        }
        return null;
    }

    /**
     * @return The minimum number of players.
     */
    public int getMinPlayers() {
        return gameConfig.getMinPlayers();
    }

    /**
     * @return The maximum number of players.
     */
    public int getMaxPlayers() {
        return gameConfig.getMaxPlayers();
    }

    /**
     * @return An array with all players.
     */
    public GamePlayer[] getPlayers() {
        if (playerList == null)
            return null;
        return playerList.clone();
    }

    /**
     * @param index The player's index.
     * @return The player with the given index or null.
     */
    public GamePlayer getPlayer(int index) {
        if (index >= 0 && index < playerList.length) {
            return playerList[index];
        }
        return null;
    }


    /**
     * @param player A player.
     * @return The index of this player.
     */
    public int getIndexOfPlayer(GamePlayer player) {
        int index = -1;
        for (int i = 0; i < playerList.length && index == -1; i++) {
            if (playerList[i].equals(player))
                index = i;
        }
        return index;
    }

    /**
     * @param index  Index for the new player.
     * @param player Player to set at the specified index.
     */
    public void setPlayer(int index, GamePlayer player) {
        if (index >= 0 && index < playerList.length && player != null && getIndexOfPlayer(player) == -1) {
            playerList[index] = player;
        }
    }

    /**
     * Returns {@code true} if at least one selected player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     *
     * @return {@code true} if at least one selected player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    public boolean isProTeaserPlayerTypeSelected() {
        int numPlayers = gameConfig.getMaxPlayers();
        PlayerFactory playerFactory = PlayerFactory.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();
        for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
            PlayerType playerType = playerFactory.getPlayerType(playerManager.getPlayerType(playerIndex));
            if (PlayerType.isProTeaser(playerType)) {
                return true;
            }
        }
        return false;
    }
}
