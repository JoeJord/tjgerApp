package com.tjger.game.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.PlayerManager;
import com.tjger.game.completed.PlayerType;
import com.tjger.lib.XmlUtil;

import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Manages the players' profiles. A player is identified by name and type.
 * The profile just saves the images and the piece colors for the players.
 * Scores and played rounds are managed by GameStatistics, but when creating a
 * new profile, this values will be initialized automatically.
 *
 * @author hagru
 */
public class PlayerProfiles {

    private static final PlayerProfiles PROFILES_INSTANCE = new PlayerProfiles();
    private static final String PROFILES_FILE_NAME = "player.profiles.xml";

    private final Map<String,GamePlayer> profileMap = new HashMap<>();
    private boolean rememberProfiles;

    private PlayerProfiles() {
        super();
        loadProfiles();
    }

    /**
     * @return The one and only profile instance.
     */
    public static PlayerProfiles getInstance() {
        return PROFILES_INSTANCE;
    }

    /**
     * Saves the players' profiles into the file.
     */
    public void saveProfiles() {
        if (rememberProfiles) {
            File fileName = HGBaseFileTools.getFileForIntern(PROFILES_FILE_NAME);
            Document newDoc = HGBaseXMLTools.createDocument();
            Element root = newDoc.createElement("profile");
            // save the players
            Element playersRoot = newDoc.createElement("players");
            GamePlayer[] players = getPlayers();
            for (int i = 0; i < players.length; i++) {
                if (!players[i].getType().isNetwork()) {
                    Element pE = XmlUtil.savePlayer(newDoc, "player", players[i]);
                    playersRoot.appendChild(pE);
                }
            }
            root.appendChild(playersRoot);
            newDoc.appendChild(root);
            // save the file
            if (!HGBaseXMLTools.writeXML(newDoc, fileName)) {
                HGBaseLog.logWarn("Could not write profile file.");
            }
        }
    }

    /**
     * Loads the players' profiles from the file.
     */
    private void loadProfiles() {
    	File fileName = HGBaseFileTools.getFileForIntern(PROFILES_FILE_NAME);
        rememberProfiles = (fileName != null && fileName.exists());
        if (rememberProfiles) {
            Element root = HGBaseXMLTools.readXML(fileName, "");
            if (root != null) {
                ChildNodeIterator.run(new ChildNodeIterator(root, "profile", this) {

                    @Override
                    public void performNode(Node node, int index, Object obj) {
                        // load the players
                        if (node.getNodeName().equals("players")) {
                            ChildNodeIterator.run(new ChildNodeIterator(node, "players", obj) {
                                @Override
                                public void performNode(Node node, int index, Object obj) {
                                    GamePlayer player = XmlUtil.loadPlayer(node);
                                    if (player != null) {
                                        profileMap.put(new PlayerKey(player).toString(), player);
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                HGBaseLog.logWarn("Could not read profile file.");
            }
        }
    }

    /**
     * @param player A game player.
     * @return True, if there exists a profile for such a player.
     */
    public boolean existsPlayer(GamePlayer player) {
        return existsPlayer(new PlayerKey(player));
    }

    /**
     * @param key A player key.
     * @return True, if there exists a profile for such a player.
     */
    public boolean existsPlayer(PlayerKey key) {
        // network profiles shall be ignored
        PlayerType pt = PlayerFactory.getInstance().getPlayerType(key.getPlayerType());
        if (pt != null && pt.isNetwork()) {
            return false;
        } else {
            return profileMap.containsKey(key.toString());
        }
    }

    /**
     * Creates the profile for the given player. If a profile for such a player (name, type) already exists,
     * the profile is overwritten.
     *
     * @param player The player to create the profile for.
     */
    public void createProfile(GamePlayer player) {
        if (rememberProfiles) {
            // remove the score and played games information
            removePlayerStatistics(player);
            // save the profile and create a new player for that
            putPlayerIntoMap(player);
        }
    }

    /**
     * @param player The player whose statistics (game, score) shall be removed.
     */
    private void removePlayerStatistics(GamePlayer player) {
        GameStatistics statistics = GameStatistics.getInstance();
        statistics.removeGamesPlayedWon(player);
        statistics.removeGameScore(player);
    }

    /**
     * Puts a cloned player into tha map.
     *
     * @param player The player to put into the map.
     */
    private void putPlayerIntoMap(GamePlayer player) {
    	PlayerFactory pf = PlayerFactory.getInstance();
        GamePlayer newPlayer = pf.createPlayer(player.getType().getId(), player.getName(), player.getPieceColor());
        profileMap.put(new PlayerKey(newPlayer).toString(), newPlayer);
    }

    /**
     * Updates a player profile. If the profile does not exist, nothing happens.
     *
     * @param player The player which shall update the existing profile.
     */
    public void updateProfile(GamePlayer player) {
        if (existsPlayer(player)) {
            putPlayerIntoMap(player);
        }
    }

    /**
     * @param player The player whose profile shall be removed.
     */
    public void removeProfile(GamePlayer player) {
        removePlayerStatistics(player);
        profileMap.remove(new PlayerKey(player).toString());
    }

    /**
     * Returns the players profiles. If profiles are not remembered, the players of the player manager are
     * returned.
     *
     * @return The players that exists in the profiles in alphabetic order.
     */
    public GamePlayer[] getPlayers() {
        GamePlayer[] players = null;
        if (rememberProfiles) {
            players = profileMap.values().toArray(new GamePlayer[0]);
        } else {
            GameEngine engine = GameManager.getInstance().getGameEngine();
            if (engine.isActiveGame()) {
                players = engine.getActivePlayers();
            } else {
                players = PlayerManager.getInstance().getPlayers();
            }
        }
        HGBaseTools.orderList(players);
        return players;
    }

    /**
     * @param key The key for a player profile (name, type).
     * @return A player for this profile or null.
     */
    public GamePlayer getPlayer(PlayerKey key) {
        return profileMap.get(key.toString());
    }

    /**
     * @return True if profiles are remembered.
     */
    public boolean isRememberProfiles() {
        return rememberProfiles;
    }
}
