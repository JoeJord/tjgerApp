package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;

import android.graphics.Canvas;
import at.hagru.hgbase.android.awt.Color;

/**
 * Paints a connection as an arrow with the given thickness and color.
 * 
 * @author hagru
 */
public class ConnectionArrowPainter implements ConnectionPainter {
    
    private final Color color;
    private final int size;

    /**
     * @param color the color of the line
     * @param size the size of the line
     */
    public ConnectionArrowPainter(Color color, int size ) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void drawConnection(PlayingField playField, SingleField field1, SingleField field2, GamePanel panel, Canvas g) {
        if (playField.getConnectionWeight(field1, field2) > 0) {
            PlayingFieldPaintUtil.drawConnectionArrow(playField, field1, field2, color, size, panel, g);            
        }
        if (playField.getConnectionWeight(field2, field1) > 0) {
            PlayingFieldPaintUtil.drawConnectionArrow(playField, field2, field1, color, size, panel, g);            
        }
    }

}
