package com.tjger.gui.completed;

import android.graphics.Bitmap;

import com.tjger.game.completed.GameManager;
import com.tjger.gui.PartSorter;

/**
 * A graphical game part, like a board or cards.
 *
 * @author hagru
 */
public class Part extends GameElement {
    private final Bitmap image;
    private ImageShadow shadow;
    private ImageReflection reflection;

    public Part(String partType, String name, Bitmap image, boolean hidden, boolean proTeaser, String productId) {
        super(partType, name, hidden, proTeaser, productId);
        this.image = image;
    }

    /**
     * @return The image for this game part.
     */
    public Bitmap getImage() {
        return image;
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
    public int compareTo(GameElement p2) {
        if (p2 == null) {
            return 1;
        }
        if (!(p2 instanceof Part)) {
            return super.compareTo(p2);
        }
        PartSorter sorter = GameManager.getInstance().getPartSorter(getType());
        if (sorter != null) {
            return sorter.compareParts(this, (Part) p2);
        } else {
            return getName().compareToIgnoreCase(p2.getName());
        }
    }

    @Override
    public boolean equals(Object o2) {
        return (o2 instanceof Part && this.toString().equals(o2.toString()));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
