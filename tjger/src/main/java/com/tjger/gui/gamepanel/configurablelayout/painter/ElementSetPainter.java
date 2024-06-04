package com.tjger.gui.gamepanel.configurablelayout.painter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.tjger.gui.Orientation;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.configurablelayout.ScaleType;
import com.tjger.gui.completed.configurablelayout.layoutelement.CoverableLayoutElement;
import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutGameElementSet;
import com.tjger.lib.PartUtil;

import at.hagru.hgbase.android.awt.Dimension;

/**
 * The interface for all painters for a set of elements of a configurable layout.
 *
 * @param <E> The type of the layout element.
 * @param <S> The type of the game panel element set.
 * @param <P> The type of the game panel element.
 * @param <G> The type of the game panel.
 */
public interface ElementSetPainter<E extends LayoutGameElementSet, S extends PartSet, P extends Part, G extends SimpleGamePanel> extends ElementPainter<E, S, G>, ElementSetSelectionPainter<E> {
    /**
     * Returns the elements of the active set with which the specified layout element should be painted.
     *
     * @param element The layout element to paint.
     * @return The elements of the active set with which the specified layout element should be painted.
     */
    P[] getActiveElements(E element);

    /**
     * Returns the drawing spacing for the parts of the layout element.<br>
     * The width of the returned dimension is the horizontal spacing, the height the vertical spacing.
     *
     * @param element The layout element to paint.
     * @return The drawing spacing for the parts of the layout element.
     */
    default Dimension getPaintSpacing(E element) {
        return element.getSpacing();
    }

    /**
     * Returns the orientation with which the elements in the set should be painted.
     *
     * @param element The layout element to paint.
     * @return The orientation with which the elements in the set should be painted.
     */
    default Orientation getPaintOrientation(E element) {
        return element.getOrientation();
    }

    /**
     * Returns the threshold at which a wrapping is done.
     *
     * @param element The layout element to paint
     * @return The threshold at which a wrapping is done.
     */
    default int getPaintWrapThreshold(E element) {
        return element.getWrapThreshold();
    }

    @Override
    default int getPercentSize(E element) {
        return element.getPercentSize(() -> getUnscaledPaintDimension(element), () -> getSurroundingAreaDimension(element));
    }

    @Override
    default ScaleType getScale(E element) {
        return element.getScale();
    }

    @Override
    default double getPaintAngle(E element) {
        return element.getAngle();
    }

    /**
     * Returns the dimension which is needed to paint the specified element unscaled.
     *
     * @param element The layout element to paint.
     * @return The dimension which is needed to paint the specified element unscaled.
     */
    private Dimension getUnscaledPaintDimension(E element) {
        return getPercentagePaintDimension(element, 100);
    }

    /**
     * Returns the dimension which is needed to paint the specified element with the specified size (as percent - 100 is normal size).
     *
     * @param element     The layout element to paint.
     * @param percentSize The size in percent (100 is normal size).
     * @return The dimension which is needed to paint the specified element with the specified size.
     */
    private Dimension getPercentagePaintDimension(E element, int percentSize) {
        P[] elements = getActiveElements(element);
        Dimension spacing = getPaintSpacing(element);
        int width = 0;
        int height = 0;
        for (int elementIndex = 0; elementIndex < elements.length; elementIndex++) {
            Bitmap image = elements[elementIndex].getImage();
            width = Math.max(width, calcPaintWidth(image.getWidth(), percentSize) + (elementIndex * spacing.width));
            height = Math.max(height, calcPaintHeight(image.getHeight(), percentSize) + (elementIndex * spacing.height));
        }
        return new Dimension(width, height);
    }

    @Override
    default Dimension getPaintDimension(E element) {
        if (isScaleUsed(element)) {
            // If a scale is specified, then the paint dimension is the surrounding area (or the game field).
            return getSurroundingAreaDimension(element);
        } else {
            return getPercentagePaintDimension(element, getPercentSize(element));
        }
    }

    /**
     * Returns {@code true} if the specified layout element should be painted covered.
     *
     * @param element The layout element to paint.
     * @return {@code true} if the specified layout element should be painted covered.
     */
    default boolean isCovered(E element) {
        return ((element instanceof CoverableLayoutElement) && (((CoverableLayoutElement) element).isCovered()));
    }

    /**
     * Returns the cover parts if the parts should be painted covered or the specified parts otherwise.
     *
     * @param element The layout element to paint.
     * @param parts   The parts to paint.
     * @return The cover parts if the parts should be painted covered or the specified parts otherwise.
     */
    @SuppressWarnings("unchecked")
    default P[] getCovers(E element, P[] parts) {
        if (isCovered(element) && parts instanceof Card[]) {
            return (P[]) PartUtil.getCovers((Card[]) parts);
        } else {
            return parts;
        }
    }

    @Override
    default void paint(E element, Canvas g) {
        Point pos = getPaintPosition(element);
        int percentSize = getPercentSize(element);
        Dimension spacing = getPaintSpacing(element);
        P[] parts = getCovers(element, getActiveElements(element));
        if (isScaleUsed(element)) {
            double factor = percentSize / 100.0;
            spacing = new Dimension((int) (spacing.width * factor), (int) (spacing.height * factor));
        }
        Orientation orientation = getPaintOrientation(element);
        if (orientation != null) {
            getGamePanel().drawParts(pos.x, pos.y, percentSize, getPaintAngle(element), orientation, getPaintWrapThreshold(element), parts, spacing.width, spacing.height, g);
        } else {
            getGamePanel().drawParts(pos.x, pos.y, percentSize, getPaintAngle(element), parts, spacing.width, spacing.height, g);
        }
        if (isSelected(element)) {
            PartUtil.drawSelectedPart(getSelectedIndex(element), parts, pos.x, pos.y, percentSize, spacing.width, spacing.height, getSelectionColor(element), getGamePanel(), g);
        }
    }
}
