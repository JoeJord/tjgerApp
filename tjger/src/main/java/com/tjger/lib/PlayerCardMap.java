package com.tjger.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.gui.completed.Card;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Stores for a number of players the cards each player holds.
 *
 * @author hagru
 */
public class PlayerCardMap extends AbstractPlayerMap implements Cloneable {

    // stores the cards for every player, GamePlayer -> ArrayList (with cards)
    private Map<GamePlayer,List<Card>> mapCards = new LinkedHashMap<>();

    public PlayerCardMap() {
        super();
        resetPlayers();
    }

    /**
     * Announces a new player. If this player already exists, his cards' information will be lost.
     * Invokation of this methode is not necessary, addCard(s) does it automatically otherwise.
     *
     * @param player The new player.
     */
    public void newPlayer(GamePlayer player) {
        mapCards.put(player, new ArrayList<Card>());
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#resetPlayers()
     */
    @Override
    public void resetPlayers() {
        mapCards = new LinkedHashMap<>();
    }

    /**
     * Resets the cards' information for each player.
     *
     */
    public void resetCards() {
        GamePlayer[] p = getPlayers();
        for (int i=0; i<p.length; i++) {
            resetCards(p[i]);
        }
    }

    /**
     * Resets the cards' information for the given player.
     *
     * @param player The player that loses his cards.
     */
    public void resetCards(GamePlayer player) {
        if (mapCards.containsKey(player)) {
            mapCards.put(player, new ArrayList<Card>());
        }
    }

    /**
     * Adds new cards to the given player.
     *
     * @param player The player that gets further cards.
     * @param cards A list with cards.
     */
    public void addCards(GamePlayer player, Card[] cards) {
        if (cards.length==0 && !mapCards.containsKey(player)) {
            newPlayer(player);
        } else {
	        for (int i=0; i<cards.length; i++) {
	            addCard(player, cards[i]);
	        }
        }
    }

    /**
     * Adds new cards to the given player.
     *
     * @param player The player that gets further cards.
     * @param cards A list with cards.
     */
    public void addCards(GamePlayer player, List<Card> cards) {
        if (!mapCards.containsKey(player)) {
            newPlayer(player);
        }
        if (!cards.isEmpty()) {
            List<Card> cardList = mapCards.get(player);
            cardList.addAll(cards);
        }
    }

    /**
     * Add a new card to the given player.
     *
     * @param player The player that gets a further card.
     * @param card The card.
     */
    public void addCard(GamePlayer player, Card card) {
        if (!mapCards.containsKey(player)) {
            newPlayer(player);
        }
        List<Card> cardList = mapCards.get(player);
        cardList.add(card);
    }

    /**
     * Removes cards from the given player.
     *
     * @param player The player that loses cards.
     * @param cards A list with cards.
     */
    public void removeCards(GamePlayer player, Card[] cards) {
        for (int i=0; i<cards.length; i++) {
            removeCard(player, cards[i]);
        }
    }

    /**
     * Removes cards from the given player.
     *
     * @param player The player that loses cards.
     * @param cards A list with cards.
     */
    public void removeCards(GamePlayer player, List<Card> cards) {
        if (mapCards.containsKey(player)) {
            List<Card> cardList = mapCards.get(player);
	        if (cardList!=null) {
	            cardList.removeAll(cards);
	        }
        }
    }

    /**
     * Remove a card from the given player.
     *
     * @param player The player that loses a card.
     * @param card The card.
     * @return True if the card was removed, otherwise false.
     */
    public boolean removeCard(GamePlayer player, Card card) {
        if (mapCards.containsKey(player)) {
	        List<Card> cardList = mapCards.get(player);
	        if (cardList!=null) {
	            return cardList.remove(card);
	        }
        }
        return false;
    }

    /**
     * Replaces a card from the given player with another card.
     *
     * @param player The player to whom a card is replaced.
     * @param oldCard The card, which should be replaced.
     * @param newCard The card, which replaces the old card.
     * @return True if the card was replaced, otherwise false.
     */
    public boolean replaceCard(GamePlayer player, Card oldCard, Card newCard) {
      if (mapCards.containsKey(player)) {
        List<Card> cardList = mapCards.get(player);
        if (cardList!=null) {
          int index = cardList.indexOf(oldCard);
          if (index != -1) {
            cardList.set(index, newCard);
            return true;
          }
        }
      }
      return false;
    }

    /**
     * @see #getCards(GamePlayer, boolean)
     */
    public Card[] getCards(GamePlayer player) {
        return getCards(player, true);
    }


    /**
     * Get all cards of a player.
     *
     * @param player The player which cards are of interest.
     * @param ordered True if the cards shall be returned ordered.
     * @return A list with cards (can be empty).
     */
    public Card[] getCards(GamePlayer player, boolean ordered) {
        List<Card> cardList = mapCards.get(player);
        if (cardList==null) {
            return new Card[0];
        }
        Card[] cards = cardList.toArray(new Card[cardList.size()]);
        if (ordered) {
            HGBaseTools.orderList(cards);
        }
        return cards;
    }

    /**
     * @param ordered True if the array shall be returned ordered.
     * @return An array with all cards that are stored in the map.
     */
    public Card[] getAllCards(boolean ordered) {
        List<Card> cardList = getAllCardList(ordered);
        return ArrayUtil.toCard(cardList);
    }

    /**
     * @see #getCardList(GamePlayer, boolean)
     */
    public List<Card> getCardList(GamePlayer player) {
        return getCardList(player, true);
    }

    /**
     * Get the list with the cards for the given player.
     * Changes on the returned list effect the list that is saved at this card-map (no copy).
     *
     * @param player The player which cards are of interest.
     * @param ordered True if the cards shall be returned ordered.
     * @return The list with the cards for the player, can be null.
     */
    public List<Card> getCardList(GamePlayer player, boolean ordered) {
        List<Card> cardList = mapCards.get(player);
        if (ordered && cardList!=null) {
            Collections.sort(cardList);
        }
        return cardList;
    }

    /**
     * @param ordered True if the list shall be returned ordered.
     * @return A list with all cards that are stored in the map.
     */
    public List<Card> getAllCardList(boolean ordered) {
        List<Card> cardList = new ArrayList<>();
        GamePlayer[] p = getPlayers();
        for (int i=0; i<p.length; i++) {
            cardList.addAll(getCardList(p[i], false));
        }
        if (ordered) {
            Collections.sort(cardList);
        }
        return cardList;
    }


    /**
     * Get a single card of a player.
     *
     * @param player The player which card is of interest.
     * @param index The index of the card (starts with 0).
     * @return The card or null.
     */
    public Card getCard(GamePlayer player, int index) {
        Card[] cards = getCards(player);
        if (index>=0 && index<cards.length) {
            return cards[index];
        }
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#getPlayers()
     */
    @Override
    public GamePlayer[] getPlayers() {
        return mapCards.keySet().toArray(new GamePlayer[0]);
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#saveMap(org.w3c.dom.Document, org.w3c.dom.Element)
     */
    @Override
    public void saveMap(Document doc, Element root) {
        GamePlayer[] player = getPlayers();
        for (int i=0; i<player.length; i++) {
            int index = GameManager.getInstance().getGameEngine().getIndexOfPlayer(player[i]);
            Card[] cards = getCards(player[i]);
            Element eP = XmlUtil.saveCardArray(doc, "player"+index, cards);
            root.appendChild(eP);
        }
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#loadNode(tjger.game.completed.GameEngine, org.w3c.dom.Node)
     */
    @Override
    protected void loadNode(GameEngine engine, Node node) {
        int player = HGBaseTools.toInt(node.getNodeName().replaceFirst("player", ""));
        if (player>=0) {
            GamePlayer p = engine.getPlayerWithIndex(player);
            if (p!=null) {
                Card[] cards = XmlUtil.loadCardArray(node);
                addCards(p, cards);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#appendPlayerData(java.lang.StringBuilder, tjger.game.GamePlayer)
     */
    @Override
    protected void appendPlayerData(StringBuilder data, GamePlayer player) {
        data.append(NetworkUtil.fromCardArray(getCards(player)));
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractPlayerMap#readPlayerData(tjger.game.GamePlayer, java.lang.String)
     */
    @Override
    protected boolean readPlayerData(GamePlayer player, String string) {
        Card[] cards = NetworkUtil.toCardArray(string);
        addCards(player, cards);
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        PlayerCardMap newMap = new PlayerCardMap();
        GamePlayer[] players = this.getPlayers();
        for (int i=0; i<players.length; i++) {
            newMap.addCards(players[i], this.getCards(players[i], false));
        }
        return newMap;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (mapCards==null)? "null" : mapCards.entrySet().toString();
    }
}
