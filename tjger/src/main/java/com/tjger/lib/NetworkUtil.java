package com.tjger.lib;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.CardSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import at.hagru.hgbase.lib.HGBaseStringBuilder;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Some help methods to get objects to correct parts of messages and back.
 *
 * @author hagru
 */
public class NetworkUtil {

    private NetworkUtil() {
        super();
    }

    /**
     * @param array An int array.
     * @return A message part.
     */
    public static String fromIntArray(int[] array) {
        if (array == null) {
            return ConstantValue.NETWORK_NULL;
        }
        HGBaseStringBuilder msg = new HGBaseStringBuilder(ConstantValue.NETWORK_DIVIDEPART);
        for (int element : array) {
            msg.append(String.valueOf(element));
        }
        return msg.toString();
    }

    /**
     * @param msg A message part.
     * @return An int array.
     */
    public static int[] toIntArray(String msg) {
        if (msg.isEmpty() || msg.equals(ConstantValue.NETWORK_NULL)) {
            return null;
        }
        String[] array = msg.split(ConstantValue.NETWORK_DIVIDEPART);
        int[] values = new int[array.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = HGBaseTools.toInt(array[i]);
        }
        return values;
    }

    /**
     * @param array A boolean array.
     * @return A message part.
     */
    public static String fromBooleanArray(boolean[] array) {
        if (array == null) {
            return ConstantValue.NETWORK_NULL;
        }
        HGBaseStringBuilder msg = new HGBaseStringBuilder(ConstantValue.NETWORK_DIVIDEPART);
        for (boolean element : array) {
            msg.append(element ? "1" : "0");
        }
        return msg.toString();
    }

    /**
     * @param msg A message part.
     * @return A boolean array.
     */
    public static boolean[] toBooleanArray(String msg) {
        if (msg.equals(ConstantValue.NETWORK_NULL)) {
            return null;
        }
        String[] array = msg.split(ConstantValue.NETWORK_DIVIDEPART);
        boolean[] values = new boolean[array.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = array[i].equals("1");
        }
        return values;
    }

    /**
     * @param array A string array.
     * @return A message part.
     */
    public static String fromStringArray(String[] array) {
        if (array == null) {
            return ConstantValue.NETWORK_NULL;
        }
        HGBaseStringBuilder msg = new HGBaseStringBuilder(ConstantValue.NETWORK_DIVIDEPART);
        for (String element : array) {
            if (element != null) {
                msg.append(element);
            }
        }
        return msg.toString();
    }

    /**
     * @param msg A message part.
     * @return A String array.
     */
    public static String[] toStringArray(String msg) {
        if (msg.equals(ConstantValue.NETWORK_NULL)) {
            return null;
        }
        String[] values = msg.split(ConstantValue.NETWORK_DIVIDEPART);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(ConstantValue.NETWORK_NULL)) {
                values[i] = ConstantValue.NETWORK_NULL;
            }
        }
        return values;
    }

    /**
     * @param array A card array.
     * @return A message part.
     */
    public static String fromCardArray(Card[] array) {
        if (array == null) {
            return ConstantValue.NETWORK_NULL;
        }
        HGBaseStringBuilder msg = new HGBaseStringBuilder(ConstantValue.NETWORK_DIVIDEPART);
        for (Card card : array) {
            msg.append(fromCard(card));
        }
        return msg.toString();
    }

    /**
     * @param cardList A list with cards.
     * @return A network message part.
     */
    public static String fromCardList(List<Card> cardList) {
        if (cardList == null) {
            return ConstantValue.NETWORK_NULL;
        }
        return fromCardArray(ArrayUtil.toCard(cardList));
    }

    /**
     * @param card A single card.
     * @return A message part
     */
    public static String fromCard(Card card) {
        if (card == null) {
            return ConstantValue.NETWORK_NULL;
        } else {
            String type = card.getCardSet().getType();
            String data = card.getColor() + "-" + card.getSequence();
            if (!ConstantValue.CONFIG_CARDSET.equals(type)) {
                data = data + "-" + type;
            }
            return data;
        }
    }

    /**
     * @param msg A message part.
     * @return A card array.
     */
    public static Card[] toCardArray(String msg) {
        if (msg.equals(ConstantValue.NETWORK_NULL)) {
            return null;
        }
        String[] array = msg.split(ConstantValue.NETWORK_DIVIDEPART);
        if (array.length == 1 && array[0].isEmpty()) {
            return new Card[0];
        }
        Card[] values = new Card[array.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = toCard(array[i]);
        }
        return values;
    }

    /**
     * @param msg A network message part.
     * @return A list with cards or null.
     */
    public static List<Card> toCardList(String msg) {
        Card[] cards = toCardArray(msg);
        return (cards == null) ? null : ArrayUtil.toList(cards);
    }

    /**
     * @param msg A message part
     * @return A single card.
     */
    public static Card toCard(String msg) {
        if (msg == null || msg.equals(ConstantValue.NETWORK_NULL)) {
            return null;
        } else {
            String[] parts = msg.split("-");
            if (parts.length < 2) {
                return null;
            }
            String color = parts[0];
            int sequence = HGBaseTools.toInt(parts[1]);
            String type = (parts.length >= 3) ? parts[2] : ConstantValue.CONFIG_CARDSET;
            CardSet set = GameConfig.getInstance().getActiveCardSet(type);
            if (set == null) {
                return null;
            }
            return set.getCard(color, sequence);
        }
    }

    /**
     * Returns a string for the specified collection, which can be transferred over the network.
     *
     * @param collection The collection.
     * @param converter  The converter of one element.
     * @param <T>        The type of the elements in the collection.
     * @return A string for the specified collection, which can be transferred over the network.
     */
    public static <T> String fromCollection(Collection<T> collection, Function<T, String> converter) {
        if (collection == null) {
            return ConstantValue.NETWORK_NULL;
        }
        return collection.stream().map(converter).collect(Collectors.joining(ConstantValue.NETWORK_DIVIDEPART));
    }

    /**
     * Returns a collection from the specified string, which was transferred over the network.
     *
     * @param msg       A network message part to convert.
     * @param converter The converter of one element.
     * @param <T>       The type of the elements in the collection.
     * @return A collection from the specified string, which was transferred over the network.
     */
    public static <T> Collection<T> toCollection(String msg, Function<String, T> converter) {
        if ((msg == null) || (ConstantValue.NETWORK_NULL.equals(msg))) {
            return null;
        }
        return Arrays.stream(msg.split(ConstantValue.NETWORK_DIVIDEPART)).map(converter).collect(Collectors.toList());
    }
}
