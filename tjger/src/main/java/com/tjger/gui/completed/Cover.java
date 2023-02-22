package com.tjger.gui.completed;

import android.graphics.Bitmap;

import com.tjger.lib.ConstantValue;

/**
 * Covers for the cards.
 *
 * @author hagru
 */
public class Cover extends Part {

    public Cover(String name, Bitmap image, boolean hidden, boolean proTeaser) {
        super(ConstantValue.CONFIG_COVER, name, image, hidden, proTeaser);
    }

}
