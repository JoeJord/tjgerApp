package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * The layout configuration for a cardset.
 */
public class CardsetLayout extends LayoutGameElementSet {
    /**
     * The index of the player for which the cards should be painted.
     */
    private int playerIndex;

    /**
     * Constructs a new instance.
     *
     * @param type          The type of the cardset.
     * @param playerIndex   The index of the player for which the cards should be painted.
     * @param xPos          The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos          The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize   The percent size with which the cards should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param area          The area in which the cards should be painted.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param orientation   The orientation with which the cards should be painted. The value may be "ltr_down", "ltr_up", "down_ltr", "up_ltr", "rtl_down", "rtl_up", "down_rtl" or "up_rtl".
     * @param wrapThreshold The threshold at which a wrapping is done. Only needed if {@code orientation} is specified.
     */
    public CardsetLayout(String type, int playerIndex, String xPos, String yPos, String percentSize, AreaLayout area, int xSpacing, int ySpacing, String orientation, int wrapThreshold) {
        super(type, xPos, yPos, percentSize, area, xSpacing, ySpacing, orientation, wrapThreshold);
        setPlayerIndex(playerIndex);
    }

    @Override
    public String getElementKey() {
        return getType() + getPlayerIndex();
    }

    /**
     * Returns the index of the player for which the cards should be painted.
     *
     * @return The index of the player for which the cards should be painted.
     */
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Sets the index of the player for which the cards should be painted.
     *
     * @param playerIndex The index of the player for which the cards should be painted.
     */
    private void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }
}
