package com.tjger.gui.completed.configurablelayout.layoutelement;

/**
 * Interface for elements of a configurable game panel which may be displayed in a specified area.
 */
public interface DisplayArea {
    /**
     * Returns the area in which an element should be painted.
     *
     * @return The area in which the element should be painted.
     */
    AreaLayout getArea();

    /**
     * Sets the area in which an element should be painted.
     *
     * @param area The area to set.
     */
    void setArea(AreaLayout area);
}
