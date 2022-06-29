package com.tjger.gui.completed;

import com.tjger.lib.ConstantValue;

import android.graphics.Bitmap;

/**
 * A single piece of a piece set.
 *
 * @author hagru
 */
public class Piece extends ColorValuePart {

    public Piece(PieceSet pieceSet, String color, int sequence, int value, Bitmap image) {
        super(pieceSet, ConstantValue.CONFIG_PIECE, color, sequence, value, image);
    }
    
    /**
     * @return The piece set to which this piese belongs.
     */
    public PieceSet getPieceSet() {
        return (PieceSet)super.getPartSet();
    }

}
