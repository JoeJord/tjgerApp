package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.IndexedLayoutElement;
import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutGameElementSet;

/**
 * The interface for all painters for an element set of a configurable layout which may be indexed by the player index.
 *
 * @param <E> The type of the layout element.
 * @param <S> The type of the game panel element set.
 * @param <P> The type of the game panel element.
 * @param <G> The type of the game panel.
 */
public interface IndexedElementSetPainter<E extends LayoutGameElementSet, S extends PartSet, P extends Part, G extends SimpleGamePanel> extends ElementSetPainter<E, S, P, G> {
    /**
     * Returns the index of the player for which the specified element should be painted.
     *
     * @param element The layout element to paint.
     * @return The index of the player for which the specified element should be painted.
     */
    default int getPlayerIndex(E element) {
        return (element instanceof IndexedLayoutElement) ? ((IndexedLayoutElement) element).getPlayerIndex()
          : -1;
    }
}
