package com.tjger.gui.completed.configurablelayout;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The possible vertical alignments in a game panel with a configurable layout.
 */
public enum VerticalAlignment {
    TOP, MIDDLE, BOTTOM;

    /**
     * Returns the enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     *
     * @param name The name of enum constant.
     * @return The enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     */
    public static VerticalAlignment valueOfSecure(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns the vertical position where to position an element corresponding to this vertical alignment or {@code HGBaseTools.INVALID_INT} if the position was specified incorrectly.
     *
     * @param elementHeight The height of the element to position.
     * @param areaHeight    The height of the area in which the element should be positioned.
     * @param topMargin     The margin on the top within the area.
     * @param bottomMargin  The margin to the bottom within the area.
     * @return The vertical position where to position an element corresponding to this vertical alignment or {@code HGBaseTools.INVALID_INT} if the position was specified incorrectly.
     */
    public int getYPosition(int elementHeight, int areaHeight, int topMargin, int bottomMargin) {
        switch (this) {
            case TOP:
                return topMargin;
            case MIDDLE:
                return topMargin + (((areaHeight - topMargin - bottomMargin) / 2) - (elementHeight / 2));
            case BOTTOM:
                return areaHeight - bottomMargin - elementHeight;

        }
        return HGBaseTools.INVALID_INT;
    }
}
