package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutGameElement;

import at.hagru.hgbase.android.awt.Color;

/**
 * The interface for all painters for an element of a configurable layout which may be selected.
 *
 * @param <E> The type of the layout element.
 */
public interface ElementSelectionPainter<E extends LayoutGameElement> {
    /**
     * Returns {@code true} if the element is selected.
     *
     * @param element The layout element to paint.
     * @return {@code true} if the element is selected.
     */
    default boolean isSelected(E element) {
        return false;
    }

    /**
     * Returns the color with which the selection should be painted.
     *
     * @param element The layout element to paint.
     * @return The color with which the selection should be painted.
     */
    default Color getSelectionColor(E element) {
        return new Color(50, 250, 50, 150);
    }
}
