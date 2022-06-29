package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.SingleField;
import com.tjger.gui.GamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;

import android.graphics.Canvas;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Draws a single field by putting an image in the center of the field. <p>
 * This image is given field property that is the name of a part set while the value is the
 * particular part for the image (the first part with the name is taken, the sequence is ignored).
 * 
 * @author hagru
 */
public class SingleFieldPartPainter extends SingleFieldImagePainter {
    
    private String partSet;
    private String defaultPart;

    /**
     * @param partSet the name of the part set that is also a property of the field
     * @param percentSize the zoom factor for the images in percent, 0 for automatic zoom
     */
    public SingleFieldPartPainter(String partSet, int percentSize) {
        this(partSet, null, percentSize);
    }

    /**
     * @param partSet the name of the part set that is also a property of the field
     * @param defaultPart the name of the part to take if the property is not set for a field
     * @param percentSize the zoom factor for the images in percent, 0 for automatic zoom
     */
    public SingleFieldPartPainter(String partSet, String defaultPart, int percentSize) {
        super(null, percentSize);
        this.partSet = partSet;
        this.defaultPart = defaultPart;
    }
    
    @Override
    public void drawSingleField(PlayingField playField, SingleField field, GamePanel panel, Canvas g) {
        String property = field.getProperty(partSet);
        String partName = HGBaseTools.hasContent(property)? property : defaultPart;
        if (HGBaseTools.hasContent(partName)) {
            PartSet parts = GameManager.getInstance().getGameConfig().getActivePartSet(partSet);
            Part part = HGBaseTools.getFirstOrNull(parts.getParts(partName));
            if (part != null) {
                int zoom = (percentSize > 0)? percentSize
                                            : calculateZoomFactor(playField, field, part.getImage());
                PlayingFieldPaintUtil.drawSingleField(playField, field, zoom, part, panel, g);
            } else {
                HGBaseLog.logWarn("The part '" + partName + "' was not found in part set '" + partSet + "'!");
            }
        } else {
            HGBaseLog.logWarn("No part information found for field " + field.getId());
        }

    }

}
