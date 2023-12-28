package com.tjger.gui.completed.configurablelayout.layoutelement;

import com.tjger.gui.completed.configurablelayout.HorizontalAlignment;
import com.tjger.gui.completed.configurablelayout.VerticalAlignment;

import java.util.function.IntSupplier;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * An element of a configurable layout.
 */
public class LayoutElement {
    /**
     * The percent sign.
     */
    protected static final CharSequence PERCENT_SIGN = "%";
    /**
     * The fixed or percentage x coordinate of the position of the element.<br>
     * It is {@code null} if horizontal alignment ({@code hAlign}) is set.
     */
    private String xPos;
    /**
     * The fixed or percentage y coordinate of the position of the element.<br>
     * It is {@code null} if vertical alignment ({@code vAlign}) is set.
     */
    private String yPos;
    /**
     * The horizontal alignment of the element.<br>
     * It is {@code null} if the fixed x coordinate ({@code xPos}) is set.
     */
    private HorizontalAlignment hAlign;
    /**
     * The horizontal alignment of the element.<br>
     * It is {@code null} if the fixed y coordinate ({@code yPos}) is set.
     */
    private VerticalAlignment vAlign;

    /**
     * Constructs a new instance.
     *
     * @param xPos The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     */
    public LayoutElement(String xPos, String yPos) {
        setXPos(xPos);
        setYPos(yPos);
    }

    /**
     * Calculates the proportional value based on a given ratio and total value.
     *
     * @param ratio A string representing the ratio with a percentage sign (e.g., "25%").
     * @param total The total value to calculate the proportion from.
     * @return The proportional value as an integer.
     */
    protected final int calcProportionalValue(String ratio, int total) {
        return (int) ((HGBaseTools.toInt(ratio.replace(PERCENT_SIGN, "").trim(), 0) / 100.0) * total);
    }

    /**
     * Returns the horizontal position where to paint the element. It is {@code null} if the horizontal alignment is set.
     *
     * @param surroundingAreaWidthSupplier The supplier for the width of the surrounding area. It's only needed if the y coordinate is specified via percentage.
     * @return The horizontal position where to paint the element. It is {@code null} if the horizontal alignment is set.
     */
    public Integer getXPos(IntSupplier surroundingAreaWidthSupplier) {
        if (xPos == null) {
            return null;
        }
        if (xPos.contains(PERCENT_SIGN)) {
            if (surroundingAreaWidthSupplier != null) {
                return calcProportionalValue(xPos, surroundingAreaWidthSupplier.getAsInt());
            } else {
                return 0;
            }
        } else {
            return HGBaseTools.toInt(xPos);
        }
    }

    /**
     * Sets the horizontal position where to paint the element.
     *
     * @param xPos The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     */
    private void setXPos(String xPos) {
        hAlign = HorizontalAlignment.valueOfSecure(xPos.toUpperCase());
        if (hAlign == null) {
            this.xPos = xPos;
        } else {
            this.xPos = null;
        }
    }

    /**
     * Returns the horizontal alignment where to paint the element. It is {@code null} if the horizontal position is set.
     *
     * @return The horizontal alignment where to paint the element. It is {@code null} if the horizontal position is set.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return hAlign;
    }

    /**
     * Returns the vertical position where to paint the element. It is {@code null} if the vertical alignment is set.
     *
     * @param surroundingAreaHeightSupplier The supplier for the height of the surrounding area. It's only needed if the y coordinate is specified via percentage.
     * @return The vertical position where to paint the element. It is {@code null} if the vertical alignment is set.
     */
    public Integer getYPos(IntSupplier surroundingAreaHeightSupplier) {
        if (yPos == null) {
            return null;
        }
        if (yPos.contains(PERCENT_SIGN)) {
            if (surroundingAreaHeightSupplier != null) {
                return calcProportionalValue(yPos, surroundingAreaHeightSupplier.getAsInt());
            } else {
                return 0;
            }
        } else {
            return HGBaseTools.toInt(yPos);
        }
    }

    /**
     * Sets the vertical position where to paint the element.
     *
     * @param yPos The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     */
    private void setYPos(String yPos) {
        vAlign = VerticalAlignment.valueOfSecure(yPos.toUpperCase());
        if (vAlign == null) {
            this.yPos = yPos;
        } else {
            this.yPos = null;
        }
    }

    /**
     * Returns the vertical alignment where to paint the element. It is {@code null} if the vertical alignment is set.
     *
     * @return The vertical alignment where to paint the element. It is {@code null} if the vertical alignment is set.
     */
    public VerticalAlignment getVerticalAlignment() {
        return vAlign;
    }
}
