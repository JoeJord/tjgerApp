package com.tjger.gui.completed.configurablelayout;

import at.hagru.hgbase.android.awt.Dimension;

/**
 * The possible scale types in a game panel with a configurable layout.
 */
public enum ScaleType {
    SCALE, SCALE_X, SCALE_Y;

    /**
     * Returns the enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     *
     * @param name The name of enum constant.
     * @return The enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     */
    public static ScaleType valueOfSecure(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns the percent size with which an element should painted to that it fits into specified area size.
     *
     * @param elementSize The size of element.
     * @param areaSize    The size of the area.
     * @return The percent size with which an element should painted to that it fits into specified area size.
     */
    private int calcPercentSize(double elementSize, double areaSize) {
        return (int) (100 * (areaSize / elementSize));
    }

    /**
     * Returns the percent size with which an element should be painted so that it fits into the area corresponding to this scale type or 100 in the case of an error.
     *
     * @param elementSize The normal size of the element.
     * @param areaSize    The size of the area.
     * @return The percent size with which an element should be painted so that it fits into the area corresponding to this scale type or 100 in the case of an error.
     */
    public int getPercentSize(Dimension elementSize, Dimension areaSize) {
        int percentSize = 100;
        if ((elementSize == null) || (areaSize == null)) {
            return percentSize;
        }
        switch (this) {
            case SCALE:
                percentSize = Math.min(calcPercentSize(elementSize.getWidth(), areaSize.getWidth()), calcPercentSize(elementSize.getHeight(), areaSize.getHeight()));
                break;
            case SCALE_X:
                percentSize = calcPercentSize(elementSize.getWidth(), areaSize.getWidth());
                break;
            case SCALE_Y:
                percentSize = calcPercentSize(elementSize.getHeight(), areaSize.getHeight());
                break;
        }
        return percentSize;
    }
}
