package com.tjger.gui.completed;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.hagru.hgbase.gui.BitmapCanvas;
import at.hagru.hgbase.lib.HGBaseTools;


/**
 * A set with a type of parts.
 *
 * @author hagru
 */
public class PartSet extends Part {

    final private Map<String, List<ColorValuePart>> partMap = new LinkedHashMap<>();

    public PartSet(String partType, String name, boolean hidden, boolean proTeaser) {
        super(partType, name, null, hidden, proTeaser);
    }

    /* (non-Javadoc)
     * @see tjger.gui.completed.Part#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        return (HGBaseTools.equalClass(this, o2) && this.toString().equals(o2.toString()));
    }

    /**
     * Adds a new part.
     *
     * @param newPart New part to add to this card set.
     */
    public void addPart(ColorValuePart newPart) {
        List<ColorValuePart> partList = partMap.get(newPart.getColor());
        if (partList == null) {
            partList = new ArrayList<>();
            partMap.put(newPart.getColor(), partList);
        }
        partList.add(newPart);
    }

    /**
     * @return All colors of the card set.
     */
    public String[] getColors() {
        return HGBaseTools.toStringArray(partMap.keySet());
    }

    /**
     * @return All parts of a set as ArrayList.
     */
    public List<ColorValuePart> getParts() {
        List<ColorValuePart> listAll = new ArrayList<>();
        for (List<ColorValuePart> listColor : partMap.values()) {
            for (ColorValuePart part : listColor) {
                listAll.add(part);
            }
        }
        return listAll;
    }

    /**
     * @param color A color of the part set.
     * @return All parts with the given color.
     */
    public List<ColorValuePart> getParts(String color) {
        List<ColorValuePart> list = partMap.get(color);
        return (list == null) ? new ArrayList<ColorValuePart>() : list;
    }

    /**
     * @param color    A color of the part set.
     * @param sequence A value of a part.
     * @return The part with the given color and value or null if it was not found.
     */
    public ColorValuePart getPart(String color, int sequence) {
        List<ColorValuePart> list = partMap.get(color);
        if (list != null) {
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                ColorValuePart c = list.get(i);
                if (c.getSequence() == sequence) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * @param color         A color of the part set.
     * @param sequenceStart The starting sequence.
     * @param sequenceEnd   The ending sequence.
     * @return All parts from the starting to the ending sequence (both included).
     */
    public List<ColorValuePart> getParts(String color, int sequenceStart, int sequenceEnd) {
        List<ColorValuePart> sequenceList = new ArrayList<>();
        List<ColorValuePart> list = partMap.get(color);
        if (list != null) {
            for (ColorValuePart part : list) {
                int sequence = part.getSequence();
                if (sequence >= sequenceStart && sequence <= sequenceEnd) {
                    sequenceList.add(part);
                }
            }
        }
        return sequenceList;
    }

    /**
     * Returns the first sequence value or -1 if there exists no values.
     *
     * @param color the color to get the sequence value for
     * @return the first sequence value or -1
     */
    public int getStartSequence(String color) {
        List<ColorValuePart> list = partMap.get(color);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getSequence();
        } else {
            return -1;
        }
    }

    /**
     * Returns the last sequence value or -1 if there exists no values.
     *
     * @param color the color to get the sequence value for
     * @return the last sequence value or -1
     */
    public int getEndSequence(String color) {
        List<ColorValuePart> list = partMap.get(color);
        if (list != null && !list.isEmpty()) {
            return list.get(list.size() - 1).getSequence();
        } else {
            return -1;
        }
    }

    /* (non-Javadoc)
     * @see tjger.gui.Part#getImage()
     */
    @Override
    public Bitmap getImage() {
        // create a new image with the first image of every colour
        int distance = 20;
        String[] color = getColors();
        Bitmap[] imgList = new Bitmap[color.length];
        int width = 0;
        int height = 0;
        for (int i = 0; i < imgList.length; i++) {
            Part[] parts = getParts(color[i]).toArray(new Part[0]);
            Arrays.sort(parts);
            if (parts.length > 0) {
                Bitmap img = parts[0].getImage();
                imgList[i] = img;
                if (img != null) {
                    width = width + distance;
                    if (img.getHeight() > height) {
                        height = img.getHeight();
                    }
                    if (i == imgList.length - 1) {
                        width = width + img.getWidth();
                    }
                }
            }
        }
        BitmapCanvas imgBuf = new BitmapCanvas(width, height);
        int xpos = 0;
        for (int i = 0; i < imgList.length; i++) {
            if (imgList[i] != null) {
                imgBuf.drawBitmap(imgList[i], xpos, 0, null);
                xpos = xpos + distance;
            }
        }
        return imgBuf.getBitmap();
    }
}
