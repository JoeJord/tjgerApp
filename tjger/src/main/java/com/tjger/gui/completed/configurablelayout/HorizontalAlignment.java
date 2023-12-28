package com.tjger.gui.completed.configurablelayout;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The possible horizontal alignments in a game panel with a configurable layout.
 */
public enum HorizontalAlignment {
    LEFT, CENTER, RIGHT;

    /**
     * Returns the enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     *
     * @param name The name of enum constant.
     * @return The enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     */
    public static HorizontalAlignment valueOfSecure(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns the horizontal position where to position an element corresponding to this horizontal alignment or {@code HGBaseTools.INVALID_INT} if the position was specified incorrectly.
     *
     * @param elementWidth The width of the element to position.
     * @param areaWidth    The width of the area in which the element should be positioned.
     * @param leftMargin   The margin to the left within the area.
     * @param rightMargin  The margin to the right within the area.
     * @return The horizontal position where to position an element corresponding to this horizontal alignment or {@code HGBaseTools.INVALID_INT} if the position was specified incorrectly.
     */
    public int getXPosition(int elementWidth, int areaWidth, int leftMargin, int rightMargin) {
        switch (this) {
            case LEFT:
                return leftMargin;
            case CENTER:
                return leftMargin + (((areaWidth - leftMargin - rightMargin) / 2) - (elementWidth / 2));
            case RIGHT:
                return areaWidth - rightMargin - elementWidth;

        }
        return HGBaseTools.INVALID_INT;
    }
}
