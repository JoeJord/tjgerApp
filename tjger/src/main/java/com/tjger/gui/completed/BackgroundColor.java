package com.tjger.gui.completed;

import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Specify a background color that can be modified by the player.<p>
 * There is at most one background color possible to be defined.
 *
 * @author hagru
 */
public class BackgroundColor extends Part {

    private Color defaultColor;

    public BackgroundColor(Color defaultColor, boolean hidden, boolean proTeaser) {
        super(ConstantValue.CONFIG_BACKCOLOR, "background_color", null, hidden, proTeaser);
        this.defaultColor = defaultColor;
    }

    /**
     * @return the default color
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * @return the active color
     */
    public Color getActiveColor() {
        if (HGBaseConfig.existsKey(ConstantValue.CONFIG_BACKCOLOR)) {
            return HGBaseConfig.getColor(ConstantValue.CONFIG_BACKCOLOR);
        } else {
            return getDefaultColor();
        }
    }

    @Override
    public boolean equals(Object o2) {
        return (HGBaseTools.equalClass(this, o2) && HGBaseTools.equalObject(getActiveColor(), ((BackgroundColor) o2).getActiveColor()));
    }

}
