package com.tjger.gui.completed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tjger.lib.ArrayUtil;

/**
 * A card set.
 *
 * @author hagru
 */
public class CardSet extends PartSet {

    /*public CardSet(String name, boolean hidden) {
        this(ConstantValue.CONFIG_CARDSET, name, hidden);
    }*/

    public CardSet(String cardSetType, String name, boolean hidden) {
        super(cardSetType, name, hidden);
    }

    /**
     * Adds a new card.
     *
     * @param newCard New card to add to this card set.
     */
    public void addCard(Card newCard) {
        super.addPart(newCard);
    }

    /**
     * @return All cards of a card set.
     */
    public Card[] getCards() {
        return toOrderedCardArray(transform(super.getParts()));
    }

    /**
     * @param color A color of the card set.
     * @return All cards with the given color.
     */
    public Card[] getCards(String color) {
        return toOrderedCardArray(transform(super.getParts(color)));
    }

    /**
     * @param color A color of the card set.
     * @param sequenceStart The starting sequence.
     * @param sequenceEnd The ending sequence.
     * @return All cards from the starting to the ending sequence (both included).
     */
    public Card[] getCards(String color, int sequenceStart, int sequenceEnd) {
        return toOrderedCardArray(transform(super.getParts(color, sequenceStart, sequenceEnd)));
    }

    /**
     * @param listCvp A list with color value parts.
     * @return A list with cards.
     */
    private List<Card> transform(List<ColorValuePart> listCvp) {
        List<Card> listCard = new ArrayList<>();
        for (ColorValuePart cvp : listCvp) {
            if (cvp instanceof Card) {
                listCard.add((Card) cvp);
            }
        }
        return listCard;
    }

    /**
     * @param color A color of the card set.
     * @param sequence A sequence of a card.
     * @return The card with the given color and value or null if it was not found.
     */
    public Card getCard(String color, int sequence) {
        ColorValuePart c = super.getPart(color, sequence);
        if (c instanceof Card) {
            return (Card)c;
        }
        return null;
    }

    /**
     * @param list A list with card objects.
     * @return An array with the cards in the correct order.
     */
    private Card[] toOrderedCardArray(List<Card> list) {
        Card[] cards = ArrayUtil.toCard(list);
        Arrays.sort(cards);
        return cards;
    }

}
