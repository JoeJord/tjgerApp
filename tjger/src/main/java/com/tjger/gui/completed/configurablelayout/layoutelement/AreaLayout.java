package com.tjger.gui.completed.configurablelayout.layoutelement;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Insets;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The layout configuration for an area.
 */
public class AreaLayout extends LayoutElement {
    /**
     * The name of the area.
     */
    private String name;
    /**
     * The width of the area.
     */
    private String width;
    /**
     * The height of the area.
     */
    private String height;
    /**
     * The top margin of the area.
     */
    private String marginTop;
    /**
     * The bottom margin of the area.
     */
    private String marginBottom;
    /**
     * The left margin of the area.
     */
    private String marginLeft;
    /**
     * The right margin of the area.
     */
    private String marginRight;
    /**
     * The flag if the area is hidden.
     */
    private boolean hidden;

    /**
     * Constructs a new instance.
     *
     * @param name         The name of the area.
     * @param xPos         The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos         The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param width        The width of the area. The value may be specified in pixels (as number) or as percentage of the game panel width (as number with following %).
     * @param height       The height of the area. The value may be specified in pixels (as number) or as percentage of the game panel height (as number with following %).
     * @param marginTop    The top margin for this area.
     * @param marginBottom The bottom margin for this area.
     * @param marginLeft   The left margin for this area.
     * @param marginRight  The right margin for this area.
     * @param hidden       The flag if the area is hidden.
     */
    public AreaLayout(String name, String xPos, String yPos, String width, String height, String marginTop, String marginBottom, String marginLeft, String marginRight, boolean hidden) {
        super(xPos, yPos);
        setName(name);
        setWidth(width);
        setHeight(height);
        setMarginTop(marginTop);
        setMarginBottom(marginBottom);
        setMarginLeft(marginLeft);
        setMarginRight(marginRight);
        setHidden(hidden);
    }

    @Override
    public String getElementKey() {
        return getName();
    }

    /**
     * Returns the name of the area.
     *
     * @return The name of the area.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the area.
     *
     * @param name The name of the area.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the width of the area.<br>
     * The value may be specified in pixels (as number) or as percentage of the game panel width (as number with following %).
     *
     * @param width The width of the area.
     */
    private void setWidth(String width) {
        this.width = width;
    }

    /**
     * Returns the width of the area.
     *
     * @param gamePanelWidthSupplier The supplier for the total width of the game field. It's only needed if the width is specified via percentage and not via a fixed value.
     * @return The width of the area.
     */
    public int getWidth(IntSupplier gamePanelWidthSupplier) {
        if (width.contains(PERCENT_SIGN)) {
            if (gamePanelWidthSupplier != null) {
                return calcProportionalValue(width, gamePanelWidthSupplier.getAsInt());
            } else {
                return 0;
            }
        } else {
            return HGBaseTools.toInt(width);
        }
    }

    /**
     * Returns the inner width of the area.<br>
     * The inner width is the width of the area minus the left and right margin.
     *
     * @param gamePanelWidthSupplier The supplier for the total width of the game field. It's only needed if the width or the margin is specified via percentage and not via a fixed value.
     * @return The inner width of the area.
     */
    public int getInnerWidth(IntSupplier gamePanelWidthSupplier) {
        return getWidth(gamePanelWidthSupplier) - getMarginLeft(gamePanelWidthSupplier) - getMarginRight(gamePanelWidthSupplier);
    }

    /**
     * Sets the height of the area.<br>
     * The value may be specified in pixels (as number) or as percentage of the game panel height (as number with following %).
     *
     * @param height The height of the area.
     */
    private void setHeight(String height) {
        this.height = height;
    }

    /**
     * Returns the height of the area.
     *
     * @param gamePanelHeightSupplier The Supplier for the total height of the game field. It's only needed if the height is specified via percentage and not via a fixed value.
     * @return The height of the area.
     */
    public int getHeight(IntSupplier gamePanelHeightSupplier) {
        if (height.contains(PERCENT_SIGN)) {
            if (gamePanelHeightSupplier != null) {
                return calcProportionalValue(height, gamePanelHeightSupplier.getAsInt());
            } else {
                return 0;
            }
        } else {
            return HGBaseTools.toInt(height);
        }
    }

    /**
     * Returns the inner height of the area.<br>
     * The inner height is the height of the area minus the top and bottom margin.
     *
     * @param gamePanelHeightSupplier The supplier for the total height of the game field. It's only needed if the height or the margin is specified via percentage and not via a fixed value.
     * @return The inner height of the area.
     */
    public int getInnerHeight(IntSupplier gamePanelHeightSupplier) {
        return getHeight(gamePanelHeightSupplier) - getMarginTop(gamePanelHeightSupplier) - getMarginBottom(gamePanelHeightSupplier);
    }

    /**
     * Returns the size of the area.
     *
     * @param gamePanelSizeSupplier The supplier for the total size of the game field. It's only needed if the width or the height is specified via percentage and not via fixed value.
     * @return The size of the area.
     */
    public Dimension getSize(Supplier<Dimension> gamePanelSizeSupplier) {
        return new Dimension(getWidth(() -> gamePanelSizeSupplier.get().width), getHeight(() -> gamePanelSizeSupplier.get().height));
    }

    /**
     * Returns the inner size of the area.<br>
     * The inner size is the size of the area minus the margins.
     *
     * @param gamePanelSizeSupplier The supplier for the total size of the game field. It's only needed if the margins or the area size is specified via percentage and not via a fixed value.
     * @return The inner size of the area.
     */
    public Dimension getInnerSize(Supplier<Dimension> gamePanelSizeSupplier) {
        return new Dimension(getInnerWidth(() -> gamePanelSizeSupplier.get().width), getInnerHeight(() -> gamePanelSizeSupplier.get().height));
    }

    /**
     * Sets the top margin of the area.
     *
     * @param marginTop The top margin of the area.
     */
    private void setMarginTop(String marginTop) {
        this.marginTop = marginTop;
    }

    /**
     * Returns the top margin of the area.
     *
     * @param gamePanelHeightSupplier The supplier for the total height of the game field. It's only needed if the margin or the height is specified via percentage and not via a fixed value.
     * @return The top margin of the area.
     */
    public int getMarginTop(IntSupplier gamePanelHeightSupplier) {
        if (marginTop.contains(PERCENT_SIGN)) {
            return calcProportionalValue(marginTop, getHeight(gamePanelHeightSupplier));
        } else {
            return HGBaseTools.toInt(marginTop, 0);
        }
    }

    /**
     * Sets the bottom margin of the area.
     *
     * @param marginBottom The bottom margin of the area.
     */
    private void setMarginBottom(String marginBottom) {
        this.marginBottom = marginBottom;
    }

    /**
     * Returns the bottom margin of the area.
     *
     * @param gamePanelHeightSupplier The supplier for the total height of the game field. It's only needed if the margin or the height is specified via percentage an not via a fixed value.
     * @return The bottom margin of the area.
     */
    public int getMarginBottom(IntSupplier gamePanelHeightSupplier) {
        if (marginBottom.contains(PERCENT_SIGN)) {
            return calcProportionalValue(marginBottom, getHeight(gamePanelHeightSupplier));
        } else {
            return HGBaseTools.toInt(marginBottom, 0);
        }
    }

    /**
     * Sets the left margin of the area.
     *
     * @param marginLeft The left margin of the area.
     */
    private void setMarginLeft(String marginLeft) {
        this.marginLeft = marginLeft;
    }

    /**
     * Returns the left margin of the area.
     *
     * @param gamePanelWidthSupplier The supplier for the total width of the game field. It's only needed if the margin or the width is specified via percentage and not via a fixed value.
     * @return The left margin of the area.
     */
    public int getMarginLeft(IntSupplier gamePanelWidthSupplier) {
        if (marginLeft.contains(PERCENT_SIGN)) {
            return calcProportionalValue(marginLeft, getWidth(gamePanelWidthSupplier));
        } else {
            return HGBaseTools.toInt(marginLeft, 0);
        }
    }

    /**
     * Sets the right margin of the area.
     *
     * @param marginRight The right margin of the area.
     */
    private void setMarginRight(String marginRight) {
        this.marginRight = marginRight;
    }

    /**
     * Returns the right margin of the area.
     *
     * @param gamePanelWidthSupplier The supplier for the total width of the game field. It's only needed if the margin or the width is specified via percentage and not via a fixed value.
     * @return The right margin of the area.
     */
    public int getMarginRight(IntSupplier gamePanelWidthSupplier) {
        if (marginRight.contains(PERCENT_SIGN)) {
            return calcProportionalValue(marginRight, getWidth(gamePanelWidthSupplier));
        } else {
            return HGBaseTools.toInt(marginRight, 0);
        }
    }

    /**
     * Returns the margin of the area.
     *
     * @param gamePanelSizeSupplier The supplier for the total size of the game field. It's only needed if the margins or the area size is specified via percentage and not via a fixed value.
     * @return The margin of the area.
     */
    public Insets getMargin(Supplier<Dimension> gamePanelSizeSupplier) {
        return new Insets(getMarginTop(() -> gamePanelSizeSupplier.get().height), getMarginLeft(() -> gamePanelSizeSupplier.get().width), getMarginBottom(() -> gamePanelSizeSupplier.get().height), getMarginRight(() -> gamePanelSizeSupplier.get().width));
    }

    /**
     * Returns the flag if the area is hidden.
     *
     * @return The flag if the area is hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the flag if the area is hidden.
     *
     * @param hidden The flag if the area is hidden.
     */
    private void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
