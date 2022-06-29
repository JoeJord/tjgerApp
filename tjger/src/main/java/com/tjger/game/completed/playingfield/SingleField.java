package com.tjger.game.completed.playingfield;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.graphics.Point;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.SimpleHGBaseItem;

/**
 * A single field on the playing field.
 *
 * @author hagru
 */
public class SingleField extends SimpleHGBaseItem implements Comparable<SingleField> {

    private final ShapeType shape;
    private final Map<String,String> properties = new TreeMap<>();
    private Point gridPosition;
    private Point[] pixelPositions;
    private Object data;

    /**
     * Create a game field by setting all final fields.
     * 
     * @param id the id of the field
     * @param shape the shape of the single field
     */
    private SingleField(String id, ShapeType shape) {
        super(id);
        this.shape = shape;
    }

    /**
     * Create a new single field for a playing field with a grid.
     *
     * @param id the id of the field
     * @param shape the shape of the single field, a polygon is not allowed on a grid
     * @param gridPosition the position on the grid
     */
    public SingleField(String id, ShapeType shape, Point gridPosition) {
        this(id, shape);
        if (shape == ShapeType.POLYGON) {
            throw new IllegalArgumentException("A single field in a grid must not be a polygon!");
        }
        this.gridPosition = HGBaseTools.requireNonNull(gridPosition, "The grid position must not be null!");
    }

    /**
     * Create a new single field for a playing field without a grid by pixel positions.
     *
     * @param id the id of the field
     * @param shape the shape of the single field
     * @param pixelPositions an array with pixel positions on the playing field
     */
    public SingleField(String id, ShapeType shape, Point[] pixelPositions) {
        this(id, shape);
        checkValidPixelPositionsForShape(shape, pixelPositions);
        this.pixelPositions = HGBaseTools.clone(pixelPositions);
    }

    /**
     * Checks whether the pixel positions are valid for the given shape type and throws an Exception if not.
     *
     * @param shape the shape type
     * @param pixelPositions the pixel positions to check
     * @throws IllegalArgumentException if the shape type does not suite to the pixel positions
     */
    private void checkValidPixelPositionsForShape(ShapeType shape, Point[] pixelPositions) {
        if (!shape.validPositions(pixelPositions)) {
            throw new IllegalArgumentException("The shape type does not fit to the given points!");
        }
    }

    /**
     * The shape type of this field.
     *
     * @return the shape type of the field
     */
    public ShapeType getShape() {
        return shape;
    }
    
    /**
     * Returns an unmodifiable map with all properties of the field.
     * 
     * @return an unmodifiable map with all properties, may be empty
     */
    public Map<String,String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Returns all property keys of the field
     *
     * @return an unmodifiable set with the property keys of the field, can be empty
     */
    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }
    
    /**
     * Returns the property value for the given key.
     * 
     * @param key the property key
     * @return the value, may be an empty string
     */
    public String getProperty(String key) {
        return HGBaseTools.toString(properties.get(key));
    }

    /**
     * Sets an property for the field.<p>
     * If the property already exists, it will be overwritten. If the value is null or empty, the property
     * key will be removed.
     *
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(String key, String value) {
        if (HGBaseTools.hasContent(key)) {
            if (HGBaseTools.hasContent(value)) {
                this.properties.put(key, value);
            } else {
                this.properties.remove(key);
            }
        }
    }
    
    /**
     * Clears all properties of the this field.
     */
    public void clearProperties() {
        this.properties.clear();
    }

    /**
     * Assign an arbitrary data object to this single field.<p>
     * This object is not stored in the playing field file but only for temporary use.
     *
     * @param data the data object to assign to
     */
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * Clears the data object of this single field, i.e., sets it to null.
     */
    public void clearData() {
        setData(null);
    }

    /**
     * Returns the assigned data object.
     *
     * @return the assigned data object, may be null
     */
    public Object getData() {
        return this.data;
    }

    /**
     * Returns the grid position if this is a field on a playing field with grids.
     *
     * @return the grid position or null if the field is not on a grid playing field
     */
    public Point getGridPosition() {
        return gridPosition;
    }
    
    /**
     * Moves the field to a new grid position.
     * 
     * @param gridPosition the new grid position, must not be null
     */
    public void setGridPosition(Point gridPosition) {
        this.gridPosition = HGBaseTools.requireNonNull(gridPosition, "The grid position must not be null!");
    }

    /**
     * Returns the pixel points of this field, the number of points depends on the shape type.
     *
     * @return the pixel points of this field or null if the field is a grid playing field
     */
    public Point[] getPixelPositions() {
        return HGBaseTools.clone(pixelPositions);
    }

    /**
     * Set new pixel positions. This can be because of moving the single field or because of shwitching from
     * grid playing field to a non-grid playing field.
     *
     * @param pixelPositions the new pxiel positions
     * @throws IllegalArgumentException if the shape type does not suite to the pixel positions
     */
    public void setPixelPositions(Point[] pixelPositions) {
        checkValidPixelPositionsForShape(this.shape, pixelPositions);
        this.pixelPositions = HGBaseTools.clone(pixelPositions);
        this.gridPosition = null;
    }

    /**
     * Compares to single fields, fields are sorted grid row/column or x/y position.<p>
     * Changing from grid to a playing field without grid effects the method but the result should be the same.
     * However, changing the pixel position can change sort ordering.
     */
    @Override
    public int compareTo(SingleField field) {
        if (field == null) {
            return -1;
        }
        if (gridPosition != null) {
            return (field.gridPosition == null)? -1 : comparePoints(gridPosition, field.gridPosition);
        } else {
            Point pos1 = HGBaseTools.getElementOrNull(pixelPositions, 0);
            Point pos2 = HGBaseTools.getElementOrNull(field.pixelPositions, 0);
            return comparePoints(pos1, pos2);
        }
    }
    
    /* (non-Javadoc)
     * @see hgb.lib.internal.SimpleHGBaseItem#toString()
     */
    @Override
    public String toString() {
        return "[ID="+getId()+", Shape="+shape+", Properties="+properties+"]";
    }

    /**
     * Compares two points, first by x then by y coordinate.
     *
     * @param point1 the first point to compare
     * @param point2 the second point to compare
     * @return a comparison by x and y coordinate
     */
    private int comparePoints(Point point1, Point point2) {
        if (point1 == null && point2 == null) {
            return 0;
        } else if (point1 != null && point2 == null) {
            return -1;
        } else if (point1 == null && point2 != null) {
            return 1;
        } else {
            int res = Integer.valueOf(point1.x).compareTo(Integer.valueOf(point2.x));
            return (res != 0)? res : Integer.valueOf(point1.y).compareTo(Integer.valueOf(point2.y));
        }
    }

}
