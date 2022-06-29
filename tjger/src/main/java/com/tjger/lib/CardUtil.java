package com.tjger.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.tjger.game.GamePlayer;
import com.tjger.gui.completed.Card;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Some help methods for managing cards.
 *
 * @author hagru
 */
public class CardUtil {

    private static Random cardRandom = new Random(new Date().getTime());

    private CardUtil() {
    	super();
    }

    /**
     * Mix the given cards.
     *
     * @param cards Cards to be mixed.
     * @return The mixed cards.
     */
    public static Card[] mixCards(Card[] cards) {
        // use the shuffle algorithm of java.util.Collections
        List<Card> list = Arrays.asList(cards);
        Collections.shuffle(list, cardRandom);
        return list.toArray(new Card[cards.length]);
    }

    /**
     * @param cards A list with cards that will be mixed.
     */
    public static void mixCards(List<Card> cards) {
        Collections.shuffle(cards, cardRandom);
    }

    /**
     * Adds a card to the given card set.
     *
     * @param cards The card set.
     * @param addCard The card to add (at the end).
     * @return The new card set with one card more.
     */
    public static Card[] addCard(Card[] cards, Card addCard) {
        Card[] newCards = new Card[cards.length + 1];
        System.arraycopy(cards, 0, newCards, 0, cards.length);
        newCards[cards.length] = addCard;
        return newCards;
    }

    /**
     * Adds cards to the given card set.
     *
     * @param cards The card set.
     * @param addCards The cards to add (at the end).
     * @return The new card set with one card more.
     */
    public static Card[] addCards(Card[] cards, Card[] addCards) {
        return (Card[])HGBaseTools.sumArrays(cards, addCards);
    }

    /**
     * Remove a card from the given card set.
     *
     * @param cards The card set.
     * @param removeCard The card to remove.
     * @return The card set without the given card.
     */
    public static Card[] removeCard(Card[] cards, Card removeCard) {
        List<Card> listCard = new ArrayList<>(Arrays.asList(cards));
        listCard.remove(removeCard);
        return ArrayUtil.toCard(listCard);
    }

    /**
     * Remove cards from the given card set.
     *
     * @param cards The card set.
     * @param removeCards The cards to remove.
     * @return The card set without the given cards.
     */
    public static Card[] removeCards(Card[] cards, Card[] removeCards) {
        List<Card> listCard = new ArrayList<>(Arrays.asList(cards));
        for (int i=0; i<removeCards.length; i++) {
            listCard.remove(removeCards[i]);
        }
        return ArrayUtil.toCard(listCard);
    }

    /**
     * Deals the cards to the given player where every player gets the given number of cards.
     * The cards are divided that player 0 gets the first numCards cards, player 1 the next and so one.
     * The cards are stored in the cardMap (normally the map should be reseted before usind this method).
     * The remaining cards are returned.
     *
     * @param cards An array with all cards.
     * @param numCards The number of cards for each player to get.
     * @param players The players that shall get cards.
     * @param cardMap The map to store the cards for the players.
     * @return The remaining cards.
     */
    public static Card[] dealCards(Card[] cards, int numCards, GamePlayer[] players, PlayerCardMap cardMap) {
        Card[] remainCards = cards.clone();
        int cardCounter = 0;
        for (int p=0; p<players.length; p++) {
            if (players[p]!=null) {
                for (int c=0; c<numCards; c++) {
                    if (cardCounter<cards.length) {
	                    cardMap.addCard(players[p], cards[cardCounter]);
	                    remainCards = removeCard(remainCards, cards[cardCounter]);
	                    cardCounter++;
                    }
                }
            }
        }
        return remainCards;
    }

    /**
     * Deals the cards to the given player where every player gets the given number of cards.
     * The cards are divided that player 0 gets the first numCards cards, player 1 the next and so one.
     * The cards are stored in the cardMap (normally the map should be reseted before usind this method).
     *
     * @param cards A list with all cards, where the cards are removed from this list.
     * @param numCards The number of cards for each player to get.
     * @param players The players that shall get cards.
     * @param cardMap The map to store the cards for the players.
     */
    public static void dealCards(List<Card> cards, int numCards, GamePlayer[] players, PlayerCardMap cardMap) {
        if (numCards<=0) {
            return;
        }
        for (int p=0; p<players.length; p++) {
            if (players[p]!=null) {
                int cardSize = cards.size();
                if (cardSize == 0) {
                    // no cards left
                    return;
                }
                if (numCards > cardSize) {
                    // reduce the number of cards to deal, the method will stop at the next player where the list size is 0
                    numCards = cardSize;
                }
                List<Card> dealList = cards.subList(0, numCards);
                cardMap.addCards(players[p], ArrayUtil.toCard(dealList));
                dealList.clear();
            }
        }
    }


    /**
     * Deals the first cards of a card set to target cards.
     * The number of cards that are dealed is the length of the target array.
     *
     * @param cards An array with all cards.
     * @param target The target card array.
     * @return The remaining cards.
     */
    public static Card[] dealCards(Card[] cards, Card[] target) {
        for (int c=0; c<target.length; c++) {
            if (c<cards.length) {
            	target[c] = cards[c];
            } else {
                target[c] = null;
            }
        }
        return removeCards(cards, target);
    }

    /**
     * Deals the first num cards and returns these ones as a list.
     * The given list is reduced by these cards.
     *
     * @param cards The list with the cards, to be reduced.
     * @param num The number of cards to take from the list.
     * @return A list with the cards taken from the given cards.
     */
    public static List<Card> dealCards(List<Card> cards, int num) {
        int listSize = cards.size();
        if (listSize==0 || num<=0) {
            return new ArrayList<>();
        }

    	if (num>listSize) {
    	    num = listSize;
    	}
    	List<Card> tmpList = cards.subList(0, num);
    	List<Card> dealList = new ArrayList<>(tmpList);
    	tmpList.clear();
    	return dealList;
    }

    /**
     * @param cards An array with cards.
     * @return The array with the cards in reversed order.
     */
    public static Card[] reverseCards(Card[] cards) {
        List<Card> reverseList = reverseCards(Arrays.asList(cards));
        return ArrayUtil.toCard(reverseList);
    }

    /**
     * @param cards A list with cards.
     * @return A list with the cards in reversed order.
     */
    public static List<Card> reverseCards(List<Card> cards) {
        List<Card> newList = new ArrayList<>(cards);
        Collections.reverse(newList);
        return newList;
    }

    /**
     * Returns a card array that has every card of the given array a second time.
     *
     * @param cards A card array.
     * @return A card array that has the double size.
     */
    public static Card[] doubleCards(Card[] cards) {
        Card[] sum = new Card[cards.length * 2];
        HGBaseTools.sumArrays(sum, cards, cards);
        return sum;
    }

    /* (non-Javadoc)
     * @see doubleCards(Card[])
     */
    public static List<Card> doubleCards(List<Card> cards) {
    	List<Card> list = new ArrayList<>(cards);
    	list.addAll(cards);
    	return list;
    }

    /**
     * @param cards The cards to get the values from.
     * @return The sum of the values of the cards.
     */
    public static int getCardsValue(Card[] cards) {
        int values = 0;
        if (cards!=null) {
	        for (int i=0; i<cards.length; i++) {
	            values += cards[i].getValue();
	        }
        }
        return values;
    }

    /* (non-Javadoc)
     * @see getCardsValue(Card[])
     */
    public static int getCardsValue(List<Card> cards) {
        return getCardsValue(ArrayUtil.toCard(cards));
    }

}
