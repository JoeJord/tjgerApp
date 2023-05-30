package com.tjger.gui.actions;

import android.view.MenuItem;

import com.tjger.MainFrame;

/**
 * The action to show the credits dialog.
 */
public class ShowCreditsDlgAction extends AbstractTjgerMenuAction {
    /**
     * Constructs a new instance.
     *
     * @param mainFrame The main frame.
     */
    public ShowCreditsDlgAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        getMainMenu().showCreditsDlg();
    }
}
