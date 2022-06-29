package com.tjger.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.PlayerType;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.Piece;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Utility for transforming ArrayLists into arrays.
 *
 * @author hagru
 */
public class ArrayUtil {

    private ArrayUtil() {
        super();
    }

    /**
     * @param list ArrayList with GamePlayer objects.
     * @return A GamePlayer array.
     */
    public static GamePlayer[] toGamePlayer(List<GamePlayer> list) {
        if (list==null) {
            return new GamePlayer[0];
        }
        return list.toArray(new GamePlayer[list.size()]);
    }

    /**
     * @param list ArrayList with PlayerType objects.
     * @return A PlayerType array.
     */
    public static PlayerType[] toPlayerType(List<PlayerType> list) {
        if (list==null) {
            return new PlayerType[0];
        }
        return list.toArray(new PlayerType[list.size()]);
    }
    /**
     * Transforms an array with player types to a string array with the ids of the type.
     * Does not use the toString()-method, because this is language dependend.
     *
     * @param list array with parts.
     * @return string array.
     */
    public static String[] toPlayerTypeIds(PlayerType[] list) {
    	return HGBaseTools.toStringIdArray(list);
    }

    /**
     * @param list ArrayList with Card objects.
     * @return A Card array.
     */
    public static Card[] toCard(List<Card> list) {
        if (list==null) {
            return new Card[0];
        }
        return list.toArray(new Card[list.size()]);
    }

    /**
     * @param list ArrayList with Piece objects.
     * @return A Piece array.
     */
    public static Piece[] toPiece(List<Piece> list) {
        if (list==null) {
            return new Piece[0];
        }
        return list.toArray(new Piece[list.size()]);
    }

    /**
     * Transforms an array with parts to a string array with the names.
     *
     * @param list array with parts.
     * @return string array.
     */
    public static String[] toPartNames(Part[] list) {
        String[] strLines = new String[list.length];
        for (int i=0; i<strLines.length; i++) {
            strLines[i] = list[i].getName();
        }
        return strLines;
    }

    /**
     * @param cards An array with cards.
     * @return A list holding the cards.
     */
    public static List<Card> toList(Card[] cards) {
        return new ArrayList<>(Arrays.asList(cards));
    }

    /**
     * @param pieces An array with pieces.
     * @return A list holding the pieces.
     */
    public static List<Piece> toList(Piece[] pieces) {
        return new ArrayList<>(Arrays.asList(pieces));
    }

}
