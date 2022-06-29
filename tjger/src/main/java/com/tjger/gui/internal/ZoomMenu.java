package com.tjger.gui.internal;

import com.tjger.MainFrame;
import com.tjger.MainStatusBar;
import com.tjger.game.completed.GameConfig;
import com.tjger.gui.GamePanel;

import at.hagru.hgbase.android.awt.Rectangle;
import at.hagru.hgbase.android.view.OnTouchZoomProvider;
import at.hagru.hgbase.gui.HGBaseStatusBar;

/**
 * The zoom menu implements all zoom-related actions.
 * 
 * @author hagru
 */
public class ZoomMenu {
    
    private static final int NO_ZOOM = 100;
	
    private int currentZoom = NO_ZOOM;
	private boolean scrollingPossible;    

    public ZoomMenu() {
    	super();
    }
    
    /**
     * @return The active zoom.
     */
    public int getZoom() {
        return currentZoom;
    }
    
    /**
     * @return the main frame
     */
    protected MainFrame getMainFrame() {
    	return MainFrame.getInstance();
    }

    /**
     * @param newZoom The new zoom value to set.
     */
    public void setZoom(int newZoom) {
        this.currentZoom = newZoom;
        getMainFrame().getMainPanel().refresh();
        HGBaseStatusBar status = getMainFrame().getStatusBar();
        if (status instanceof MainStatusBar) {
            ((MainStatusBar) status).actualizeZoomValue();  
        }
        scrollingPossible = (currentZoom > getFitWindowZoom());
        OnTouchZoomProvider zoomProvider = getZoomProvider();
        if (zoomProvider != null) {
            zoomProvider.setZoomFactor(newZoom);
        }
    }
    
    /**
     * @return true if scrolling is possible at the moment, i.e., the game panel is larger than the screen
     */
    public boolean isScrollingPossible() {
    	return scrollingPossible;
    }    
    
    /**
     * Sets the zoom, that the game field fits into the window.
     */
    public void onFitWindow() {
        int oldZoom = getZoom();
        int newZoom = getFitWindowZoom();
        if (oldZoom != newZoom) {
            setZoom(newZoom);
            OnTouchZoomProvider zoomProvider = getZoomProvider();
            if (zoomProvider != null) {
                zoomProvider.setMinZoom(newZoom);
            }
        }
    }

	/**
	 * @return the zoom provider of the game panel
	 */
	protected OnTouchZoomProvider getZoomProvider() {
		GamePanel panel = getMainFrame().getGamePanel();
		return (panel == null) ? null : panel.getZoomProvider();
	} 
    
    /**
     * @return The zoom that the game field fit's into window.
     */
    public int getFitWindowZoom() {
        int newZoom = getBestZoomHeight();
        int zoomW = getBestZoomWidth();
        if (zoomW < newZoom) {
            newZoom = zoomW;
        }
        return newZoom;
    }
    
    /**
     * @return The best zoom width.
     */
    public int getBestZoomWidth() {
        GameConfig config = GameConfig.getInstance();       
        Rectangle viewport = getMainFrame().getMainPanel().getViewport();
        if (viewport == null) {
        	return NO_ZOOM;
        } else {
	        int zoomW = (int) (100 * calculateRelative(viewport.getWidth(), config.getFieldWidth()));
	        int zoomH = (int) (100 * calculateRelative(viewport.getHeight(), config.getFieldHeight()));
	        if (zoomH<zoomW) {
	            return (int) (100 * (viewport.getWidth() / config.getFieldWidth()));
	        }
	        return zoomW;
        }
    }
    
    /**
     * @return The best zoom height.
     */
    public int getBestZoomHeight() {
        GameConfig config = GameConfig.getInstance();       
        Rectangle viewport = getMainFrame().getMainPanel().getViewport();
        if (viewport == null) {
        	return NO_ZOOM;
        } else {
	        int zoomW = (int)(100 * calculateRelative(viewport.getWidth(), config.getFieldWidth()));
	        int zoomH = (int)(100 * calculateRelative(viewport.getHeight(), config.getFieldHeight()));
	        if (zoomW < zoomH) {
	            return (int) (100 * (viewport.getHeight() / config.getFieldHeight()));
	        }
	        return zoomH;
        }
    }
    
    /**
     * Calculates the relative value from the given ones. Avoids division by zero.
     * 
     * @param num1 the first factor
     * @param num2 the second factor
     * @return the relative or 1 if num2 is 0
     */
    private double calculateRelative(double num1, double num2) {
        return (num2 == 0)? 1 : num1 / num2;
        
    }   

}
