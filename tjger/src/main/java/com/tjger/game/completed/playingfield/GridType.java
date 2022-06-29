package com.tjger.game.completed.playingfield;

/**
 * The kind of grid used for the playing field.
 *
 * @author hagru
 */
public enum GridType {

    YES, 
    NO,
    MIXED;

    /**
     * @return true if a fixed grid is used for the playing field
     */
    public boolean isYes() {
        return YES.equals(this);
    }
    
    /**
     * @return true if not grid is used for the playing field
     */
    public boolean isNo() {
        return NO.equals(this);
    }
    
    /**
     * @return true if various grids or no grid have been used when creating field and grid information cannot be used
     */
    public boolean isMixed() {
        return MIXED.equals(this);
    }
}
