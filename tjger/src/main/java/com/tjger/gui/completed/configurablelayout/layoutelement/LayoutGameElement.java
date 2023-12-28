package com.tjger.gui.completed.configurablelayout.layoutelement;

import com.tjger.gui.completed.configurablelayout.ScaleType;

import java.util.function.Supplier;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A game element of a configurable layout.<br>
 * A game element is an element which is used to play the game.
 */
public abstract class LayoutGameElement extends LayoutElement implements DisplayArea {
    /**
     * The type name of the game element in the tjger configuration.
     */
    private String type;
    /**
     * The fixed percent size with which the element should be painted.<br>
     * It is {@code null} if the {@code scale} is set.
     */
    private Integer percentSize;
    /**
     * The scaling with which the element should be painted.<br>
     * It is {@code null} if the fixed {@code percentSize} is set.
     */
    private ScaleType scale;
    /**
     * The area in which the element should be painted.
     */
    private AreaLayout area;

    /**
     * Constructs a new instance.
     *
     * @param type        The type name of the game element in the tjger configuration.
     * @param xPos        The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos        The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize The percent size with which the element should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param area        The area in which the element should be painted.
     */
    protected LayoutGameElement(String type, String xPos, String yPos, String percentSize, AreaLayout area) {
        super(xPos, yPos);
        setType(type);
        setPercentSize(percentSize);
        setArea(area);
    }

    /**
     * Returns the type name of the game element in the tjger configuration.
     *
     * @return The type name of the game element in the tjger configuration.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type name of the game element in the tjger configuration.
     *
     * @param type The type to set.
     */
    private void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the percent size with which the element should be painted.
     *
     * @param percentSize The percent size to set. May be "scale", "scale_x" or "scale_y" or a number.
     */
    private void setPercentSize(String percentSize) {
        scale = ScaleType.valueOfSecure(percentSize.toUpperCase());
        if (scale == null) {
            this.percentSize = HGBaseTools.toInt(percentSize);
            if ((this.percentSize == HGBaseTools.INVALID_INT) || (this.percentSize < 0)) {
                this.percentSize = 100;
            }
        } else {
            this.percentSize = null;
        }
    }

    /**
     * Returns the percent size with which the element should be painted.
     *
     * @param elementSizeSupplier The supplier for the normal size of the element. It's only needed if the size of the elements is specified via scale and not via percentage.
     * @param areaSizeSupplier    The supplier for the size of the area. It's only needed if the size of the elements is specified via scale and not via percentage.
     * @return The percent size with which the element should be painted.
     */
    public int getPercentSize(Supplier<Dimension> elementSizeSupplier, Supplier<Dimension> areaSizeSupplier) {
        if (percentSize != null) {
            return percentSize;
        } else if ((elementSizeSupplier == null) || (areaSizeSupplier == null)) {
            return 100;
        }
        return scale.getPercentSize(elementSizeSupplier.get(), areaSizeSupplier.get());
    }

    /**
     * Returns the scaling with which the element should be painted.<br>
     * It is {@code null} if the percent size was set to a fixed value.
     *
     * @return The scaling with which the element should be painted.
     */
    public ScaleType getScale() {
        return scale;
    }

    @Override
    public AreaLayout getArea() {
        return area;
    }

    @Override
    public void setArea(AreaLayout area) {
        this.area = area;
    }
}
