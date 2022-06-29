package com.tjger;

import com.tjger.game.completed.GameConfig;

import android.view.Gravity;
import android.widget.TextView;
import at.hagru.hgbase.gui.HGBaseStatusBar;

/**
 * The tjger status bar.<p>
 * It has a field for displaying the current zoom.
 *
 * @author hagru
 */
public abstract class MainStatusBar extends HGBaseStatusBar {

    final private MainFrame mainFrame;
    private int zoomIndex;

    public MainStatusBar(MainFrame frame, int[] panelWidth) {
        super(frame, panelWidth);
        this.mainFrame = frame;
        this.zoomIndex = -1;
    }

    /**
     * @return The main frame object.
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * Actualize the text of the status bar. 
     */
    public abstract void actualizeText();

    /**
     * If there is a zoom panel, the zoom value displayed there is
     * set to the current one.
     */
    public void actualizeZoomValue() {
        if (zoomIndex != -1) {
            MainMenu menu = mainFrame.getMainMenu();
			int zoom = (menu != null) ? menu.getZoom() : GameConfig.getInstance().getActiveZoom();
			setText(zoomIndex, zoom + "%  ");
        }
    }

    /**
     * Makes the panel with the given index to a zoom panel.
     * Set only one zoom panel for a status bar.
     *
     * @param index Index of the panel, that shall be the zoom panel.
     */
    public void setZoomPanel(int index) {
        TextView lbZoom = getLabel(index);
        if (lbZoom != null) {
            zoomIndex = index;
            lbZoom.setGravity(Gravity.END);
            actualizeZoomValue();
        }
    }
}
