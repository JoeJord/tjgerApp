package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * Interface for elements of a configurable game panel which may be indexed by the player index.
 */
public interface IndexedLayoutElement {
    /**
     * Sets the index of the player for which the layout element should be painted.
     *
     * @param playerIndex The index to set.
     */
    void setPlayerIndex(int playerIndex);

    /**
     * Returns the index of the player for which the layout element should be painted.
     *
     * @return The index of the player for which the layout element should be painted.
     */
    int getPlayerIndex();
}
