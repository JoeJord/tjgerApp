package com.tjger.gui.completed;

import com.tjger.lib.ConstantValue;

import android.graphics.Bitmap;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A background image for the game.
 *
 * @author hagru
 */
public class Background extends Part {

    private boolean repeatMode;
    private boolean ignoreZoom;
    private int zoom;

    public Background(String name, Bitmap image, boolean repeat, boolean ignoreZoom, boolean hidden, int zoom) {
        super(ConstantValue.CONFIG_BACKGROUND, name, image, hidden);
        this.repeatMode = repeat;
        this.ignoreZoom = ignoreZoom;
        this.zoom = (zoom>0)? zoom : 100;
    }
    
    /* (non-Javadoc)
     * @see tjger.gui.completed.Part#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        return (HGBaseTools.equalClass(this, o2) && this.toString().equals(o2.toString()));
    }
    
    /**
     * @return If it's repeat mode, the background image is drawn multiple if it's to small.
     */
    public boolean isRepeatMode() {
        return repeatMode;
    }
    
    /**
     * @return True if the zoom shall be ignored, makes only sense if <code>isRepeatMode()</code> is also true.
     */
    public boolean isIgnoreZoom() {
        return ignoreZoom;
    }
    
    /**
     * @return The zoom factor for this background.
     */
    public int getZoom() {
        return zoom;
    }

}
