package com.tjger.lib;

import android.graphics.Bitmap;
import android.widget.TextView;
import at.hagru.hgbase.gui.HGBaseGuiTools;

/**
 * Some utilties for Gui operations.
 *
 * @author hagru
 */
public class ImageUtil {

	private ImageUtil() {
		super();
	}

    /**
     * Compares two images by their content (formerly done by their file name).
     *
     * @param img1 First image.
     * @param img2 Second image.
     * @return True if it's the same image.
     */
    public static boolean isEqualImage(Bitmap img1, Bitmap img2) {
        if (img1!=null && img2!=null) {
            return img1.equals(img2);
        }
        return false;
    }

    /**
     * @param imageList A list with images.
     * @return The height of the highest image.
     */
    public static int getMaxImageHeight(Bitmap[] imageList) {
        int max = 0;
        for (int i=0; i<imageList.length; i++) {
            if (imageList[i]!=null && imageList[i].getHeight()>max) {
                max = imageList[i].getHeight();
            }
        }
        return max;
    }

    /**
     * @param imageList A list with images.
     * @return The width of the widest image.
     */
    public static int getMaxImageWidth(Bitmap[] imageList) {
        int max = 0;
        for (int i=0; i<imageList.length; i++) {
            if (imageList[i]!=null && imageList[i].getWidth()>max) {
                max = imageList[i].getWidth();
            }
        }
        return max;
    }

    /**
     * @see HGBaseGuiTools#setImageOnLabel(TextView, Bitmap)
     */
    public static void setImageOnLabel(TextView label, Bitmap image) {
    	HGBaseGuiTools.setImageOnLabel(label, image);
    }

    /**
     * @see HGBaseGuiTools#setImageOnLabel(TextView, Bitmap, int, int)
     */
    public static void setImageOnLabel(TextView label, Bitmap image, int width, int height) {
        HGBaseGuiTools.setImageOnLabel(label, image, width, height);
    }

}
