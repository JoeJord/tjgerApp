package com.tjger.gui.gamepanel.configurablelayout.painter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.configurablelayout.ScaleType;
import com.tjger.gui.completed.configurablelayout.layoutelement.AreaLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.PartLayout;
import com.tjger.lib.PartUtil;

import at.hagru.hgbase.android.awt.Dimension;

/**
 * The painter for an element of the type "Part" of a configurable layout.
 */
public class PartPainter implements ElementPainter<PartLayout, Part>, ElementSelectionPainter<PartLayout> {
    /**
     * The game panel where to paint.
     */
    private final SimpleGamePanel gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public PartPainter(SimpleGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public SimpleGamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    public Part getActiveElement(PartLayout element) {
        return GameConfig.getInstance().getActivePart(element.getType());
    }

    /**
     * Returns the size of the active configured game panel element which is painted.
     *
     * @param element The layout element to paint.
     * @return The size of the active configured game panel element which is painted.
     */
    protected Dimension getActiveElementSize(PartLayout element) {
        Bitmap image = getActiveElement(element).getImage();
        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    public int getPercentSize(PartLayout element) {
        return getPercentSize(element, getActiveElementSize(element));
    }

    /**
     * Returns the percent size with which the specified layout element should be painted.
     *
     * @param element     The layout element to paint.
     * @param elementSize The normal size of the element.
     * @return The percent size with which the specified layout element should be painted.
     */
    protected int getPercentSize(PartLayout element, Dimension elementSize) {
        AreaLayout area = element.getArea();
        Dimension areaSize = (area == null) ? getGamePanel().getFieldSize() : area.getInnerSize(() -> getGamePanel().getFieldSize());
        return element.getPercentSize(() -> elementSize, () -> areaSize);
    }

    @Override
    public ScaleType getScale(PartLayout element) {
        return element.getScale();
    }

    @Override
    public double getPaintAngle(PartLayout element) {
        return element.getAngle();
    }

    @Override
    public Dimension getPaintDimension(PartLayout element) {
        Dimension elementSize = getActiveElementSize(element);
        return new Dimension(calcPaintWidth(elementSize.width, getPercentSize(element, elementSize)), calcPaintHeight(elementSize.height, getPercentSize(element, elementSize)));
    }

    @Override
    public void paint(PartLayout element, Canvas g) {
        Point pos = getPaintPosition(element);
        int percentSize = getPercentSize(element);
        Part part = getActiveElement(element);
        getGamePanel().drawPart(pos.x, pos.y, percentSize, getPaintAngle(element), part, g);
        if (isSelected(element)) {
            PartUtil.drawSelectedPart(part, pos.x, pos.y, percentSize, getSelectionColor(element), getGamePanel(), g);
        }
    }
}
