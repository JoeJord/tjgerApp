package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Shows game hints dialog.
 * 
 * @author hagru
 */
public class ShowGameHintsDlgAction extends AbstractTjgerMenuAction {
    
    public ShowGameHintsDlgAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    /* (non-Javadoc)
     * @see hgb.gui.menu.IMenuAction#perform(java.lang.String, java.awt.event.ActionEvent)
     */
    @Override
    public void perform(int id, MenuItem item) {
        getMainMenu().showGameHintsDialog();
    }

}
