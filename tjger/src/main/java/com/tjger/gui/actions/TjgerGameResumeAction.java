package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Resume the last (automatically) saved game.
 * 
 * @author hagru
 */
public class TjgerGameResumeAction extends AbstractTjgerMenuAction {
    
    public TjgerGameResumeAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainFrame().onGameResume();
    }
}
