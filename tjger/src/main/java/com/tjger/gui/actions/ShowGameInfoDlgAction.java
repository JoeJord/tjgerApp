package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Shows game info dialog.
 * 
 * @author hagru
 */
public class ShowGameInfoDlgAction extends AbstractTjgerMenuAction {
    
    public ShowGameInfoDlgAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainMenu().showGameInfoDialog();
    }

}
