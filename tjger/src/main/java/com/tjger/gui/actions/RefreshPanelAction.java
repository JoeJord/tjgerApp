package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Refresh the main panel.
 * 
 * @author hagru
 */
public class RefreshPanelAction extends AbstractTjgerMenuAction {
    
    public RefreshPanelAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainMenu().refreshMainPanel();
    }

}
