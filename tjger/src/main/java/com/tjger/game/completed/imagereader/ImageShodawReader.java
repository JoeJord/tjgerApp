package com.tjger.game.completed.imagereader;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.completed.ImageShadow;
import com.tjger.gui.completed.Part;

import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Read the shadow data for parts.
 *
 * @author hgr
 */
public class ImageShodawReader extends AbstractImageEffectReader<ImageShadow> {

    /**
     * Create a new image shadow reader.
     */
    public ImageShodawReader() {
        super(GameConfig.CONFIG_SHADOW);
    }

    /* (non-Javadoc)
    * @see tjger.game.completed.GameConfig.AbstractImageEffectReader#readEffectDefinition(java.lang.String)
    */
    @Override
    protected ImageShadow readEffectDefinition(String effectValue) {
        if (!effectValue.isEmpty()) {
            if (HGBaseTools.toBoolean(effectValue)) {
                // use a default shadow
                return new ImageShadow();
            } else {
                String[] data = effectValue.split(";");
                if (data.length > 1) {
                    int x = HGBaseTools.toInt(data[0]);
                    int y = HGBaseTools.toInt(data[1]);
                    float a = (data.length > 2) ? HGBaseTools.toFloat(data[2]) : HGBaseTools.INVALID_FLOAT;
                    if (x != HGBaseTools.INVALID_INT && y != HGBaseTools.INVALID_INT) {
                        return (!HGBaseTools.isValid(a)) ? new ImageShadow(x, y)
                                                        : new ImageShadow(x, y, a);
                    }
                }
                HGBaseLog.logWarn("Invalid shadow detected for node " + getCurrentNode().getNodeName() + "!");
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.game.completed.GameConfig.AbstractImageEffectReader#setEffectForPart(tjger.gui.completed.Part, tjger.gui.completed.ImageEffect)
     */
    @Override
    protected void setEffectForPart(Part part, ImageShadow effect) {
        part.setShadow(effect);
    }

    /* (non-Javadoc)
     * @see tjger.game.completed.GameConfig.AbstractImageEffectReader#getEffectFromPart(tjger.gui.completed.Part)
     */
    @Override
    public ImageShadow getEffectFromPart(Part part) {
        return part.getShadow();
    }

}