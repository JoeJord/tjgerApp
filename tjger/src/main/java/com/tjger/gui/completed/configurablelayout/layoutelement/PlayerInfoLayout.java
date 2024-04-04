package com.tjger.gui.completed.configurablelayout.layoutelement;

import com.tjger.gui.completed.configurablelayout.PlayerInfo;
import com.tjger.gui.completed.configurablelayout.ScaleType;

import java.util.function.Supplier;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.gui.StringSizeSupplier;

/**
 * The layout configuration for a player information.
 */
public class PlayerInfoLayout extends LayoutElement implements DisplayArea, IndexedLayoutElement {
    /**
     * The type of information which should be painted.
     */
    private PlayerInfo type;
    /**
     * The index of the player for which the information should be painted.
     */
    private int playerIndex;
    /**
     * The font size with which the information should be painted. It is {@code null} if the {@code scale} is set.
     */
    private String fontSize;
    /**
     * The scaling with which the information should be painted.<br>
     * It is {@code null} if the fixed {@code fontSize} is set.
     */
    private ScaleType scale;
    /**
     * The color with which the information should be painted.
     */
    private Color color;
    /**
     * The angle with which the information should be painted.
     */
    private double angle;
    /**
     * The area in which the element should be painted.
     */
    private AreaLayout area;

    /**
     * Constructs a new instance.
     *
     * @param type        The type of the player information. May be "name", "game_score", "round_score" or "turn_score".
     * @param playerIndex The index of the player for which the cards should be painted.
     * @param xPos        The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos        The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param fontSize    The font size with which the information should be painted. The value may be "scale", "scale_x" or "scale_y", a percentage (number with following %) or a number.
     * @param color       The color with which the information should be painted.
     * @param angle       The angle with which the information should be painted.
     * @param area        The area in which the information should be painted.
     */
    public PlayerInfoLayout(String type, int playerIndex, String xPos, String yPos, String fontSize, Color color, double angle, AreaLayout area) {
        super(xPos, yPos);
        setType(type);
        setPlayerIndex(playerIndex);
        setFontSize(fontSize);
        setColor(color);
        setAngle(angle);
        setArea(area);
    }

    @Override
    public String getElementKey() {
        return getType().name() + getPlayerIndex();
    }

    /**
     * Returns the type of the information which should be painted.
     *
     * @return The type of the information which should be painted.
     */
    public PlayerInfo getType() {
        return type;
    }

    /**
     * Sets the type of information which should be painted.
     *
     * @param type The type to set. May be "name", "game_score", "round_score" or "turn_score".
     */
    private void setType(String type) {
        this.type = PlayerInfo.valueOf((type != null) ? type.toUpperCase() : null);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    /**
     * Sets the font size with which the information should be painted. The value may be "scale", "scale_x" or "scale_y", a percentage (number with following %) or a number.
     *
     * @param fontSize The font size to set. The value may be "scale", "scale_x" or "scale_y", a percentage (number with following %) or a number.
     */
    private void setFontSize(String fontSize) {
        scale = ScaleType.valueOfSecure(fontSize.toUpperCase());
        this.fontSize = (scale == null) ? fontSize : null;
    }

    /**
     * Returns the font size so that the player information fits optimally into the specified area.<br>
     * If the player information should only fit horizontally, then -1 must be specified as height.<br>
     * On the other hand, if the player information should only fit vertically, then -1 must be specified as width.
     *
     * @param stringSizeSupplier The supplier which delivers the size of the player information.
     * @param maxHeight The height, the player information should not exceed. If -1, the height is not checked.
     * @param maxWidth The width, the player information should not exceed. If -1, the width is not checked.
     * @return The font size so that the player information fits optimally into the specified area.
     */
    private int findOptimalFontSize(StringSizeSupplier stringSizeSupplier, int maxHeight, int maxWidth) {
        if (stringSizeSupplier == null) {
            return 0;
        }
        int low = 0;
        int high = 1000;
        while (low <= high) {
            int mid = (low + high) / 2;
            Dimension stringSize = stringSizeSupplier.get(mid);
            // Check if the string is within the bounds. If a boundary is -1, then it is not relevant.
            if (((stringSize.width <= maxWidth) || (maxWidth == -1))
                    && ((stringSize.height <= maxHeight) || (maxHeight == -1))) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return high;
    }

    /**
     * Returns the font size with which the information should be painted if the font size is specified via scale.
     *
     * @param stringSizeSupplier The supplier which delivers the size of the player information.
     * @param areaSizeSupplier The supplier for the size of the area.
     * @return The font size with which the information should be painted if the font size is specified via scale.
     * @throws IllegalArgumentException if scale, {@code stringSizeSupplier} or {@code areaSizeSupplier} is not specified or if the scale type is invalid.
     */
    private Float getScaledFontSize(StringSizeSupplier stringSizeSupplier, Supplier<Dimension> areaSizeSupplier) throws IllegalArgumentException {
        if ((getScale() == null) || (stringSizeSupplier == null) || (areaSizeSupplier == null)) {
            throw new IllegalArgumentException();
        }
        Dimension areaSize = areaSizeSupplier.get();
        switch (getScale()) {
            case SCALE:
                // The player information should fit vertically and horizontally. So the area size is used as it is.
                return (float) findOptimalFontSize(stringSizeSupplier, areaSize.height, areaSize.width);
            case SCALE_X:
                // The player information should fit horizontally. So don't check the height (max. height = -1).
                return (float) findOptimalFontSize(stringSizeSupplier, -1, areaSize.width);
            case SCALE_Y:
                // The player information should fit vertically. So don't check the width (max. width = -1).
                return (float) findOptimalFontSize(stringSizeSupplier, areaSize.height, -1);
            default:
                throw new IllegalArgumentException("Invalid scale type " + getScale().name());
        }
    }

    /**
     * Returns the font size with which the information should be painted if the font size is specified via percentage.
     *
     * @param stringSizeSupplier The supplier which delivers the size of the player information.
     * @param areaSizeSupplier The supplier for the size of the area.
     * @return The font size with which the information should be painted if the font size is specified via percentage.
     * @throws IllegalArgumentException if {@code stringSiozeSupplier}, {@code areaSizeSupplier} or font size is not specified or the font size was not specified as percentage.
     */
    public float getPercentFontSize(StringSizeSupplier stringSizeSupplier, Supplier<Dimension> areaSizeSupplier) {
        if (stringSizeSupplier == null) {
            throw new IllegalArgumentException("The supplier for the string size must be specified!");
        }
        if (areaSizeSupplier == null) {
            throw new IllegalArgumentException("The supplier for the area size must be specified!");
        }
        if (fontSize == null) {
            throw new IllegalArgumentException("The font size was not set for the player information " + getElementKey() + "!");
        }
        if (!fontSize.contains(PERCENT_SIGN)) {
            throw new IllegalArgumentException("The font size was not specified as percentage for the player information " + getElementKey() + "!");
        }
        return findOptimalFontSize(stringSizeSupplier, calcProportionalValue(fontSize, areaSizeSupplier.get().height), -1);
    }

    /**
     * Returns the font size with which the information should be painted.
     *
     * @param stringSizeSupplier The supplier which delivers the size of the player information. It's only needed if the size of the information is specified via scale or in percentage and not fixed.
     * @param areaSizeSupplier The supplier for the size of the area. It's only needed if the size of the information is specified via scale or in percentage and not fixed.
     * @return The font size with which the information should be painted.
     */
    public Float getFontSize(StringSizeSupplier stringSizeSupplier, Supplier<Dimension> areaSizeSupplier) {
        try {
            return getScaledFontSize(stringSizeSupplier, areaSizeSupplier);
        } catch (Exception e) {
            // Font size not specified as scaling. Try next.
        }
        try {
            return getPercentFontSize(stringSizeSupplier, areaSizeSupplier);
        } catch (Exception e) {
            // Font size not specified as percentage. Try next.
        }
        if (fontSize != null) {
            return Float.valueOf(fontSize);
        }
        return null;
    }

    /**
     * Returns the scaling with which the information should be painted.<br>
     * It is {@code null} if the font size was set to a fixed value.
     *
     * @return The scaling with which the information should be painted.
     */
    public ScaleType getScale() {
        return scale;
    }

    /**
     * Returns the color with which the information should be painted.
     *
     * @return The color with which the information should be painted.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color with which the information should be painted.
     *
     * @param color The color to set.
     */
    private void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the angle with which the information should be painted.
     *
     * @return The angle with which the information should be painted.
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the angle with which the information should be painted.
     *
     * @param angle The angle to set.
     */
    private void setAngle(double angle) {
        this.angle = angle;
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
