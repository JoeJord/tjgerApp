package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutGameElementSet;

/**
 * The interface for all painters for an element set of a configurable layout which may be selected.
 *
 * @param <E> The type of the layout element.
 */
public interface ElementSetSelectionPainter<E extends LayoutGameElementSet> extends ElementSelectionPainter<E> {
    /**
     * Returns the index of the selected element in the set.
     *
     * @param element The layout element to paint.
     * @return The index of the selected element in the set.
     */
    default int getSelectedIndex(E element) {
        return 0;
    }
}
