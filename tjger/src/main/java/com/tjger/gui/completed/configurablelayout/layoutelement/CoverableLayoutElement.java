package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * Interface for elements of a configurable game panel which may be painted covered.
 */
public interface CoverableLayoutElement {
    /**
     * Returns {@code true} if the layout element should be painted covered.
     *
     * @return {@code true} if the layout element should be painted covered.
     */
    boolean isCovered();

    /**
     * Sets the information if the layout element should be painted covered.
     *
     * @param covered {@code true} if the layout element should be painted covered.
     */
    void setCovered(boolean covered);
}
