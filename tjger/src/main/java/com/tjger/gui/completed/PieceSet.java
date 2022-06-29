package com.tjger.gui.completed;

import java.util.ArrayList;
import java.util.List;

import com.tjger.lib.ArrayUtil;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A piece set.
 *
 * @author hagru
 */
public class PieceSet extends PartSet {

    public PieceSet(String name, boolean hidden) {
        super(ConstantValue.CONFIG_PIECESET, name, hidden);
    }

    /**
     * Adds a new piece.
     *
     * @param newPiece New piece to add to this piece set.
     */
    public void addPiece(Piece newPiece) {
        super.addPart(newPiece);
    }

    /**
     * @return All pieces of a piece set.
     */
    public Piece[] getPieces() {
        return toOrderedPieceArray(transform(super.getParts()));
    }

    /**
     * @param color A color of the piece set.
     * @return All pieces with the given color.
     */
    public Piece[] getPieces(String color) {
        return toOrderedPieceArray(transform(super.getParts(color)));
    }

    /**
     * @param color A color of the piece set.
     * @param sequenceStart The starting sequence.
     * @param sequenceEnd The ending sequence.
     * @return All pieces from the starting to the ending sequence (both included).
     */
    public Piece[] getPieces(String color, int sequenceStart, int sequenceEnd) {
        return toOrderedPieceArray(transform(super.getParts(color, sequenceStart, sequenceEnd)));
    }

    /**
     * @param listCvp A list with color value parts.
     * @return A list with pieces.
     */
    private List<Piece> transform(List<ColorValuePart> listCvp) {
    	List<Piece> listPiece = new ArrayList<>();
    	for (ColorValuePart cvp : listCvp) {
    		if (cvp instanceof Piece) {
    			listPiece.add((Piece)cvp);
    		}
    	}
    	return listPiece;
    }


    /**
     * @param color A color of the piece set.
     * @param sequence A sequence of a piece.
     * @return The piece with the given color and value or null if it was not found.
     */
    public Piece getPiece(String color, int sequence) {
        ColorValuePart p = super.getPart(color, sequence);
        if (p instanceof Piece) {
            return (Piece)p;
        }
        return null;
    }

    /**
     * @param list A list with card objects.
     * @return An array with the cards in the correct order.
     */
    private Piece[] toOrderedPieceArray(List<Piece> list) {
        Piece[] pieces = ArrayUtil.toPiece(list);
        HGBaseTools.orderList(pieces);
        return pieces;
    }

}
