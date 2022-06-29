package com.tjger.gui.internal;

import com.tjger.R;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.hagru.hgbase.HGBaseActivity;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.HGBaseWelcome;

/**
 * Draws the welcome screen for tjger applications.
 * 
 * @author hagru
 */
public class TjgerWelcome extends HGBaseWelcome {

	protected TjgerWelcome() {
		super();
	}

    /**
     * Returns a dialog to be displayed as welcome/splash screen.
     * 
     * @param activity the main activity that will start the welcome dialog, must not be null
     * @return the welcome dialog (splash screen), showing the tjger logo if no application image is set
     */
    public static Dialog createDialog(HGBaseActivity activity) {
    	final int welcomeImageId = HGBaseResources.getResourceIdByName("welcome", HGBaseResources.DRAWABLE);
    	final int titleBarHeight = getTitleBarHeight(activity);
    	final boolean isLandscape = HGBaseGuiTools.isScreenLandscape(activity);
    	boolean blackOnWhite = true;
    	Dialog welcomeDialog = createPlainDialog(activity, titleBarHeight);
        LinearLayout frame = createMainLayout(activity);
        LinearLayout tjgerFrame = frame;
        boolean showWelcomeImage = (welcomeImageId > 0);
        boolean showTjgerImage = true;
        Bitmap wBitmap = null; 
        if (showWelcomeImage) {
        	wBitmap = HGBaseGuiTools.loadImage(welcomeImageId);
            if (isLandscape && wBitmap.getWidth() > wBitmap.getHeight()*1.5 
            		|| !isLandscape && wBitmap.getHeight() > wBitmap.getWidth()*1.5) {
                showTjgerImage = false;
            }          
        }
    	int imageHeight = HGBaseGuiTools.getScreenSize(activity).y - TEXT_SIZE * 4 - titleBarHeight;
    	ImageView welcomeImageView;
    	if (showWelcomeImage) {
    		int tjgerImageSize = (isLandscape) ? (int) (imageHeight * (wBitmap.getHeight() / (double) wBitmap.getWidth())) 
    										   : imageHeight / 3;
    		if (!isLandscape) {
    		    imageHeight = imageHeight - tjgerImageSize;
    		}
    		welcomeImageView = createImageView(activity, welcomeImageId, imageHeight);
            welcomeImageView.setBackgroundColor(Color.WHITE);
            if (showTjgerImage && isLandscape) {
                frame = HGBaseGuiTools.createLinearLayout(activity, true);
            	tjgerFrame.addView(frame);
            }
            if (!isLandscape) {
            	frame.addView(welcomeImageView);
            }
            if (showTjgerImage) {
        		ImageView tjgermageView = createImageView(activity, R.drawable.tjger_together, isLandscape, tjgerImageSize);
        		frame.addView(tjgermageView);
            } else {
            	blackOnWhite = false;
            	welcomeImageView.setBackgroundColor(Color.BLACK);
            }
            if (isLandscape) {
            	frame.addView(welcomeImageView);
            }
    	} else {
    		welcomeImageView = createImageView(activity, R.drawable.tjger_alone, imageHeight);
        	frame.addView(welcomeImageView);
    	}
        TextView appInfoText = createAppInfoView(activity, titleBarHeight, blackOnWhite);
    	tjgerFrame.addView(appInfoText);
    	welcomeDialog.setContentView(tjgerFrame);
    	return welcomeDialog;
    }
	
}
