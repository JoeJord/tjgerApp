package com.tjger.lib;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Stores for players a number value for each player.
 *
 * @author hagru
 */
public class PlayerIntMap extends AbstractPlayerMap implements Cloneable {

    private Map<GamePlayer,Integer> mapInts; // stores the number for every player, GamePlayer -> Integer

    public PlayerIntMap() {
        super();
        resetPlayers();
    }

    /**
     * Announces a new player. If this player already exists, his number information will be lost (set to 0).
     * Invokation of this methode is not necessary, setValue/addValue do this automatically otherwise.
     *
     * @param player The new player.
     */
    public void newPlayer(GamePlayer player) {
        mapInts.put(player, Integer.valueOf(0));
    }


    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#resetPlayers()
     */
    @Override
    public void resetPlayers() {
        mapInts = new HashMap<>();
    }

    /**
     * Resets the value information for each player.
     * @param value The value every player shall get.
     */
    public void resetValues(int value) {
        GamePlayer[] p = getPlayers();
        for (int i=0; i<p.length; i++) {
            setValue(p[i], value);
        }
    }

    /**
     * @param player The player to set a value for.
     * @param value The value to set.
     */
    public void setValue(GamePlayer player, int value) {
        mapInts.put(player, Integer.valueOf(value));
    }

    /**
     * @param player The player to get the value for.
     * @return The value of the player or INVALID_INT if player does not exists.
     */
    public int getValue(GamePlayer player) {
        return getValue(player, HGBaseTools.INVALID_INT);
    }

    /**
     * @param player The player to get the value for.
     * @param defaultValue The default value if the player does not exist.
     * @return The value of the player of the defaultValue if player does not exist.
     */
    public int getValue(GamePlayer player, int defaultValue) {
        Integer value = mapInts.get(player);
        return (value==null)? defaultValue : value.intValue();
    }

    /**
     * Adds the given value to the existing value (or set the value if it does not exist).
     *
     * @param player The player to add a value.
     * @param valueToAdd The value to sum to the existing value.
     */
    public void addValue(GamePlayer player, int valueToAdd) {
        int oldValue = getValue(player, 0);
        setValue(player, oldValue + valueToAdd);
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#getPlayers()
     */
    @Override
    public GamePlayer[] getPlayers() {
        return mapInts.keySet().toArray(new GamePlayer[mapInts.size()]);
    }

   /* (non-Javadoc)
    * @see tjger.lib.AbstractPlayerMap#saveMap(org.w3c.dom.Document, org.w3c.dom.Element)
    */
    @Override
    public void saveMap(Document doc, Element root) {
        GamePlayer[] player = getPlayers();
        for (int i=0; i<player.length; i++) {
            int index = GameManager.getInstance().getGameEngine().getIndexOfPlayer(player[i]);
            Element eP = doc.createElement("player"+index);
            eP.setAttribute("value", String.valueOf(getValue(player[i])));
            root.appendChild(eP);
        }
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#loadNode(tjger.game.completed.GameEngine, org.w3c.dom.Node)
     */
    @Override
    protected void loadNode(GameEngine engine, Node node) {
        int player = HGBaseTools.toInt(node.getNodeName().replaceFirst("player", ""));
        int value = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "value"));
        if (player>=0 && value!=HGBaseTools.INVALID_INT) {
            GamePlayer p = engine.getPlayerWithIndex(player);
            if (p!=null) {
                setValue(p, value);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#appendPlayerData(java.lang.StringBuilder, tjger.game.GamePlayer)
     */
    @Override
    protected void appendPlayerData(StringBuilder data, GamePlayer player) {
        data.append(getValue(player));
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#readPlayerData(tjger.game.GamePlayer, java.lang.String)
     */
    @Override
    protected boolean readPlayerData(GamePlayer player, String string) {
        int v = HGBaseTools.toInt(string);
        if (HGBaseTools.isValid(v)) {
            setValue(player, v);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        PlayerIntMap newMap = new PlayerIntMap();
        GamePlayer[] players = this.getPlayers();
        for (int i=0; i<players.length; i++) {
            newMap.setValue(players[i], this.getValue(players[i]));
        }
        return newMap;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (mapInts==null)? "null" : mapInts.entrySet().toString();
    }

    /**
     * @return The sum of all values of the players.
     */
    public int getAllValues() {
        int sum = 0;
        GamePlayer[] players = this.getPlayers();
        for (int i=0; i<players.length; i++) {
            sum = sum + this.getValue(players[i], 0);
        }
        return sum;
    }

}
