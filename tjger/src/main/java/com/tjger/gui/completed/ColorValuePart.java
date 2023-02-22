package com.tjger.gui.completed;

import android.graphics.Bitmap;

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

    final private String color;
    final private int sequence;
    final private int value;
    protected PartSet partSet;

    public ColorValuePart(PartSet partSet, String partType, String color, int sequence, int value, Bitmap image, boolean proTeaser) {
        super(partType, partSet.getName() + "." + color + "." + sequence, image, false, proTeaser);
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


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        if (o2 != null && o2.getClass().equals(this.getClass())) {
            String intraId2 = ((ColorValuePart) o2).getIntraId();
            return getIntraId().equals(intraId2);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getIntraId().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Part p2) {
        if (p2 instanceof ColorValuePart) {
            ColorValuePart c2 = (ColorValuePart) p2;
            // look for the way of ordering
            // Use the part sorter if there is one.
            PartSorter sorter = GameManager.getInstance().getPartSorter(getType());
            if (sorter != null) {
                return sorter.compareParts(this, p2);
            }
            int orderMode = GameManager.getInstance().getGameConfig().getOrderby(getPartSet().getType());
            if (orderMode == GameConfig.ORDERBY_NONE) {
                return 0;
            }
            if (orderMode == GameConfig.ORDERBY_VALUE) {
                // sort value and color
                int compare = this.getPartSet().getName().compareToIgnoreCase(c2.getPartSet().getName());
                if (compare != 0) {
                    return compare;
                }
                String[] colors = getPartSet().getColors();
                compare = Integer.valueOf(this.getValue()).compareTo(Integer.valueOf(c2.getValue()));
                if (compare != 0) {
                    return compare;
                }
                compare = Integer.valueOf(this.getSequence()).compareTo(Integer.valueOf(c2.getSequence()));
                if (compare != 0) {
                    return compare;
                }
                return HGBaseTools.getIndexOf(colors, this.getColor()) - HGBaseTools.getIndexOf(colors, c2.getColor());
            } else if (orderMode == GameConfig.ORDERBY_COLOR) {
                // sort color and value
                int compare = this.getPartSet().getName().compareToIgnoreCase(c2.getPartSet().getName());
                if (compare != 0) {
                    return compare;
                }
                String[] colors = getPartSet().getColors();
                compare = HGBaseTools.getIndexOf(colors, this.getColor()) - HGBaseTools.getIndexOf(colors, c2.getColor());
                if (compare != 0) {
                    return compare;
                }
                compare = Integer.valueOf(this.getValue()).compareTo(Integer.valueOf(c2.getValue()));
                if (compare != 0) {
                    return compare;
                }
                return Integer.valueOf(this.getSequence()).compareTo(Integer.valueOf(c2.getSequence()));
            } else {
                return -1;
            }
        } else {
            return super.compareTo(p2);
        }
    }

}
