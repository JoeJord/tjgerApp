package com.tjger.gui.completed.playingfield;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.game.completed.playingfield.SingleFieldConnection;
import com.tjger.gui.GamePanel;
import com.tjger.gui.completed.Part;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Polygon;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Paint utilities for a the playing field.
 *
 * @author hagru
 */
public class PlayingFieldPaintUtil {
    
    public static final Color DEFAULT_SELECTION_COLOR = new Color(255, 255, 255, 128);

    /**
     * Prevent instantiation of utility class.
     */
    private PlayingFieldPaintUtil() {
        super();
    }

    /**
     * Paints all single fields of the playing field using a single field painter.
     *
     * @param field the playing field, must not be null
     * @param painter the single field painter, must not be null
     * @param panel the game panel to paint the field, must not be null
     * @param g the graphics object, must not be null
     */
    public static void drawAllFields(PlayingField field, SingleFieldPainter painter, GamePanel panel, Canvas g) {
        for (SingleField sf : field.getFields()) {
            painter.drawSingleField(field, sf, panel, g);
        }
    }

    /**
     * Paints all single fields of the playing field with the given border and fill color.
     *
     * @param field the playing field, must not be null
     * @param border the border color, may be null
     * @param fill the fill color, may be null
     * @param panel the game panel to paint the field, must not be null
     * @param g the graphics object, must not be null
     */
    public static void drawAllFields(PlayingField field, Color border, Color fill, GamePanel panel, Canvas g) {
        SingleFieldPainter painter = new SingleFieldAreaPainter(border, fill);
        for (SingleField sf : field.getFields()) {
            painter.drawSingleField(field, sf, panel, g);
        }
    }

    /**
     * Paints a single field with the given border and fill color.
     *
     * @param playField the playing field, must not be null
     * @param field the single field to paint, must not be null
     * @param border the border color, may be null
     * @param fill the fill color, may be null
     * @param panel the game panel to paint the field, must not be null
     * @param g the graphics object, must not be null
     */
    public static void drawSingleField(PlayingField playField, SingleField field, Color border, Color fill, GamePanel panel, Canvas g) {
        SingleFieldPainter painter = new SingleFieldAreaPainter(border, fill);
        painter.drawSingleField(playField, field, panel, g);
    }
    
    /**
     * Draws the single field selected with a default selection color.
     * 
     * @param playField the playing field, must not be null
     * @param field the single field to paint
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawSingleFieldSelected(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        drawSingleFieldSelected(playField, field, DEFAULT_SELECTION_COLOR, panel, g);
    }

    /**
     * Draws the single field selected with the given selection color.
     * 
     * @param playField the playing field, must not be null
     * @param field the single field to paint
     * @param selection the color for the selection
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawSingleFieldSelected(PlayingField playField, SingleField field, Color selection, GamePanel panel, Canvas g) {
        drawSingleField(playField, field, null, selection, panel, g);
    }
    
    /**
     * Draws an image centered on the single field.
     * 
     * @param playField the playing field, must not be null
     * @param field the single field to draw the image on
     * @param img the image to draw
     * @param percentSize the zoom factor for the image in percent (full size is 100%)
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawSingleField(PlayingField playField, SingleField field, int percentSize, Bitmap img, GamePanel panel, Canvas g) {
        if (img != null) {
            Point center = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field));
            int diffX = (int)((img.getWidth() * percentSize) / 200.0);
            int diffY = (int)((img.getHeight() * percentSize) / 200.0);
            panel.drawImage(center.x - diffX, center.y - diffY, percentSize, img, g);
        }
    }
    
    /**
     * Draws a part centered on the single field.
     * 
     * @param playField the playing field, must not be null
     * @param field the single field to draw the image on
     * @param part the part to draw
     * @param percentSize the zoom factor for the image in percent (full size is 100%)
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawSingleField(PlayingField playField, SingleField field, int percentSize, Part part, GamePanel panel, Canvas g) {
        if (part != null) {
            Point center = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field));
            Bitmap img = part.getImage();
            int diffX = (int)((img.getWidth() * percentSize) / 200.0);
            int diffY = (int)((img.getHeight() * percentSize) / 200.0);
            panel.drawPart(center.x - diffX, center.y - diffY, percentSize, part, g);
        }
    }
    
    /**
     * Paints all connections between fields of the playing field using a connection painter.
     *
     * @param field the playing field, must not be null
     * @param painter the connection painter, must not be null
     * @param panel the game panel to paint the connections, must not be null
     * @param g the graphics object, must not be null
     */
    public static void drawAllConnections(PlayingField field, ConnectionPainter painter, GamePanel panel, Canvas g) {
        for (SingleFieldConnection connection : getConnectionsBetweenFields(field)) {
            painter.drawConnection(field, connection.getFirst(), connection.getSecond(), panel, g);
        }
    }

    /**
     * Paints all connections between fields of the playing field with the given color and thickness.
     *
     * @param field the playing field, must not be null
     * @param color the color of the connection line
     * @param size the thickness of the connection line
     * @param panel the game panel to paint the connections, must not be null
     * @param g the graphics object, must not be null
     */
    public static void drawAllConnections(PlayingField field, Color color, int size, GamePanel panel, Canvas g) {
        ConnectionPainter painter = new ConnectionLinePainter(color, size);
        drawAllConnections(field, painter, panel, g);
    }
    
    /**
     * Returns all connections that exists between fields, independent if the connection is only in one or
     * in both directions.
     * 
     * @param field the playing field, must not be null
     * @return an unmodifiable collection with with connections
     */
    public static Collection<SingleFieldConnection> getConnectionsBetweenFields(PlayingField field) {
        Collection<SingleFieldConnection> connections = new HashSet<>();
        for (SingleField sf1 : field.getFields()) {
            for (SingleField sf2 : field.getNeighbours(sf1)) {
                if (!connections.contains(new SingleFieldConnection(sf2, sf1))) {
                    connections.add(new SingleFieldConnection(sf1, sf2));
                }
            }
        }
        return Collections.unmodifiableCollection(connections);
    }
    
    /**
     * Draws a connection line between the two given field, it is not checked whether there is really a
     * connection defined.
     * 
     * @param playField the playing field containing the single fields
     * @param field1 the first field
     * @param field2 the second field
     * @param color the color of the line
     * @param size the thickness of the line, must be 1 or greater
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawConnectionLine(PlayingField playField, SingleField field1, SingleField field2,
                                          Color color, int size, GamePanel panel, Canvas g) {
        panel.changeColor(color);
        if (size == 1) {
            Point center1 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field1));
            Point center2 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field2));
            panel.drawLine(center1.x, center1.y, center2.x, center2.y, g);
        } else {
            Polygon polygon = getPolygonForConnection(playField, field1, field2, size);
            panel.drawPolygon(polygon, true, g);
        }
    }
    
    /**
     * Creates a polygon for the connection between the centers of the given field, it is not checked whether
     * there exists a real connection.
     * 
     * @param playField the playing field containing the single fields
     * @param field1 the first field
     * @param field2 the second field
     * @param size the thickness of the connection, must be 1 or greater
     * @return a polygon representing the connection
     */
    public static Polygon getPolygonForConnection(PlayingField playField, SingleField field1, SingleField field2, int size) {
        HGBaseTools.checkCondition(size >= 1, "The size of the connection must not lower than 1!");
        Point center1 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field1));
        Point center2 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(field2));
        boolean horizontal = (Math.abs(center1.y - center2.y) <= Math.abs(center1.x - center2.x));
        boolean vertical = !horizontal;
        int diff1 = size / 2;
        int diff2 = (HGBaseTools.isEven(size))? Math.max(diff1 - 1, 0) : diff1;
        int diffX1 = (horizontal)? 0 : diff1;
        int diffX2 = (horizontal)? 0 : diff2;
        int diffY1 = (vertical)? 0 : diff1;
        int diffY2 = (vertical)? 0 : diff2;
        List<Point> points = new ArrayList<>();
        points.add(new Point(center1.x - diffX1, center1.y - diffY1));
        points.add(new Point(center1.x + diffX2, center1.y + diffY2));
        points.add(new Point(center2.x + diffX2, center2.y + diffY2));
        points.add(new Point(center2.x - diffX1, center2.y - diffY1));
        return HGBaseGuiTools.createPolygon(points.toArray(new Point[points.size()]));
    }
    
    /**
     * Draws a connection arrow from the start to the target field, it is not checked whether there is really
     * a connection defined.
     * 
     * @param playField the playing field containing the single fields
     * @param start the start field of the arrow
     * @param target the target field of the arrow
     * @param color the color of the arrow
     * @param size the thickness of the arrow, must be 1 or greater
     * @param panel the game panel
     * @param g the graphics object
     */
    public static void drawConnectionArrow(PlayingField playField, SingleField start, SingleField target,
                                          Color color, int size, GamePanel panel, Canvas g) {
        drawConnectionLine(playField, start, target, color, size, panel, g);
        Point center1 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(start));
        Point center2 = HGBaseGuiTools.getCenterOfPolygon(playField.getPixelPositions(target));
        panel.changeColor(color);
        panel.drawArrow(center1.x, center1.y, center2.x, center2.y, (int)(size * 1.5f), g);
    }
    
}
