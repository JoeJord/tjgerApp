package com.tjger.gui.completed;

import com.tjger.lib.ConstantValue;

import android.graphics.Bitmap;

/**
 * A single card of a card set.
 *
 * @author hagru
 */
public class Card extends ColorValuePart {

    public Card(CardSet cardSet, String color, int sequence, int value, Bitmap image) {
        super(cardSet, ConstantValue.CONFIG_CARD, color, sequence, value, image);
    }
    
    /**
     * @return The card set to which this card belongs.
     */
    public CardSet getCardSet() {
        return (CardSet)super.getPartSet();
    }

}
