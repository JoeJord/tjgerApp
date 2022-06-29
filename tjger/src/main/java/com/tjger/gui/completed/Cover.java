package com.tjger.gui.completed;

import com.tjger.lib.ConstantValue;

import android.graphics.Bitmap;

/**
 * Covers for the cards.
 * 
 * @author hagru
 */
public class Cover extends Part {

    public Cover(String name, Bitmap image, boolean hidden) {
        super(ConstantValue.CONFIG_COVER, name, image, hidden);
    }

}
