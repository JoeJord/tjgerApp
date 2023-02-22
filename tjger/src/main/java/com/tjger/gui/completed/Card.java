package com.tjger.gui.completed;

import android.graphics.Bitmap;

import com.tjger.lib.ConstantValue;

/**
 * A single card of a card set.
 *
 * @author hagru
 */
public class Card extends ColorValuePart {

    public Card(CardSet cardSet, String color, int sequence, int value, Bitmap image, boolean proTeaser) {
        super(cardSet, ConstantValue.CONFIG_CARD, color, sequence, value, image, proTeaser);
    }

    /**
     * @return The card set to which this card belongs.
     */
    public CardSet getCardSet() {
        return (CardSet) super.getPartSet();
    }

}
