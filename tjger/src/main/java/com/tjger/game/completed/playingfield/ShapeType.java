package com.tjger.game.completed.playingfield;

import android.graphics.Point;

/**
 * The shape of a single field.
 *
 * @author hagru
 */
public enum ShapeType {

    SQUARE,
    RECTANGLE,
    CIRCLE,
    ELLIPSE,
    POLYGON;


    /**
     * Tests whether the given array with pixel points is valid for the shape type.<p>
     * A square, rectangle, circle or ellipse must have two points whereas the first point is the top-left and
     * the second the bottom-right one. Additionally a square and a circle must have equal width and height.
     * A polygon must have at least three points.
     *
     * @param positions the positions to test, must not be null
     */
    public boolean validPositions(Point[] positions) {
       if (this.equals(POLYGON)) {
           return (positions.length >= 3);
       } else {
           if (positions.length == 2) {
               Point topLeft = positions[0];
               Point bottomRight = positions[1];
               return (topLeft.x < bottomRight.x && topLeft.y < bottomRight.y
                       && (this.equals(RECTANGLE) || this.equals(ELLIPSE)
                               || bottomRight.x - topLeft.x == bottomRight.y - topLeft.y));
           } else {
               return false;
           }
       }
    }
    
    /**
     * @return true if the shape is a square
     */
    public boolean isSquare() {
        return (SQUARE.equals(this));
    }

    /**
     * @return true if the shape is a rectangle
     */
    public boolean isRectange() {
        return (RECTANGLE.equals(this));
    }

    /**
     * @return true if the shape is a circle
     */
    public boolean isCircle() {
        return (CIRCLE.equals(this));
    }

    /**
     * @return true if the shape is an ellipse
     */
    public boolean isEllipse() {
        return (ELLIPSE.equals(this));
    }

    /**
     * @return true if the shape is a polygon
     */
    public boolean isPolygon() {
        return (POLYGON.equals(this));
    }
}
