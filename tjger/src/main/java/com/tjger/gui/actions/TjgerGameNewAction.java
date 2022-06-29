package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Start a new game.
 * 
 * @author hagru
 */
public class TjgerGameNewAction extends AbstractTjgerMenuAction {
    
    public TjgerGameNewAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainFrame().onGameNew();
    }
}
