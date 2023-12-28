package com.tjger.gui;

/**
 * Possible orientations how multiple elements could be arranged.
 */
public enum Orientation {
    /**
     * First left to right and then down after wrapping.
     */
    LTR_DOWN,
    /**
     * First left to right and then up after wrapping.
     */
    LTR_UP,
    /**
     * First down and then left to right after wrapping.
     */
    DOWN_LTR,
    /**
     * First up and then left to right after wrapping.
     */
    UP_LTR,
    /**
     * First right to left and then down after wrapping.
     */
    RTL_DOWN,
    /**
     * First right to left and then up after wrapping.
     */
    RTL_UP,
    /**
     * First down and then right to left after wrapping.
     */
    DOWN_RTL,
    /**
     * First up and then right to left after wrapping.
     */
    UP_RTL;

    /**
     * Returns the enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     *
     * @param name The name of enum constant.
     * @return The enum constant with the specified name or {@code null} if the name doesn't match exactly an enum constant in this type.
     */
    public static Orientation valueOfSecure(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
