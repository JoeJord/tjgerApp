package com.tjger.lib;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;

/**
 * An abstract class that holds common methods for player maps.
 * 
 * @author hagru
 */
public abstract class AbstractPlayerMap {
    
    protected AbstractPlayerMap() {
        super();
    }
    
    /**
     * Saves the map of the particular type for the player.
     * 
     * @param doc The document object.
     * @param root The root to store the new element.
     */
    public abstract void saveMap(Document doc, Element root);

    /**
     * @param doc The document object.
     * @param root The root to store the new element.
     * @param newNode The name of the new element that shall contain the player  map.
     * @return The new created element.
     */
    public Element saveMap(Document doc, Element root, String newNode) {
        Element mapNode = doc.createElement(newNode);
        saveMap(doc, mapNode);
        root.appendChild(mapNode);
        return mapNode;
    }
    
    /**
     * Resets all information about the players (for {@link #loadMap(Node)}).
     */
    public abstract void resetPlayers();
    
    /**
     * Loads the player's map from the given node.
     *
     * @param root The node containing the information for the player card map.
     */
    public void loadMap(Node root) {
        final GameEngine engine = GameManager.getInstance().getGameEngine();
        resetPlayers();
        // load the cards for the players
        ChildNodeIterator.run(new ChildNodeIterator (root, this) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                //GamePlayer
                if (node.getNodeName().startsWith("player")) {
                    loadNode(engine, node);
                }
            }
        });
    }

    /**
     * Loads the a single player node for the map data.
     * 
     * @param engine the game engine
     * @param node the node for a single player
     */
    protected abstract void loadNode(final GameEngine engine, Node node);
    
    /**
     * @return All players that are in this map.
     */
    public abstract GamePlayer[] getPlayers();
    
    /**
     * Converts the nap into a message part for the network transformation.
     *
     * @return The message part representing the map.
     */
    public String toNetworkString() {
        StringBuilder data = new StringBuilder();
        GameEngine engine = GameManager.getInstance().getGameEngine();
        GamePlayer[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            if (i>0) {
                data.append(ConstantValue.NETWORK_DIVIDEPART2);
            }
            int index = engine.getIndexOfPlayer(players[i]);
            data.append(index);
            data.append(ConstantValue.NETWORK_DIVIDEPAIR);
            appendPlayerData(data, players[i]);
        }
        if (data.length()==0) {
            data.append(ConstantValue.NETWORK_NULL);
        }
        return data.toString();
    }
    
    /**
     * Appends the player data for the network string. This must be a single data using the default
     * network characters (is already separated by {@link ConstantValue#NETWORK_DIVIDEPART2}.
     * 
     * @param data the string builder to append the data
     * @param player the player to append the information for
     * @see AbstractPlayerMap#readPlayerData(GamePlayer, String)
     */
    protected abstract void appendPlayerData(StringBuilder data, GamePlayer player);
    
    /**
     * Converts a message part to the map.
     *
     * @param data The message part representing the map.
     * @return True if converting was successful.
     */
    public boolean fromNetworkString(String data) {
        resetPlayers();
        if (data.equals(ConstantValue.NETWORK_NULL)) {
            return true;
        }
        GameEngine engine = GameManager.getInstance().getGameEngine();
        String[] datas = data.split(ConstantValue.NETWORK_DIVIDEPART2);
        for (int i=0; i<datas.length; i++) {
            String[] value = datas[i].split(ConstantValue.NETWORK_DIVIDEPAIR);
            if (value.length>2) {
                return false;
            }
            if (value.length==2) {
                GamePlayer p = engine.getPlayerWithIndex(HGBaseTools.toInt(value[0]));
                if (p == null || !readPlayerData(p, value[1])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reads a single player data from network stream.
     * 
     * @param player the player to read the data for
     * @param string the data to store into the map
     * @return true if everything was ok, false if the data was wrong
     * @see AbstractPlayerMap#appendPlayerData(StringBuilder, GamePlayer)
     */
    protected abstract boolean readPlayerData(GamePlayer player, String string);

    
}
