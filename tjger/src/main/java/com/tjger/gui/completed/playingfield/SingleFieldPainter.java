package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;

import android.graphics.Canvas;

/**
 * A painter for a single field.
 *
 * @author hagru
 */
public interface SingleFieldPainter {

    /**
     * Draws a single field on the panel.
     *
     * @param playField the playing field where the single field is part of
     * @param field the field to paint
     * @param panel the panel to paint the field on
     * @param g the graphics object
     */
    void drawSingleField(PlayingField playField, SingleField field, GamePanel panel, Canvas g);

}
