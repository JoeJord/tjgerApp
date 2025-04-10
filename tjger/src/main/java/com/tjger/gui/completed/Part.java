package com.tjger.gui.completed;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tjger.game.completed.GameManager;
import com.tjger.gui.PartSorter;

import at.hagru.hgbase.lib.HGBaseItem;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * A graphical game part, like a board or cards.
 *
 * @author hagru
 */
public class Part implements HGBaseItem, Comparable<Part> {

    private final String partType;
    private final String name;
    private final Bitmap image;
    private final boolean hidden;
    /**
     * Flag if this part is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    private final boolean proTeaser;
    private ImageShadow shadow;
    private ImageReflection reflection;

    public Part(String partType, String name, Bitmap image, boolean hidden, boolean proTeaser) {
        super();
        this.partType = partType;
        this.name = name;
        this.image = image;
        this.hidden = hidden;
        this.proTeaser = proTeaser;
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

    @Override
    public String getId() {
        return getName();
    }

    @NonNull
    @Override
    public String toString() {
        return HGBaseText.getText(getName());
    }

    @Override
    public int compareTo(Part p2) {
        if (p2 == null) {
            return 1;
        }
        PartSorter sorter = GameManager.getInstance().getPartSorter(getType());
        if (sorter != null) {
            return sorter.compareParts(this, p2);
        } else {
            return getName().compareToIgnoreCase(p2.getName());
        }
    }

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

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Returns {@code true} if this part is only available in the pro version but should be shown in the free version as teaser for the pro version.
     *
     * @return {@code true} if this part is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    public boolean isProTeaser() {
        return proTeaser;
    }
}
