package com.tjger.gui.completed.configurablelayout.layoutelement;

import com.tjger.gui.Orientation;

import at.hagru.hgbase.android.awt.Dimension;

/**
 * A set of game elements of a configurable layout.
 */
public class LayoutGameElementSet extends LayoutGameElement {
    /**
     * The horizontal spacing.
     */
    private int xSpacing;
    /**
     * The vertical spacing.
     */
    private int ySpacing;
    /**
     * The orientation with which the elements should be painted.
     */
    private Orientation orientation;
    /**
     * The threshold at which a wrapping is done.<br>
     * Only needed if {@code orientation} is specified.
     */
    private int wrapThreshold;

    /**
     * Constructs a new instance.
     *
     * @param type          The type name of the game element set in the tjger configuration.
     * @param xPos          The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos          The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize   The percent size with which the element set should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param area          The area in which the element set should be painted.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param orientation   The orientation with which the elements should be painted. The value may be "ltr_down", "ltr_up", "down_ltr", "up_ltr", "rtl_down", "rtl_up", "down_rtl" or "up_rtl".
     * @param wrapThreshold The threshold at which a wrapping is done. Only needed if {@code orientation} is specified.
     */
    public LayoutGameElementSet(String type, String xPos, String yPos, String percentSize, AreaLayout area, int xSpacing, int ySpacing, String orientation, int wrapThreshold) {
        super(type, xPos, yPos, percentSize, area);
        setSpacing(xSpacing, ySpacing);
        setOrientation(orientation);
        setWrapThreshold(wrapThreshold);
    }

    /**
     * Sets the drawing spacing for the elements of the set.
     *
     * @param xSpacing The horizontal spacing.
     * @param ySpacing The vertical spacing.
     */
    private void setSpacing(int xSpacing, int ySpacing) {
        this.xSpacing = xSpacing;
        this.ySpacing = ySpacing;
    }

    /**
     * Returns the drawing spacing for the elements of the set.<br>
     * The width of the returned dimension is the horizontal spacing, the height the vertical spacing.
     *
     * @return The drawing spacing for the elements of the set.
     */
    public Dimension getSpacing() {
        return new Dimension(xSpacing, ySpacing);
    }

    /**
     * Returns the orientation with which the elements should be painted.
     *
     * @return The orientation with which the elements should be painted.
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation with which the elements should be painted.
     *
     * @param orientation The orientation with which the elements should be painted.
     */
    private void setOrientation(String orientation) {
        this.orientation = (orientation == null) ? null : Orientation.valueOfSecure(orientation.toUpperCase());
    }

    /**
     * Returns the threshold at which a wrapping is done.
     *
     * @return The threshold at which a wrapping is done.
     */
    public int getWrapThreshold() {
        return wrapThreshold;
    }

    /**
     * Sets the threshold at which a wrapping is done.<br>
     * Only needed if {@code orientation} is specified.
     *
     * @param wrapThreshold The threshold at which a wrapping is done.
     */
    private void setWrapThreshold(int wrapThreshold) {
        this.wrapThreshold = wrapThreshold;
    }
}
