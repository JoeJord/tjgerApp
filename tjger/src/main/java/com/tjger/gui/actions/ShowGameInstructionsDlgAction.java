package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Shows game instructions dialog.
 * 
 * @author hagru
 */
public class ShowGameInstructionsDlgAction extends AbstractTjgerMenuAction {
    
    public ShowGameInstructionsDlgAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainMenu().showGameInstructionsDialog();
    }

}
