package com.tjger.gui.completed;

import com.tjger.game.completed.GameManager;
import com.tjger.gui.PartSorter;

import android.graphics.Bitmap;
import at.hagru.hgbase.lib.HGBaseItem;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * A graphical game part, like a board or cards.
 *
 * @author hagru
 */
public class Part implements HGBaseItem, Comparable<Part> {
    
    final private String partType;
    final private String name;
    final private Bitmap image;
    final private boolean hidden;
    private ImageShadow shadow;
    private ImageReflection reflection;

    public Part(String partType, String name, Bitmap image, boolean hidden) {
        super();
        this.partType = partType;
        this.name = name;
        this.image = image;
        this.hidden = hidden;
    }
    
    /**
     * @return The image for this game part.
     */
    public Bitmap getImage() {
        return image;
    }
    /**
     * @return The name of this game part.
     */
    public String getName() {
        return name;
    }
    
    /**
     * If the part is hidden, it will not be displayed in the settings dialog
     * and can not be an active part.
     * 
     * @return True if this part is hidden.
     */
    public boolean isHidden() {
        return hidden;
    }
    
    /**
     * If a part has a shadow, then this shadow will be painted on the game panel automatically.
     * 
     * @return the shadow object, can be null.
     */
    public ImageShadow getShadow() {
        return shadow;
    }

    /**
     * Set the shadow for a part. This is done automatically when reading the configuration.
     * This method allows overriding the specified shadow.
     * 
     * @param shadow the shadow for the part, can be null.
     */
    public void setShadow(ImageShadow shadow) {
        this.shadow = shadow;
    }

    /**
     * If a part has a reflection, then this reflection will be painted on the game panel automatically.
     * 
     * @return the reflection object, can be null.
     */
    public ImageReflection getReflection() {
        return reflection;
    }

    /**
     * Set the reflection for a part. This is done automatically when reading the configuration.
     * This method allows overriding the specified reflection.
     * 
     * @param reflection the reflection for the part, can be null.
     */
    public void setReflection(ImageReflection reflection) {
        this.reflection = reflection;
    }

    /* (non-Javadoc)
     * @see hgb.gui.HGBaseItem#getId()
     */
    @Override
    public String getId() {
        return getName();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return HGBaseText.getText(getName());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Part p2) {
        if (p2==null) {
            return 1;
        }
        PartSorter sorter = GameManager.getInstance().getPartSorter(getType());
        if (sorter!=null) {
        	return sorter.compareParts(this, p2);
        } else {
            return getName().compareToIgnoreCase(p2.getName());
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        return (o2 instanceof Part && this.toString().equals(o2.toString()));
    }
    
    /**
     * @return The type of this game part.
     */
    public String getType() {
        return this.partType;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
