package com.tjger.gui.actions;

import com.tjger.MainFrame;
import com.tjger.gui.internal.TjgerAboutDlg;

import android.view.MenuItem;

/**
 * Shows the tjger dialog.
 * 
 * @author hagru
 */
public class ShowTjgerDlgAction extends AbstractTjgerMenuAction {
    
    public ShowTjgerDlgAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        TjgerAboutDlg.show(getMainFrame());
    }

}
