package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;

import android.graphics.Canvas;
import android.graphics.Point;
import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Polygon;
import at.hagru.hgbase.gui.HGBaseGuiTools;

/**
 * The default field painter draws a field with the given border and fill color taking the shape for the area to paint.
 *
 * @author hagru
 */
public class SingleFieldAreaPainter implements SingleFieldPainter {

    private final Color borderColor;
    private final Color fillColor;

    public SingleFieldAreaPainter(Color border, Color fill) {
        this.borderColor = border;
        this.fillColor = fill;
    }

    /**
     * Returns the border color.
     *
     * @return the border color, may be null
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Returns the fill color.
     *
     * @return the fill color, may be null
     */
    public Color getFillColor() {
        return fillColor;
    }

    @Override
    public void drawSingleField(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        switch (field.getShape()) {
            case SQUARE:    // paint a rectangle
            case RECTANGLE: drawRectangle(playField, field, panel, g);
                            break;
            case CIRCLE:    // paint an ellipse
            case ELLIPSE:   drawEllipse(playField, field, panel, g);
                            break;
            case POLYGON:   drawPolygon(playField, field, panel, g);
                            break;
            default:        throw new IllegalStateException("The given field has an unknown shape: " + field.getShape());
        }
    }

    /**
     * Draws a rectangle (or a square).
     *
     * @param playField the playing field
     * @param field the field to paint
     * @param panel the game panel
     * @param g the graphics object
     */
    protected void drawRectangle(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        Point[] positions = playField.getPixelPositions(field);
        RectangleDimension dim = RectangleDimension.fromPoints(positions);
        if (fillColor != null) {
            panel.changeColor(fillColor);
            panel.drawRect(dim.x, dim.y, dim.width, dim.height, true, g);
        }
        if (borderColor != null) {
            panel.changeColor(borderColor);
            panel.drawRect(dim.x, dim.y, dim.width, dim.height, false, g);
        }
    }

    /**
     * Draws an ellipse (or a circle).
     *
     * @param playField the playing field
     * @param field the field to paint
     * @param panel the game panel
     * @param g the graphics object
     */
    protected void drawEllipse(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        Point[] positions = playField.getPixelPositions(field);
        RectangleDimension dim = RectangleDimension.fromPoints(positions);
        if (fillColor != null) {
            panel.changeColor(fillColor);
            panel.drawOval(dim.x, dim.y, dim.width, dim.height, true, g);
        }
        if (borderColor != null) {
            panel.changeColor(borderColor);
            panel.drawOval(dim.x, dim.y, dim.width, dim.height, false, g);
        }
    }

    /**
     * Draws a polygon).
     *
     * @param playField the playing field
     * @param field the field to paint
     * @param panel the game panel
     * @param g the graphics object
     */
    protected void drawPolygon(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        Point[] positions = playField.getPixelPositions(field);
        Polygon polygon = HGBaseGuiTools.createPolygon(positions);
        if (fillColor != null) {
            panel.changeColor(fillColor);
            panel.drawPolygon(polygon, true, g);
        }
        if (borderColor != null) {
            panel.changeColor(borderColor);
            panel.drawPolygon(polygon, false, g);
        }
    }

    /**
     * Transforms two points into a rectangle dimension with top/left coordinate and width/height
     */
    protected static class RectangleDimension {

        int x;
        int y;
        int width;
        int height;

        private RectangleDimension() {
            super();
        }

        public static RectangleDimension fromPoints(Point[] positions) {
            if (positions.length == 2) {
                RectangleDimension dim = new RectangleDimension();
                dim.x = positions[0].x;
                dim.y = positions[0].y;
                dim.width = positions[1].x - positions[0].x + 1;
                dim.height = positions[1].y - positions[0].y + 1;
                return dim;
            } else {
                throw new IllegalStateException("The pixel positions do not suite to the shape type of the field!");
            }
        }

        /**
         * @return the left coordinate
         */
        public int getX() {
            return x;
        }

        /**
         * @return the top coordinate
         */
        public int getY() {
            return y;
        }

        /**
         * @return the width of the rectangle
         */
        public int getWidth() {
            return width;
        }

        /**
         * @return the height of the rectangle
         */
        public int getHeight() {
            return height;
        }

    }

}
