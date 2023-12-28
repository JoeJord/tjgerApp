package com.tjger.gui.gamepanel.configurablelayout.painter;

import android.graphics.Canvas;
import android.graphics.Point;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.configurablelayout.ScaleType;
import com.tjger.gui.completed.configurablelayout.layoutelement.AreaLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.DisplayArea;
import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutElement;
import com.tjger.gui.gamepanel.configurablelayout.ConfigurableGamePanel;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Insets;
import at.hagru.hgbase.android.awt.Rectangle;

/**
 * The interface for all painters for an element of a configurable layout.
 *
 * @param <E> The type of the layout element.
 * @param <P> The type of the game panel element.
 */
public interface ElementPainter<E extends LayoutElement, P extends Part> {
    /**
     * Returns the game panel.
     *
     * @return The game panel.
     */
    SimpleGamePanel getGamePanel();

    /**
     * Returns the active configured game panel element which is painted.
     *
     * @param element The layout element to paint.
     * @return The active configured game panel element which is painted.
     */
    P getActiveElement(E element);

    /**
     * Returns {@code true} if the specified layout element should be painted.
     *
     * @param element The layout element to paint.
     * @return {@code true} if the specified layout element should be painted.
     */
    default boolean shouldPaint(E element) {
        return true;
    }

    /**
     * Returns the percent size with which the specified layout element should be painted.
     *
     * @param element The layout element to paint.
     * @return The percent size with which the specified layout element should be painted.
     */
    int getPercentSize(E element);

    /**
     * Returns the scaling with which the specified element should be painted.
     *
     * @param element The layout element to paint.
     * @return The scaling with which the specified element should be painted.
     */
    ScaleType getScale(E element);

    /**
     * Returns {@code true} if the specified element used scaling.
     *
     * @param element The layout element to paint.
     * @return {@code true} if the specified element used scaling.
     */
    default boolean isScaleUsed(E element) {
        return getScale(element) != null;
    }

    /**
     * Returns the scaled width which is needed to paint the layout element.
     *
     * @param elementWidth The unscaled width of the layout element.
     * @param percentSize  The percent size with which the layout element should be painted.
     * @return The scaled width which is needed to paint the layout element.
     */
    default int calcPaintWidth(int elementWidth, int percentSize) {
        double factor = percentSize / 100.0;
        return (int) (elementWidth * factor);
    }

    /**
     * Returns the scaled height which is needed to paint the layout element.
     *
     * @param elementHeight The unscaled height of the layout element.
     * @param percentSize   The percent size with which the layout element should be painted.
     * @return The scaled height which is needed to paint the layout element.
     */
    default int calcPaintHeight(int elementHeight, int percentSize) {
        double factor = percentSize / 100.0;
        return (int) (elementHeight * factor);
    }

    /**
     * Returns the dimension which is needed to paint the specified element.
     *
     * @param element The layout element to paint.
     * @return The dimension which is needed to paint the specified element.
     */
    Dimension getPaintDimension(E element);

    /**
     * Returns the surrounding area for the specified element.
     *
     * @param element The layout element to paint.
     * @return The surrounding area for the specified element.
     */
    default AreaLayout getSurroundingArea(E element) {
        return (element instanceof DisplayArea) ? ((DisplayArea) element).getArea() : null;
    }

    /**
     * Returns the margin for the specified element in the area where the element is painted.
     *
     * @param element The layout element to paint.
     * @return The margin for the specified element in the area where the element is painted.
     */
    default Insets getMargin(E element) {
        AreaLayout area = getSurroundingArea(element);
        return (area != null) ? area.getMargin(() -> getGamePanel().getFieldSize()) : GameConfig.getInstance().getGamefieldLayoutMargin();
    }

    /**
     * Returns the position of the surrounding area.
     *
     * @param element The layout element to paint.
     * @return The position of the surrounding area.
     */
    default Point getSurroundingAreaPosition(E element) {
        AreaLayout area = getSurroundingArea(element);
        return (area != null) ? ((ConfigurableGamePanel) getGamePanel()).getAreaPainter().getPaintPosition(area) : new Point(0, 0);
    }

    /**
     * Returns the width of the surrounding area.
     *
     * @param element The layout element to paint.
     * @return The width of the surrounding area.
     */
    default int getSurroundingAreaWidth(E element) {
        AreaLayout area = getSurroundingArea(element);
        return (area != null) ? area.getWidth(() -> getGamePanel().getFieldWidth()) : getGamePanel().getFieldWidth();
    }

    /**
     * Returns the height of the surrounding area.
     *
     * @param element The layout element to paint.
     * @return The height of the surrounding area.
     */
    default int getSurroundingAreaHeight(E element) {
        AreaLayout area = getSurroundingArea(element);
        return (area != null) ? area.getHeight(() -> getGamePanel().getFieldHeight()) : getGamePanel().getFieldHeight();
    }

    /**
     * Returns the dimension of the area that surrounds the specified element.<br>
     * This is either the defined area in which the element should be painted or the game panel if no area is defined.
     *
     * @param element The layout element to paint.
     * @return The dimension of the area that surrounds the specified element.
     */
    default Dimension getSurroundingAreaDimension(E element) {
        AreaLayout area = getSurroundingArea(element);
        return (area != null) ? area.getInnerSize(() -> getGamePanel().getFieldSize()) : getGamePanel().getFieldSize();
    }

    /**
     * Returns the x coordinate of the position where to paint the specified layout element.
     *
     * @param element The layout element to paint.
     * @return The x coordinate of the position where to paint the specified layout element.
     */
    default int calcHorizontalPosition(E element) {
        Insets margin = getMargin(element);
        Point areaPosition = getSurroundingAreaPosition(element);
        Integer xPos = element.getXPos(() -> getSurroundingAreaWidth(element));
        if (xPos != null) {
            return xPos + areaPosition.x + margin.left;
        }
        Dimension dimension = getPaintDimension(element);
        return areaPosition.x + element.getHorizontalAlignment().getXPosition(dimension.width, getSurroundingAreaWidth(element), margin.left, margin.right);
    }

    /**
     * Returns the y coordinate of the position where to paint the specified layout element.
     *
     * @param element The layout element to paint.
     * @return The y coordinate of the position where to paint the specified layout element.
     */
    default int calcVerticalPosition(E element) {
        Insets margin = getMargin(element);
        Point areaPosition = getSurroundingAreaPosition(element);
        Integer yPos = element.getYPos(() -> getSurroundingAreaHeight(element));
        if (yPos != null) {
            return yPos + areaPosition.y + margin.top;
        }
        Dimension dimension = getPaintDimension(element);
        return areaPosition.y + element.getVerticalAlignment().getYPosition(dimension.height, getSurroundingAreaHeight(element), margin.top, margin.bottom);
    }

    /**
     * Returns the position where to paint the specified layout element.
     *
     * @param element The layout element to paint.
     * @return The position where to paint the specified layout element.
     */
    default Point getPaintPosition(E element) {
        return new Point(calcHorizontalPosition(element), calcVerticalPosition(element));
    }

    /**
     * Returns the area which is needed to paint the specified element.
     *
     * @param element The layout element to paint.
     * @return The area which is needed to paint the specified element.
     */
    default Rectangle getPaintArea(E element) {
        return new Rectangle(getPaintPosition(element), getPaintDimension(element));
    }

    /**
     * Paints the specified element.
     *
     * @param element The layout element to paint.
     * @param g       The canvas where to draw.
     */
    void paint(E element, Canvas g);
}
