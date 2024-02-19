package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * The layout configuration for a part.
 */
public class PartLayout extends LayoutGameElement {
    /**
     * Constructs a new instance.
     *
     * @param type        The type of the part.
     * @param xPos        The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos        The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize The percent size with which the part should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param angle       The angle with which the part should be painted.
     * @param area        The area in which the part should be painted.
     */
    public PartLayout(String type, String xPos, String yPos, String percentSize, double angle, AreaLayout area) {
        super(type, xPos, yPos, percentSize, angle, area);
    }
}
