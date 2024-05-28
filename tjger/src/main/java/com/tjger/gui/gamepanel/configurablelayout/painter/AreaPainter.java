package com.tjger.gui.gamepanel.configurablelayout.painter;

import android.graphics.Canvas;
import android.graphics.Point;

import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.configurablelayout.ScaleType;
import com.tjger.gui.completed.configurablelayout.layoutelement.AreaLayout;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Dimension;

/**
 * The painter for an element of the type "Area" of a configurable layout.
 */
public class AreaPainter implements ElementPainter<AreaLayout, Part> {
    /**
     * The game panel where to paint.
     */
    private final SimpleGamePanel gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public AreaPainter(SimpleGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public SimpleGamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    public Part getActiveElement(AreaLayout element) {
        throw new UnsupportedOperationException("There are no active gamefield elements for an area!");
    }

    @Override
    public boolean shouldPaint(AreaLayout element) {
        return (ElementPainter.super.shouldPaint(element)) && (!element.isHidden());
    }

    @Override
    public int getPercentSize(AreaLayout element) {
        return 100;
    }

    @Override
    public ScaleType getScale(AreaLayout element) {
        return null;
    }

    @Override
    public double getPaintAngle(AreaLayout element) {
        return 0;
    }

    @Override
    public Dimension getPaintDimension(AreaLayout element) {
        return new Dimension(element.getWidth(() -> getGamePanel().getFieldWidth()), element.getHeight(() -> getGamePanel().getFieldHeight()));
    }

    /**
     * Returns the color in which the area frame is painted.
     *
     * @param element The layout element to paint.
     * @return The color in which the area frame is painted.
     */
    protected Color getColor(AreaLayout element) {
        return Color.BLACK;
    }

    @Override
    public void paint(AreaLayout element, Canvas g) {
        Point pos = getPaintPosition(element);
        Dimension dimension = getPaintDimension(element);
        getGamePanel().changeColor(getColor(element));
        getGamePanel().drawRect(pos.x, pos.y, dimension.width, dimension.height, false, g);
    }
}
