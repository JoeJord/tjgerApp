package com.tjger.gui.completed;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.gui.PartSorter;

import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A part that has a color and a value.
 *
 * @author hagru
 */
public class ColorValuePart extends Part {

    private final String color;
    private final int sequence;
    private final int value;
    protected PartSet partSet;

    public ColorValuePart(PartSet partSet, String partType, String color, int sequence, int value, Bitmap image, boolean proTeaser, String productId) {
        super(partType, partSet.getName() + "." + color + "." + sequence, image, false, proTeaser, productId);
        this.partSet = partSet;
        this.color = color;
        this.sequence = sequence;
        this.value = value;
    }

    /**
     * @return The part set to which this part belongs.
     */
    public PartSet getPartSet() {
        return partSet;
    }

    /**
     * @return The "color" of this part.
     */
    public String getColor() {
        return color;
    }

    /**
     * @return The sequence of this part, often equals to the value. The sequence is unique.
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * @return The value of this part.
     */
    public int getValue() {
        return value;
    }


    @NonNull
    @Override
    public String toString() {
        return HGBaseText.getText(getPartSet().getName()) + " " + getIntraId();
    }

    /**
     * @return The internal id that ignores the part set.
     */
    protected String getIntraId() {
        return getColor() + "-" + getSequence();
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 != null && o2.getClass().equals(this.getClass())) {
            String intraId2 = ((ColorValuePart) o2).getIntraId();
            return getIntraId().equals(intraId2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getIntraId().hashCode();
    }

    @Override
    public int compareTo(GameElement p2) {
        if (p2 instanceof ColorValuePart) {
            ColorValuePart c2 = (ColorValuePart) p2;
            // look for the way of ordering
            // Use the part sorter if there is one.
            PartSorter sorter = GameManager.getInstance().getPartSorter(getType());
            if (sorter != null) {
                return sorter.compareParts(this, c2);
            }
            int orderMode = GameManager.getInstance().getGameConfig().getOrderby(getPartSet().getType());
            if (orderMode == GameConfig.ORDERBY_NONE) {
                return 0;
            }
            if (orderMode == GameConfig.ORDERBY_VALUE) {
                return compareByValue(c2);
            } else if (orderMode == GameConfig.ORDERBY_COLOR) {
                return compareByColor(c2);
            } else {
                return -1;
            }
        } else {
            return super.compareTo(p2);
        }
    }

    /**
     * Compares this color value part with the specified by the value.
     *
     * @param c2 The other color value part.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    private int compareByValue(ColorValuePart c2) {
        // sort value and color
        int compare = this.getPartSet().getName().compareToIgnoreCase(c2.getPartSet().getName());
        if (compare != 0) {
            return compare;
        }
        String[] colors = getPartSet().getColors();
        compare = Integer.compare(this.getValue(), c2.getValue());
        if (compare != 0) {
            return compare;
        }
        compare = Integer.compare(this.getSequence(), c2.getSequence());
        if (compare != 0) {
            return compare;
        }
        return HGBaseTools.getIndexOf(colors, this.getColor())
                - HGBaseTools.getIndexOf(colors, c2.getColor());
    }

    /**
     * Compares this color value part with the specified by the color.
     *
     * @param c2 The other color value part.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    private int compareByColor(ColorValuePart c2) {
        // sort color and value
        int compare = this.getPartSet().getName().compareToIgnoreCase(c2.getPartSet().getName());
        if (compare != 0) {
            return compare;
        }
        String[] colors = getPartSet().getColors();
        compare = HGBaseTools.getIndexOf(colors, this.getColor())
                - HGBaseTools.getIndexOf(colors, c2.getColor());
        if (compare != 0) {
            return compare;
        }
        compare = Integer.compare(this.getValue(), c2.getValue());
        if (compare != 0) {
            return compare;
        }
        return Integer.compare(this.getSequence(), c2.getSequence());
    }
}
