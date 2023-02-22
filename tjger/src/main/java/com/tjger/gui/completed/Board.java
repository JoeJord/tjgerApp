package com.tjger.gui.completed;

import android.graphics.Bitmap;

import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A game board.
 *
 * @author hagru
 */
public class Board extends Part {

    final private int xPos;
    final private int yPos;
    final private int zoom;

    public Board(String name, Bitmap image, int xPos, int yPos, boolean hidden, int zoom, boolean proTeaser) {
        super(ConstantValue.CONFIG_BOARD, name, image, hidden, proTeaser);
        this.xPos = xPos;
        this.yPos = yPos;
        this.zoom = (zoom > 0) ? zoom : 100;
    }

    /* (non-Javadoc)
     * @see tjger.gui.completed.Part#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        return (HGBaseTools.equalClass(this, o2) && this.toString().equals(o2.toString()));
    }

    /**
     * @return The starting x-position for painting the board.
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * @return The starting y-position for painting the board.
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * @return The zoom factor for this background.
     */
    public int getZoom() {
        return zoom;
    }

}
