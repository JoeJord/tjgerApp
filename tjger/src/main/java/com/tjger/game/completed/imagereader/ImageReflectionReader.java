package com.tjger.game.completed.imagereader;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.completed.ImageReflection;
import com.tjger.gui.completed.Part;

import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Read the reflection data for parts.
 *
 * @author hgr
 */
public class ImageReflectionReader extends AbstractImageEffectReader<ImageReflection> {

    /**
     * Create a new image shadow reader.
     */
    public ImageReflectionReader() {
        super(GameConfig.CONFIG_REFLECTION);
    }

    /* (non-Javadoc)
     * @see tjger.game.completed.imagereader.AbstractImageEffectReader#readEffectDefinition(java.lang.String)
     */
    @Override
    protected ImageReflection readEffectDefinition(String effectValue) {
        if (!effectValue.isEmpty()) {
            int gap = HGBaseTools.toInt(effectValue);
            if (HGBaseTools.isValid(gap)) {
                // first check for a single number as gap (and do not interpret 0 or 1 as boolean
                return new ImageReflection(gap);
            } else if (HGBaseTools.toBoolean(effectValue)) {
                // use a default reflection
                return new ImageReflection();
            } else {
                String[] data = effectValue.split(";");
                if (data.length == 3) {
                    int g = HGBaseTools.toInt(data[0]);
                    float h = HGBaseTools.toFloat(data[1]);
                    float a = HGBaseTools.toFloat(data[2]);
                    if (HGBaseTools.isValid(g) && HGBaseTools.isValid(h) && HGBaseTools.isValid(a)) {
                        return new ImageReflection(g, h, a);
                    }
                }
                HGBaseLog.logWarn("Invalid reflection detected for node " + getCurrentNode().getNodeName() + "!");
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.game.completed.imagereader.AbstractImageEffectReader#setEffectForPart(tjger.gui.completed.Part, tjger.gui.completed.ImageEffect)
     */
    @Override
    protected void setEffectForPart(Part part, ImageReflection effect) {
        part.setReflection(effect);
    }

    /* (non-Javadoc)
     * @see tjger.game.completed.imagereader.AbstractImageEffectReader#getEffectFromPart(tjger.gui.completed.Part)
     */
    @Override
    public ImageReflection getEffectFromPart(Part part) {
        return part.getReflection();
    }

}