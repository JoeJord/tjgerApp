package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * The layout configuration for a cardset.
 */
public class CardsetLayout extends LayoutGameElementSet implements IndexedLayoutElement, CoverableLayoutElement {
    /**
     * The index of the player for which the cards should be painted.
     */
    private int playerIndex;
    /**
     * The flag if the cards should be painted covered.
     */
    private boolean covered;

    /**
     * Constructs a new instance.
     *
     * @param type          The type of the cardset.
     * @param playerIndex   The index of the player for which the cards should be painted.
     * @param xPos          The x position. The value may be "left", "center" or "right", a percentage (number with following %) or a number.
     * @param yPos          The y position. The value may be "top", "middle" or "bottom", a percentage (number with following %) or a number.
     * @param percentSize   The percent size with which the cards should be painted. May be "scale", "scale_x" or "scale_y" or a number.
     * @param angle         The angle with which the cards should be painted.
     * @param area          The area in which the cards should be painted.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param orientation   The orientation with which the cards should be painted. The value may be "ltr_down", "ltr_up", "down_ltr", "up_ltr", "rtl_down", "rtl_up", "down_rtl" or "up_rtl".
     * @param wrapThreshold The threshold at which a wrapping is done. Only needed if {@code orientation} is specified.
     * @param covered       The flag if the cards should be painted covered.
     */
    public CardsetLayout(String type, int playerIndex, String xPos, String yPos, String percentSize, double angle, AreaLayout area, int xSpacing, int ySpacing, String orientation, int wrapThreshold, boolean covered) {
        super(type, xPos, yPos, percentSize, angle, area, xSpacing, ySpacing, orientation, wrapThreshold);
        setPlayerIndex(playerIndex);
        setCovered(covered);
    }

    @Override
    public String getElementKey() {
        return getType() + getPlayerIndex();
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public boolean isCovered() {
        return covered;
    }

    @Override
    public void setCovered(boolean covered) {
        this.covered = covered;
    }
}
