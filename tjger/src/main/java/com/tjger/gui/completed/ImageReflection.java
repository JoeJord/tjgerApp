package com.tjger.gui.completed;

/**
 * Holds the reflection information for drawing a reflection for an image on the game panel.
 * 
 * @author hagru
 */
public class ImageReflection implements ImageEffect {
    
    /**
     * The percent of the image that shall be drawn, must be > 0.0 and <= 1.0.
     */
    private float imageHeight = 0.5f;
    /**
     * The starting alpha value.
     */
    private float alphaStart = 0.5f;
    /**
     * The ending alpha value, not intended to be set from extern.
     */
    private float alphaEnd = 0.0f;
    
    /**
     * The vertical gap between the original image and the reflection.
     */
    private int gap = 0;

    /**
     * Create the default reflection.
     */
    public ImageReflection() {
        super();
    }
    
    /**
     * Specify the gap between original image and reflection.
     * 
     * @param gap the gap between original and vertical image, must not be lower than 0
     */
    public ImageReflection(int gap) {
        if (gap<0) {
            throw new IllegalArgumentException("Gap must be greater than 0!");
        }
        this.gap = gap;
    }
    
    /**
     * Specify image height, the starting alpha value and the gap between original and reflection image.
     * 
     * @param gap the gap between original and vertical image, must not be lower than 0
     * @param imageHeight the percentage of the part of the image to be drawn, must be greater {@code 0.0} and lower or equal {@code 1.0} 
     * @param alphaStart the starting alpha value must be between {@code 0.0} and {@code 1.0}
     */
    public ImageReflection(int gap, float imageHeight, float alphaStart) {
        this(gap);
        if (alphaStart<=0.0f || alphaStart>1.0f) {
            throw new IllegalArgumentException("Height value must be greater 0.0 and lower or equal 1.0!");
        }
        if (alphaStart<0.0f || alphaStart>1.0f) {
            throw new IllegalArgumentException("Alpha value must be between 0.0 and 1.0!");
        }
        this.imageHeight = imageHeight;
        this.alphaStart = alphaStart;
    }
    
    /**
     * Returns the percentage of the image height to be drawn.
     * 
     * @return the percentage of the image height to be drawn
     */
    public float getImageHeight() {
        return imageHeight;
    }

    /**
     * Returns the starting alpha value.
     * 
     * @return the starting alpha value
     */
    public float getAlphaStart() {
        return alphaStart;
    }

    /**
     * Returns the ending alpha value.
     * 
     * @return the ending alpha value
     */
    public float getAlphaEnd() {
        return alphaEnd;
    }
    
    /**
     * Returns the vertical gap between the original image and the reflection.
     * 
     * @return the vertical gap between the original image and the reflection
     */
    public int getGap() {
        return gap;
    }

}
