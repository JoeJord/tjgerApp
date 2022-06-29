package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Draws a single field by putting an image in the center of the field.
 * 
 * @author hagru
 */
public class SingleFieldImagePainter implements SingleFieldPainter {
    
    protected Bitmap image;
    protected int percentSize;

    /**
     * @param image the image to paint for all fields
     * @param percentSize the zoom factor for the image in percent, 0 for automatic zoom
     */
    public SingleFieldImagePainter(Bitmap image, int percentSize) {
        this.image = image;
    }

    @Override
    public void drawSingleField(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        if (image != null) {
            int zoom = (percentSize > 0)? percentSize
                                        : calculateZoomFactor(playField, field, image);
            PlayingFieldPaintUtil.drawSingleField(playField, field, zoom, image, panel, g);
        }

    }

    /**
     * Calculates the zoom factor based on the size of the image and of the single field.
     * 
     * @param playField the playing field, must not  be null
     * @param singleField the single field to paint the image on
     * @param image the image to paint
     * @return the zoom factor or 0 if the image is null
     */
    protected int calculateZoomFactor(PlayingField playField, SingleField field, Bitmap image) {
        if (image == null) {
            return 0;
        } else {
            Point[] pixels = playField.getPixelPositions(field);
            int minX = -1, minY = -1, maxX = -1, maxY = -1;
            for (Point p : pixels) {
                minX = (minX == -1 || minX > p.x)? p.x : minX;
                minY = (minY == -1 || minY > p.y)? p.y : minY;
                maxX = (maxX == -1 || maxX < p.x)? p.x : maxX;
                maxY = (maxY == -1 || maxY < p.y)? p.y : maxY;
            }
            double zoomX = (maxX - minX + 1.0) / image.getWidth();
            double zoomY = (maxY - minY + 1.0) / image.getHeight();
            return (int) (Math.min(zoomX, zoomY) * 100);
        }
    }

}
