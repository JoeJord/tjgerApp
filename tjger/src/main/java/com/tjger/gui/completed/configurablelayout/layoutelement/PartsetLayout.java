package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * The layout configuration for a partset.
 */
public class PartsetLayout extends LayoutGameElementSet {
    /**
     * Constructs a new instance.
     *
     * @param type          The type of the partset.
     * @param xPos          The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos          The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize   The percent size with which the parts should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param angle         The angle with which the parts should be painted.
     * @param area          The area in which the parts should be painted.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param orientation   The orientation with which the parts should be painted. The value may be "ltr_down", "ltr_up", "down_ltr", "up_ltr", "rtl_down", "rtl_up", "down_rtl" or "up_rtl".
     * @param wrapThreshold The threshold at which a wrapping is done. Only needed if {@code orientation} is specified.
     */
    public PartsetLayout(String type, String xPos, String yPos, String percentSize, double angle, AreaLayout area, int xSpacing, int ySpacing, String orientation, int wrapThreshold) {
        super(type, xPos, yPos, percentSize, angle, area, xSpacing, ySpacing, orientation, wrapThreshold);
    }
}
