package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Close the current game.
 * 
 * @author hagru
 */
public class TjgerGameCloseAction extends AbstractTjgerMenuAction {
    
    public TjgerGameCloseAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainFrame().onGameClose();
    }
}
