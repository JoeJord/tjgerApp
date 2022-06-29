package com.tjger.gui.completed;

/**
 * Holds the shadow information for drawing a shadow for an image on the game panel.
 * 
 * @author hagru
 */
public class ImageShadow implements ImageEffect {
    
    private int shadowX = 5;
    private int shadowY = 5;
    private float alpha = 0.5f;

    /**
     * Create the default shadow.
     */
    public ImageShadow() {
        super();
    }
    
    /**
     * Specify the x and y distance of the shadow.
     * 
     * @param x the x distance.
     * @param y the y distance.
     */
    public ImageShadow(int x, int y) {
        this.shadowX = x;
        this.shadowY = y;
    }

    /**
     * Specify the x and y distance of the shadow as well as the alpha value.
     * 
     * @param x the x distance.
     * @param y the y distance.
     * @param alpha the alpha value must be between {@code 0.0} and {@code 1.0}.
     */
    public ImageShadow(int x, int y, float alpha) {
        this(x, y);
        if (alpha<0.0f || alpha>1.0f) {
            throw new IllegalArgumentException("Alpha value must be between 0.0 and 1.0!");
        }
        this.alpha = alpha;
    }

    /**
     * @return the x distance of the shadow.
     */
    public int getShadowX() {
        return shadowX;
    }

    /**
     * @return the y distance of the shadow.
     */
    public int getShadowY() {
        return shadowY;
    }

    /**
     * @return the alpha value of the shadow (between {@code 0.0} and {@code 1.0}).
     */
    public float getAlpha() {
        return alpha;
    }

}
