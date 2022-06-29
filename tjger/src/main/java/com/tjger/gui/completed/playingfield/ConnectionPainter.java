package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;

import android.graphics.Canvas;

/**
 * A painter for a connection between to fields.
 *
 * @author hagru
 */
public interface ConnectionPainter {

    /**
     * Draws a connection between two fields on the panel.
     *
     * @param playField the playing field containing the single fields
     * @param field1 the first field
     * @param field2 the second field
     * @param panel the panel to paint the connection on
     * @param g the graphics object
     */
    void drawConnection(PlayingField playField, SingleField field1, SingleField field2, GamePanel panel, Canvas g);

}
