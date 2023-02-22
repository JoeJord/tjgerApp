package com.tjger.gui.completed;

import android.graphics.Bitmap;

import com.tjger.lib.ConstantValue;

/**
 * A single piece of a piece set.
 *
 * @author hagru
 */
public class Piece extends ColorValuePart {

    public Piece(PieceSet pieceSet, String color, int sequence, int value, Bitmap image, boolean proTeaser) {
        super(pieceSet, ConstantValue.CONFIG_PIECE, color, sequence, value, image, proTeaser);
    }

    /**
     * @return The piece set to which this piese belongs.
     */
    public PieceSet getPieceSet() {
        return (PieceSet) super.getPartSet();
    }

}
